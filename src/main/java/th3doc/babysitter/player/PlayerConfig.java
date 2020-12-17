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
    final private PlayerHandler player;
    private final String joinDate;
    final private String joinTime;
    private String playTime;
    
    
    //CONSTRUCTOR
    public PlayerConfig(PlayerHandler player)
    {
        //player
        //static variables
        playerList = new HashMap<>();
        this.player = player;
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
        if(configSave) { listConfig.save(); }
        //PLAYER CONFIG
        //initialize
        if(!playerConfig.getConfig().isSet(Config._joinDate.txt))
        {
            playerConfig.getConfig().createSection(Config._joinDate.txt);
            playerConfig.getConfig().set(Config._joinDate.txt, getDate("date"));

            //first join implemented below here !!!!!
        }
        if(!playerConfig.getConfig().isSet(Config._lastJoined.txt))
        {
            playerConfig.getConfig().createSection(Config._lastJoined.txt + "." + Config._date.txt);
            playerConfig.getConfig().createSection(Config._lastJoined.txt + "." + Config._time.txt);
        }
        playerConfig.getConfig().set(Config._lastJoined.txt + "." + Config._date.txt, getDate("date"));
        playerConfig.getConfig().set(Config._lastJoined.txt + "." + Config._time.txt, getDate("time"));
        if(playerConfig.getConfig().isSet(Config._lastJoined.txt))
        {
            playerConfig.getConfig().set(Config._lastJoined.txt + "." + Config._date.txt, getDate("date"));
            playerConfig.getConfig().set(Config._lastJoined.txt + "." + Config._time.txt, getDate("time"));
        }
        if(!playerConfig.getConfig().isSet(Config._playTime.txt))
        {
            playerConfig.getConfig().createSection(Config._playTime.txt);
            playerConfig.getConfig().set(Config._playTime.txt, "0:0:0:0");
        }
        //config save
        playerConfig.save();
        //load
        this.joinDate = playerConfig.getConfig().getString(Config._joinDate.txt);
        this.joinTime = playerConfig.getConfig().getString(Config._lastJoined.txt + "." + Config._time.txt);
        this.playTime = playerConfig.getConfig().getString(Config._playTime.txt);
    }
    
    
    //GETTERS
    public String getFirstJoinDate() { return joinDate; }
    public String getJoinTime() { return joinTime; }
    public String getPlayTime() { return this.playTime; }
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
    
    
    //SETTERS
    public void setPlayTime()
    {
        final int[] currentTime = parseInt(getDate("time"), ":");
        final int[] joinTime = parseInt(this.joinTime, ":");
        final int[] playTime = parseInt(this.playTime, ":");
        this.playTime = checkPlayTimeFormat(currentTime, joinTime, playTime);
    }
    private int[] parseInt(String str, String regex)
    {
        String[] strArray = str.split(regex);
        int[] ints = new int[strArray.length];
        for(int i=0; i<strArray.length; i++)
        {
            ints[i] = Integer.parseInt(strArray[i]);
        }
        return ints;
    }
    private String checkPlayTimeFormat(int[] current, int[] join, int[] play)
    {
        int day = ((current[0] - join[0]) / 24) + play[0];
        int hr = ((current[0] - join[0]) % 24) + play[1];
        int min = (current[1] - join[1]) + play[2];
        int sec = (current[2] - join[2]) + play[3];
        final String s = Integer.toString(sec % 60);
        min += sec / 60;
        final String m = Integer.toString(min % 60);
        hr += min / 60;
        final String h = Integer.toString((hr) % 24);
        day += hr / 24;
        final String d = Integer.toString(day);
        
        return d + ":" + h + ":" + m + ":" + s;
    }
    
    
    //SAVE
    public void save()
    {
        ConfigHandler config = new ConfigHandler(player.getMain(),
                                                 Config._playerData.txt,
                                                 player.getUUID().toString(),
                                                 Config._playerConfig.txt);
        setPlayTime();
        config.getConfig().set(Config._playTime.txt, this.playTime);
        config.save();
    }
}
