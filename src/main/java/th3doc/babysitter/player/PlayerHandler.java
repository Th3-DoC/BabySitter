package th3doc.babysitter.player;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.admin.AdminConfig;
import th3doc.babysitter.player.admin.PlayerAdmin;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.PlayerType;
import th3doc.babysitter.player.inventories.PlayerInventories;
import th3doc.babysitter.player.location.PlayerLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerHandler {

    //VARIABLES && GETTERS
    private final Main main;
    private final Player p;
    private final String name;
    private final UUID uuid;
    private final PlayerConfig config;
//    private final Rewards rewards;
    private final PlayerInventories inventories;
    private final PlayerLocation locations;
    private final PlayerAdmin adminPlayer;
    
    
    //CONSTRUCTOR
    public PlayerHandler(Main main, Player p)
    {
        //main
        this.main = main;
        this.p = p;
        this.name = p.getName();
        this.uuid = p.getUniqueId();
        //object classes
        this.config = new PlayerConfig(this);
        if (checkPermGroup(PlayerType.Admin, "")
            || p.hasPermission(Perm._permBypass.txt))
        {
            this.adminPlayer = new PlayerAdmin(this);
        } else { this.adminPlayer = null; }
//        this.rewards = new Rewards(this);
//        p.sendMessage("player rewards created");
        this.inventories = new PlayerInventories(this);
        this.locations = new PlayerLocation(this);
        vanishAdmin();
        if(!AdminConfig.vanishedAdmins.contains(name))
        {
            main.getServer().broadcastMessage(ChatColor.YELLOW + name + Chat._fakeLogIn.txt);
        }
    }
    
    
    //GETTERS
    public Main getMain() { return this.main; }
    public Player getPlayer() { return this.p; }
    public String getName() { return this.name; }
    public UUID getUUID() { return this.uuid; }
    public PlayerConfig getConfig() { return this.config; }
//    public Rewards rewards() { return this.rewards; }
    public PlayerInventories inventory() { return this.inventories; }
    public PlayerLocation location() { return this.locations; }
    public PlayerAdmin admin() { return this.adminPlayer; }
    public boolean isAdmin() { return AdminConfig.adminList.contains(name); }

    
    //SEND MESSAGE
    public void message(String message) { p.sendMessage(message); }
    
    
    //GET PLAYER PERMISSION HANDLER GROUP
    public boolean checkPermGroup(PlayerType type, String rank) {
        List<String> playerGroups = new ArrayList<>(Arrays.asList(main.getPerms().getPlayerGroups(p)));
        List<String> finalPlayerGroups = new ArrayList<>(playerGroups);
        for(String group : playerGroups)
        {
            for(String parentGroup : main.getGroups())
            {
                if(main.getPerms().groupHas((World) null, group, "group." + parentGroup))
                {
                    finalPlayerGroups.add(parentGroup);
                }
            }
        }
        List<String> adminGroups = main.defaultConfig().getAdminRanks();
        List<String> specialGroups = main.defaultConfig().getSpecialRanks();
        if(type == PlayerType.Admin)
        {
            for(String adminGroup : adminGroups) { return finalPlayerGroups.contains(adminGroup); }
        }
        else if(type == PlayerType.Special)
        {
            for(String specialGroup : specialGroups) { return playerGroups.contains(specialGroup); }
        }
        else if(!rank.equals("")) { return playerGroups.contains(rank); }
        return false;
    }

    
    //VANISH ADMINS FROM PLAYER
    public void vanishAdmin()
    {
        if(AdminConfig.vanishedAdmins != null)
        {
            List<String> vanishedAdmins = AdminConfig.vanishedAdmins;
            //CHECK FOR VANISHED ADMIN
            if(!vanishedAdmins.isEmpty())
            {
                //ITERATE ADMINS AND HIDE FROM PLAYER
                for(String vAdmin : vanishedAdmins)
                {
                    Player admin = main.getServer().getPlayer(vAdmin);
                    //CHECK ADMIN IS VALID
                    if(admin != null)
                    {
                        //CHECK PLAYER IS NOT AN ADMIN
                        if(!isAdmin())
                        {
                            p.hidePlayer(main, admin);
                        }
                    }
                }
            }
        }
    }
    
    
    //SAVE
    public void save()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                inventories.getConfig().save();
                adminPlayer.getConfig().save();
                locations.save();
                config.save();
            }
        }.runTaskAsynchronously(main);
    }

    
    //PLAYER MEMORY DUMP
    public void memoryDump()
    {
        save();
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                AdminConfig.adminList.remove(name);
                AdminConfig.vanishedAdmins.remove(name);
            }
        }.runTaskLaterAsynchronously(main, 20L);
        
    }
}
