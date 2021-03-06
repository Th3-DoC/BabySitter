package th3doc.babysitter.player;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PlayerHandler {

    //CONSTRUCTOR
    private Main main;
    public PlayerHandler(Main main)
    {
        this.main = main;
        inventories = new PlayerInventories(main);
        locations = new PlayerLocation(main);
        adminPlayer = new PlayerAdmin(main);
    }

    //PLAYER DATA

    //INVENTORIES
    private PlayerInventories inventories;
    public PlayerInventories inventory() { return inventories; }

    //LOCATIONS
    private PlayerLocation locations;
    public PlayerLocation location() { return locations; }

    //ADMIN PLAYER
    private PlayerAdmin adminPlayer;
    public PlayerAdmin admin() { return adminPlayer; }

    //PLAYER LIST
    private HashMap<String, String> playerList = new HashMap<>();
    
    //CHECK ADMIN LIST FOR PLAYER
    public boolean isAdmin(String pName) { return adminPlayer.list().contains(pName); }

    //INITIALIZE
    public void initialize(Player p)
    {
        //ADD PLAYER NAME TO CONFIG FOLDER IF NONE EXISTS
        File file;
        file = new File(main.getDataFolder(),
                File.separator + Config._playerData.txt +
                        File.separator + p.getUniqueId().toString() +
                        File.separator + "!." + p.getName() + ".yml");
        if(!file.exists())
        {
            //ADD CHECK FOR OLD FILE!!!!!!
            try { file.getParentFile().mkdirs(); file.createNewFile(); }
            catch(IOException f) { f.printStackTrace(); }
        }
    
        //PLAYER LIST CONFIG
        ConfigHandler listConfig = new ConfigHandler(main
                , Config._playerData.txt
                , ""
                , Config._playerList.txt);
        
        //INITIALIZE PLAYER BASE
        if(!listConfig.getConfig().isSet(Config._playerList.txt))
        {
            listConfig.getConfig().createSection(Config._playerList.txt);
        }
        
        //CHECK PLAYER LIST ISN'T EMPTY
        if(!listConfig.getConfig().getConfigurationSection(Config._playerList.txt).getValues(false).isEmpty())
        {
            for(String key : listConfig.getConfig().getConfigurationSection(Config._playerList.txt).getKeys(false))
            {
                ConfigurationSection configSection =
                        listConfig.getConfig().getConfigurationSection(Config._playerList.txt).getConfigurationSection(key);
                assert configSection != null;
                String name = configSection.getString(Config._playerName.txt);
                String uuid = configSection.getString(Config._playerUUID.txt);
                
                playerList.put(name, uuid);
            }
        }
        
        //CHECK PLAYER EXISTS
        if(!playerList.containsKey(p.getName()))
        {
            playerList.put(p.getName(), p.getUniqueId().toString());
            ConfigurationSection section = listConfig.getConfig().getConfigurationSection(Config._playerList.txt)
                    .createSection(Integer.toString(playerList.size()));
            section.createSection(Config._playerName.txt);
            section.set(Config._playerName.txt, p.getName());
            section.createSection(Config._playerUUID.txt);
            section.set(Config._playerUUID.txt, p.getName());
        }
    
        //PLAYER CONFIG
        ConfigHandler playerConfig = new ConfigHandler(main
                , Config._playerData.txt
                , p.getUniqueId().toString()
                , Config._playerConfig.txt);
        
        //INITIALIZE PLAYER CONFIG
        if(!playerConfig.getConfig().isSet(Config._joinDate.txt))
        {
            playerConfig.getConfig().createSection(Config._joinDate.txt);
            playerConfig.getConfig().set(Config._joinDate.txt, getDate());
    
            //FIRST JOIN
            
        }

        //INITIALIZE PLAYER ACCESS
        adminPlayer.initialize(p);
        inventories.initialize(p);
        locations.initialize(p);
    }
    
    //GET DATE
    private String getDate()
    {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(now);
    }
    
    //GET TIME
    private String getTime()
    {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(now);
    }

    //GET PLAYER PERMISSION HANDLER GROUP
    public String getPermGroup(Player p) {
        String group = "";
        if (main.getLuckPerms().getUserManager().getUser(p.getUniqueId()) != null)
        {
            group = main.getLuckPerms().getUserManager().getUser(p.getUniqueId())
                    .getPrimaryGroup();
        }
        return group;
    }

    //VANISH ADMINS FROM PLAYER
    public void vanishAdmin(Player p)
    {
        List<String> vanishedAdmins = adminPlayer.getVanishedAdmins();
        //CHECK FOR VANISHED ADMIN
        if (!vanishedAdmins.isEmpty())
        {
            //ITERATE ADMINS AND HIDE FROM PLAYER
            for (String name : vanishedAdmins)
            {
                Player admin = main.getServer().getPlayer(name);
                //CHECK ADMIN IS VALID
                if (admin != null)
                {
                    //CHECK PLAYER IS NOT AN ADMIN
                    if (!isAdmin(p.getName()))
                    {
                        p.hidePlayer(main, admin);
                    }
                }
            }
        }
    }

    //PLAYER MEMORY DUMP
    public void memoryDump(Player p)
    {
        inventories.memoryDump(p);
        locations.memoryDump(p);
        adminPlayer.memoryDump(p);
    }
}
