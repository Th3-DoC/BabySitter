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
        rewards = new Rewards(main);
        inventories = new PlayerInventories(main);
        locations = new PlayerLocation(main);
        adminPlayer = new PlayerAdmin(main);
    }

    //PLAYER DATA
    
    //REWARDS
    private Rewards rewards;
    public Rewards rewards() { return rewards; }

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
    public HashMap<String, String> list() { return playerList; }
    
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
                , Config._playerListConfig.txt);
        //check player isn't empty
        if(!listConfig.getConfig().isSet(Config._playerList.txt)) {
            listConfig.getConfig().createSection(Config._playerList.txt);
        }
        if(listConfig.getConfig().getConfigurationSection(Config._playerList.txt).isSet("0"))
        {
            for(String key : listConfig.getConfig().getConfigurationSection(Config._playerList.txt)
                    .getKeys(false))
            {
                ConfigurationSection configSection =
                        listConfig.getConfig().getConfigurationSection(Config._playerList.txt)
                                .getConfigurationSection(key);
                assert configSection != null;
                String name = configSection.getString(Config._playerName.txt);
                String uuid = configSection.getString(Config._playerUUID.txt);
                
                playerList.put(name, uuid);
            }
        }
        
        //check player exists
        if(!playerList.containsValue(p.getUniqueId().toString()))
        {
            String size = Integer.toString(playerList.size());
            if(!listConfig.getConfig().getConfigurationSection(Config._playerList.txt).isSet("0")) {
                size = "0";
            }
            playerList.put(p.getName(), p.getUniqueId().toString());
            ConfigurationSection section = listConfig.getConfig().getConfigurationSection(Config._playerList.txt)
                    .createSection(size);
            section.createSection(Config._playerName.txt);
            section.set(Config._playerName.txt, p.getName());
            section.createSection(Config._playerUUID.txt);
            section.set(Config._playerUUID.txt, p.getUniqueId().toString());
        }
        //save config
        listConfig.save();
        //PLAYER CONFIG
        ConfigHandler playerConfig = new ConfigHandler(main
                , Config._playerData.txt
                , p.getUniqueId().toString()
                , Config._playerConfig.txt);
        
        //initialize
        if(!playerConfig.getConfig().isSet(Config._joinDate.txt))
        {
            playerConfig.getConfig().createSection(Config._joinDate.txt);
            playerConfig.getConfig().set(Config._joinDate.txt, getDate("time"));
    
            //first join
    
            playerConfig.save();
        }
        //INITIALIZE PLAYER ACCESS
        rewards.initialize();
        adminPlayer.initialize(p);
        inventories.initialize(p);
        locations.initialize(p);
    }
    
    //GET DATE
    private String getDate(String type)
    {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String[] split = format.format(now).split(" ");
        String send = "";
        if(type.equals("date")) { send = split[0]; }
        if(type.equals("time")) { send = split[1]; }
        return send;
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
