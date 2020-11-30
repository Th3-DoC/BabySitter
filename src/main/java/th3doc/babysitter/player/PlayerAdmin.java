package th3doc.babysitter.player;

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
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.PermType;
import th3doc.babysitter.player.data.States;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerAdmin {

    //CONSTRUCTOR
    private Main main;
    public PlayerAdmin(Main main) { this.main = main; }

    //ADMIN LIST
    private List<String> adminList = new ArrayList<>();
    protected List<String> list() { return adminList; }

    //INITIALIZE PLAYER
    public void initialize(Player p)
    {
        Group group = main.getLuckPerms().getGroupManager().getGroup(main.player().getPermGroup(p));
        //CHECK GROUP IS VALID
        if (group != null)
        {
            //CHECK PLAYER IS ADMIN RANK, ELSE RETURN;
            if (main.getConfig().getStringList(Config._adminRanks.txt)
                    .contains(main.player().getPermGroup(p))
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
                ConfigHandler config =
                        new ConfigHandler(main
                                , Config._playerData.txt
                                , p.getUniqueId().toString()
                                , Config._adminConfig.txt);

                //CHECK ADMIN CONFIG VALUES, CREATE IF EMPTY
                //STATES
                if (!config.getConfig().isSet(Config._states.txt))
                {
                    config.getConfig().createSection(Config._states.txt);
                    ConfigurationSection states = config.getConfig().getConfigurationSection(Config._states.txt);
                    //ADMIN STATE
                    if (!states.isSet(Config._adminState.txt))
                    {
                        states.createSection(Config._adminState.txt);
                        states.set(Config._adminState.txt, "false");
                    }
                    //VANISH STATE
                    if (!states.isSet(Config._vanishState.txt))
                    {
                        states.createSection(Config._vanishState.txt);
                        states.set(Config._vanishState.txt, "false");
                    }
                    //FLY STATE
                    if (!states.isSet(Config._flyState.txt))
                    {
                        states.createSection(Config._flyState.txt);
                        states.set(Config._flyState.txt, "false");
                    }
                    //SAVE CONFIG
                    config.save();
                }
                //INITIALIZE VARIABLES
                states.put(p.getName(), config.getConfig().getConfigurationSection(Config._states.txt));
                /**
                 * END LOAD/CREATE CONFIG
                 */
                /**
                 *
                 * ACTIVATE STATES WITH TRUE VALUES
                 *
                 */
                //CHECK IF SPECIAL PERMISSIONS IS TRUE ALWAYS
                if (main.getConfig().getBoolean(Config._specialPermsAlways.txt)) { setPermissions(p, true, PermType.Special); }
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

    //LUCK-PERM PERMISSIONS
    public void setPermissions(Player p, boolean boo, PermType type)
    {
        if (!p.hasPermission(Perm._permBypass.txt)) {
            ConfigurationSection permissions;
            if (type == PermType.Admin) {
                permissions = main.getConfig().getConfigurationSection(Config._adminPermissionList.txt);
            }
            else if (type == PermType.Special) {
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
                        boolean v = false;
                        boolean set = false;
                        if (!value && p.hasPermission(permission)) { continue; }
                        if (boo && value) { if (!p.hasPermission(permission)) { v = true; set = true; } }
                        if (!boo && value) { if (p.hasPermission(permission)) { set = true; } }
                        if (set) {
                            User user = main.getLuckPerms().getUserManager().getUser(p.getUniqueId());
                            Node node = Node.builder(permission).value(v).build();
                            user.data().add(node);
                            main.getLuckPerms().getUserManager().saveUser(user);
                        }
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
        if (state == States.Admin) { return states.get(pName).getBoolean(Config._adminState.txt); }
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
        if (state == States.Admin) { stateSection = Config._adminState.txt; }
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
    protected List<String> getVanishedAdmins() { return vanishedAdmins; }
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
            String message = ChatColor.YELLOW + p.getName() + Chat._fakeLogOut.txt;
            if (states.get(p.getName()).getBoolean(Config._vanishState.txt)) { message = Chat._vanishOn.txt; }
            vanishedAdmins.add(p.getName());
            setState(p, true, States.Vanish);
            new BukkitRunnable() {
                @Override
                public void run() {
                    removePlayerTabInfo(p, false);
                }
            }.runTaskLater(main, 1L);
            main.getServer().broadcastMessage(message);
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
