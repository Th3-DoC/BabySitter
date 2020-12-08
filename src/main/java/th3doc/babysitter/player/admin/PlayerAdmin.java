package th3doc.babysitter.player.admin;

import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;
import th3doc.babysitter.player.PlayerHandler;
import th3doc.babysitter.player.data.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerAdmin {

    //CONSTRUCTOR
    private final PlayerHandler player;
    private final AdminConfig config;
    public PlayerAdmin(PlayerHandler player)
    {
        this.player = player;
        this.config = new AdminConfig(player);
        //CHECK GROUP IS VALID
        if (!player.getPermGroup().equals(""))
        {
            //CHECK PLAYER IS ADMIN RANK, ELSE RETURN;
            if (player.getMain().getConfig().getStringList(Config._adminRanks.txt)
                    .contains(player.getPermGroup())
                    || p.hasPermission(Perm._permBypass.txt))
            {
                //ADD TO ADMIN LIST
                adminList.add(p.getName());

                /**
                 *
                 * LOAD/CREATE CONFIG
                 *
                 */
                //LOAD ADMIN CONFIG
                configs.put(p.getName(), new AdminConfig(main, p));
                //CHECK IF SPECIAL PERMISSIONS IS TRUE ALWAYS
                if (main.getConfig().getBoolean(Config._specialPermsAlways.txt)) { setPermissions(p, true, PlayerType.Special); }
                //CHECK ADMIN STATE
                if (states.get(p.getName()).getBoolean(Config._adminState.txt)
                        || p.hasPermission(Perm._permBypass.txt))
                {
                    //SET FLY STATE
                    if (states.get(p.getName()).getBoolean(Config._flyState.txt))
                    {
                        p.setAllowFlight(true);
                        p.setFlying(true);
                    }
                    //SET VANISH STATE
                    if (states.get(p.getName()).getBoolean(Config._vanishState.txt))
                    {
                        toggleVanish(p);
                    }
                }
            }
        }
    }

    //ADMIN LIST
    private static List<String> adminList = new ArrayList<>();
    public List<String> list() { return adminList; }

    //LUCK-PERM PERMISSIONS
    public void setPermissions(Player p, boolean boo, PlayerType type)
    {
        if (!p.hasPermission(Perm._permBypass.txt)) {
            ConfigurationSection permissions;
            if (type == PlayerType.Admin) {
                permissions = main.getConfig().getConfigurationSection(Config._adminPermissionList.txt);
            }
            else if (type == PlayerType.Special) {
                if (main.getConfig().getBoolean(Config._specialPermissions.txt)
                        && main.getConfig().getStringList(Config._specialRanks.txt)
                        .contains(main.player().getPermGroup(p)))
                {
                    permissions = main.getConfig().getConfigurationSection(Config._specialPermissionList.txt);
                }
                else { return; }
            }
            else { return; }
            //CHECK ADMIN PERMISSIONS ARE VALID
            if (permissions != null)
            {
                //ITERATE PERMISSIONS & VALUES
                for (String key : permissions.getKeys(false))
                {
                    String permission =
                            permissions.getConfigurationSection(key).get(Config._permission.txt).toString();
                    boolean value =
                            permissions.getConfigurationSection(key).getBoolean(Config._value.txt);
                    //CHECK PERMISSION IS VALID
                    if (permission != null)
                    {
                        if (!value && p.hasPermission(permission)) { continue; }
                        if (boo && value) { if (!p.hasPermission(permission)) { main.getPerms().playerAdd(p, permission); } }
                        if (!boo && value) { if (p.hasPermission(permission)) { main.getPerms().playerRemove(p, permission); } }
                    }
                }
                p.recalculatePermissions();
                p.updateCommands();
            }
        }
    }

    //STATES
    private HashMap<String, ConfigurationSection> states = new HashMap<>();
    public boolean getState(String pName, States state)
    {
        if (state == States.Babysit) { return states.get(pName).getBoolean(Config._adminState.txt); }
        if (state == States.Fly) { return states.get(pName).getBoolean(Config._flyState.txt); }
        if (state == States.Vanish) { return states.get(pName).getBoolean(Config._vanishState.txt); }
        return false;
    }
    public void setState(Player p, Boolean boo, States state)
    {
        ConfigHandler config =
                new ConfigHandler(main, Config._playerData.txt, p.getUniqueId().toString(), Config._adminConfig.txt);
        ConfigurationSection configSection = config.getConfig().getConfigurationSection(Config._states.txt);
        String stateSection = "";
        if (state == States.Babysit) { stateSection = Config._adminState.txt; }
        if (state == States.Vanish) { stateSection = Config._vanishState.txt; }
        if (state == States.Fly) { stateSection = Config._flyState.txt; }
        states.get(p.getName()).set(stateSection, boo);
        configSection.set(stateSection, boo);
        config.save();
    }

    //REMOVE PLAYER TAB INFO TRUE?FALSE
    private void removePlayerTabInfo(Player receiver, boolean boo) {
        PacketPlayOutPlayerInfo.EnumPlayerInfoAction info;
        if (!boo) { info = PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER; }
        else { info = PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER; }
        Player bukkitPlayer = Bukkit.getPlayer(receiver.getUniqueId());
        EntityPlayer[] playerNMS = new EntityPlayer[1];
        playerNMS[0] = ((CraftPlayer) bukkitPlayer).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(info, playerNMS);
        ((CraftPlayer) receiver).getHandle().playerConnection.sendPacket(packet);
    }

    //VANISH TOGGLE
    private List<String> vanishedAdmins = new ArrayList<>();
    public List<String> getVanishedAdmins() { return vanishedAdmins; }
    public void toggleVanish(Player p)
    {

        //might require we save who we hide from so were not stuck hidden next time they log in?

        //CHECK IF PLAYER IS VANISHED
        if (!vanishedAdmins.contains(p.getName()))
        {
            //ITERATE AND HIDE ADMIN FROM PLAYERS
            for (Player player : main.getServer().getOnlinePlayers())
            {
                //CHECK IF PLAYER IS AN ADMIN
                if (!adminList.contains(player.getName()))
                {
                    player.hidePlayer(main, p);
                }
            }
            vanishedAdmins.add(p.getName());
            new BukkitRunnable() {
                @Override
                public void run() {
                    removePlayerTabInfo(p, false);
                }
            }.runTaskLater(main, 1L);
            if (states.get(p.getName()).getBoolean(Config._vanishState.txt)) { p.sendMessage(Chat._vanishOn.txt); return; }
            setState(p, true, States.Vanish);
            main.getServer().broadcastMessage(ChatColor.YELLOW + p.getName() + Chat._fakeLogOut.txt);
        } else
        {
            //ITERATE AND UN-HIDE ADMIN FROM PLAYERS
            for (Player player : main.getServer().getOnlinePlayers())
            {
                player.showPlayer(main, p);
            }
            vanishedAdmins.remove(p.getName());
            setState(p, false, States.Vanish);
            removePlayerTabInfo(p, true);
            main.getServer().broadcastMessage(ChatColor.YELLOW + p.getName() + Chat._fakeLogIn.txt);
        }
    }

    //ACTIVATE SPECTATE
    public boolean forceSpectate(Player p, String[] args)
    {
        //CHECK FOR VALID ARGUMENT AND USER INPUT
        if ((!(main.getServer().getPlayer(args[0]) instanceof Player)
                || main.getServer().getPlayer(args[0]) == p
                || args.length > 1))
        {
            p.sendMessage(Chat._targetInvalid.txt);
            return false;
        } else
        {
            p.setGameMode(GameMode.SPECTATOR);
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    p.teleport(main.getServer().getPlayer(args[0]));
                    p.setSpectatorTarget(main.getServer().getPlayer(args[0]));
                    p.getSpectatorTarget().addPassenger(p);
                }
            }.runTaskLater(main, 1L);
            return true;
        }
    }

    //REMOVE ADMIN FROM MEMORY
    public void memoryDump(Player p) {
        adminList.remove(p.getName());
        vanishedAdmins.remove(p.getName());
    }
}
