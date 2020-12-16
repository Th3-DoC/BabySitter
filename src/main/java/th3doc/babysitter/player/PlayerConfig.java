package th3doc.babysitter.player;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOCase;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.filefilter.PrefixFileFilter;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PlayerConfig {

    //VARIABLES
    public static HashMap<String, String> playerList;//name/uuid
    private final String joinDate;
    
    
    //CONSTRUCTOR
    public PlayerConfig(PlayerHandler player)
    {
        //player
        //static variables
        playerList = new HashMap<>();
        //config
        boolean configSave = false;
        ConfigHandler listConfig = new ConfigHandler(player.getMain()
                , Config._playerData.txt
                , ""
                , Config._playerListConfig.txt);
        ConfigHandler playerConfig = new ConfigHandler(player.getMain()
                , Config._playerData.txt
                , player.getUUID().toString()
                , Config._playerConfig.txt);
        //add player name to config folder if none exits
        File file;
        file = new File(player.getMain().getDataFolder(),
                File.separator + Config._playerData.txt +
                        File.separator + player.getUUID().toString() +
                        File.separator + "!." + player.getName() + ".yml");
        //check file doesn't already exist
        if(!file.exists())
        {
            //check for a previous player name
            File directory = new File(player.getMain().getDataFolder(),
                    File.separator + Config._playerData.txt +
                            File.separator + player.getUUID().toString());
            File[] files = directory.listFiles((FileFilter) new PrefixFileFilter("!.", IOCase.INSENSITIVE));
            if(files != null)
            {
                for(File oldName : files)
                {
                    if(oldName.delete()) { Bukkit.getLogger().info(oldName.getName() + " File Deleted, "
                            + file + " Added"); }
                }
            }
            //create new file
            try { file.getParentFile().mkdirs(); file.createNewFile(); }
            catch(IOException f) { f.printStackTrace(); }
        }
        //PLAYER LIST CONFIG
        //ADD FEATURE LAST JOINED, CHECK ON LIST LOAD AGAINST CONFIG SET DATE TO DELETE OLD FILES && DELETE IF CONDITIONS MET
        //initialize
        if(!listConfig.getConfig().isSet(Config._playerList.txt)) {
            listConfig.getConfig().createSection(Config._playerList.txt);
            configSave = true;
        }
        //load player list from config if exists
        if(playerList.isEmpty()
           && listConfig.getConfig().getConfigurationSection(Config._playerList.txt).isSet("0"))
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
        //add player to list if missing
        if(!playerList.containsValue(player.getUUID().toString()))
        {
            String size = Integer.toString(playerList.size());
            if(!listConfig.getConfig().getConfigurationSection(Config._playerList.txt).isSet("0")) {
                size = "0";
            }
            playerList.put(player.getName(), player.getUUID().toString());
            ConfigurationSection section = listConfig.getConfig().getConfigurationSection(Config._playerList.txt)
                                                     .createSection(size);
            section.createSection(Config._playerName.txt);
            section.set(Config._playerName.txt, player.getName());
            section.createSection(Config._playerUUID.txt);
            section.set(Config._playerUUID.txt, player.getUUID().toString());
            configSave = true;
        }
        //check if player name matches value
        else if(playerList.containsValue(player.getUUID().toString()) && !playerList.containsKey(player.getName()))
        {
            for(String key: playerList.keySet())
            {
                if(playerList.get(key).equals(player.getUUID().toString()))
                {
                    if(!key.equals(player.getName()))
                    {
                        playerList.remove(key);
                        playerList.put(player.getName(), player.getUUID().toString());
                        configSave = true;
                    }
                }
            }
        }
        if(configSave) { listConfig.save();configSave = false; }
        //PLAYER CONFIG
        //initialize
        if(!playerConfig.getConfig().isSet(Config._joinDate.txt))
        {
            playerConfig.getConfig().createSection(Config._joinDate.txt);
            playerConfig.getConfig().set(Config._joinDate.txt, getDate("date"));
            configSave = true;

            //first join
        }
        if(configSave) { playerConfig.save(); }
        //load
        this.joinDate = playerConfig.getConfig().getString(Config._joinDate.txt);
    }
    
    
    //GETTERS
    public String getJoinDate() { return joinDate; }

    
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
}
