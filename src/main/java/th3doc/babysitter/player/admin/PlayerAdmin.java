package th3doc.babysitter.player.admin;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.player.PlayerHandler;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.PlayerType;
import th3doc.babysitter.player.data.States;

import java.util.HashMap;

public class PlayerAdmin {
    
    //VARIABLES
    private final PlayerHandler player;
    private final AdminConfig config;
    final private GUI gui;
    
    
    //CONSTRUCTOR
    
    public PlayerAdmin(PlayerHandler player)
    {
        this.player = player;
        //ADD TO ADMIN LIST
        this.config = new AdminConfig(player);
        AdminConfig.adminList.add(player.getName());
        this.gui = new GUI(player);

        //CHECK IF SPECIAL PERMISSIONS IS TRUE ALWAYS
        if (player.getMain().defaultConfig().isSpecialPermsAlwaysActive()) { setPermissions(true, PlayerType.Special); }
        //CHECK ADMIN STATE
        if (config.getState(States.ADMIN)
            || player.getPlayer().hasPermission(Perm._permBypass.txt))
        {
            //SET FLY STATE
            if (config.getState(States.FLY))
            {
                player.getPlayer().setAllowFlight(true);
                player.getPlayer().setFlying(true);
            }
            //SET VANISH STATE
            if (config.getState(States.VANISH))
            {
                toggleVanish();
            }
        }
    }
    
    
    //GETTERS
    public AdminConfig getConfig() { return this.config; }
    public GUI gui() { return this.gui; }

    //PERMISSIONS
    public void setPermissions(boolean boo, PlayerType type)
    {
        if (!player.getPlayer().hasPermission(Perm._permBypass.txt)) {
            HashMap<String, Boolean> permissions;
            if (type == PlayerType.Admin) {
                permissions = player.getMain().defaultConfig().getAdminPermissionsList();
            }
            else if (type == PlayerType.Special) {
                if (player.getMain().defaultConfig().isSpecialPermissionsActive()
                        && player.checkPermGroup(PlayerType.Special, ""))
                {
                    permissions = player.getMain().defaultConfig().getSpecialPermissionsList();
                }
                else { return; }
            }
            else { return; }
            //ITERATE PERMISSIONS & VALUES
            for (String permission : permissions.keySet())
            {
                boolean value =
                        permissions.get(permission);
                //CHECK PERMISSION IS VALID
                if (permission != null)
                {
                    OfflinePlayer offlinePlayer = player.getPlayer();
                    if (!value && player.getPlayer().hasPermission(permission)) { continue; }
                    if (boo && value) { if (!player.getPlayer().hasPermission(permission))
                                            { player.getMain().getPerms().playerAdd(null, offlinePlayer, permission); } }
                    if (!boo && value) { if (player.getPlayer().hasPermission(permission))
                                            { player.getMain().getPerms().playerRemove(null, offlinePlayer, permission); } }
                }
            }
        }
    }

   
    //PLAYER TAB INFO TRUE?FALSE
    private void playerTabInfo(boolean boo) {
        PacketPlayOutPlayerInfo.EnumPlayerInfoAction info;
        if (!boo) { info = PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER; }
        else { info = PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER; }
        Player bukkitPlayer = Bukkit.getPlayer(player.getUUID());
        EntityPlayer[] playerNMS = new EntityPlayer[1];
        playerNMS[0] = ((CraftPlayer) bukkitPlayer).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(info, playerNMS);
        ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(packet);
    }

    
    //VANISH TOGGLE
    public void toggleVanish()
    {
        //CHECK IF PLAYER IS VANISHED
        if (!AdminConfig.vanishedAdmins.contains(player.getName()))
        {
            //ITERATE AND HIDE ADMIN FROM PLAYERS
            for (Player p : player.getMain().getServer().getOnlinePlayers())
            {
                //CHECK IF PLAYER IS AN ADMIN
                if (!AdminConfig.adminList.contains(player.getName()))
                {
                    p.hidePlayer(player.getMain(), player.getPlayer());
                }
            }
            AdminConfig.vanishedAdmins.add(player.getName());
            new BukkitRunnable() {
                @Override
                public void run() {
                    playerTabInfo(false);
                }
            }.runTaskLater(player.getMain(), 1L);
            if (config.getState(States.VANISH)) { player.message(Chat._vanishOn.txt); return; }
            config.setState(true, States.VANISH);
            player.getMain().getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + Chat._fakeLogOut.txt);
        } else
        {
            //ITERATE AND UN-HIDE ADMIN FROM PLAYERS
            for (Player p : player.getMain().getServer().getOnlinePlayers())
            {
                p.showPlayer(player.getMain(), player.getPlayer());
            }
            AdminConfig.vanishedAdmins.remove(player.getName());
            config.setState(false, States.VANISH);
            playerTabInfo(true);
            player.getMain().getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + Chat._fakeLogIn.txt);
        }
        player.save();
    }

    //ACTIVATE SPECTATE
    public boolean forceSpectate(Player p, String[] args)
    {
        //CHECK FOR VALID ARGUMENT AND USER INPUT
        if ((player.getMain().getServer().getPlayer(args[0]) == null
             || player.getMain().getServer().getPlayer(args[0]) == p
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
                    p.teleport(player.getMain().getServer().getPlayer(args[0]));
                    p.setSpectatorTarget(player.getMain().getServer().getPlayer(args[0]));
                    p.getSpectatorTarget().addPassenger(p);
                }
            }.runTaskLater(player.getMain(), 1L);
            return true;
        }
    }
}
