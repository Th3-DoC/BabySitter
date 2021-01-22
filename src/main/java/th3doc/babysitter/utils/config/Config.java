package th3doc.babysitter.utils.config;


import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import th3doc.babysitter.Main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Config
{

    //VARIABLES
    final private File file;
    final private FileConfiguration config;
    final private String uuid;

    //CONSTRUCTOR
    public Config(Main main, String folder, String uuid, String yml)
    {
        this.uuid = uuid;
        this.file = new File(main.getDataFolder(),
                File.separator + folder +
                        File.separator + uuid +
                        File.separator + yml);
        if(!file.exists())
        {
            try
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch(IOException e){ e.printStackTrace(); }
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }
    
    public String getOwnerID() { return this.uuid; }

    //GET CONFIG
    public boolean isSet(String path) { return this.config.isSet(path); }
    public ConfigurationSection getConfigSection(String path) { return this.config.getConfigurationSection(path); }
    public Object get(String path) { return config.get(path); }
    public String getStr(String path) { return this.config.getString(path); }
    public boolean getBoo(String path) { return this.config.getBoolean(path); }
    public List<Map<?, ?>> getMap(String path) { return this.config.getMapList(path); }
    public UUID getUUID(String path) { return UUID.fromString(path); }
    public Location getLoc(String path) { return this.config.getLocation(path); }
    public void create(String path) { this.config.createSection(path); }
    public void create(String path, Map<String, Object> map) { this.config.createSection(path, map); }
    public void set(String path, Object obj)
    {
        if(!this.config.isSet(path))
        {
            this.config.createSection(path);
        }
        this.config.set(path, obj);
    }
    
    //SAVE CONFIG
    public void save()
    {
        try { this.config.save(this.file); }
        catch(IOException e) { e.printStackTrace(); }
    }
}
