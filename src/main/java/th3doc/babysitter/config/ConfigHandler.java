package th3doc.babysitter.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import th3doc.babysitter.Main;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

    //VARIABLES
    private final File file;
    private final FileConfiguration config;

    //CONSTRUCTOR
    public ConfigHandler(Main main, String folder, String uuid, String yml)
    {
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

    //GET CONFIG
    public FileConfiguration getConfig() { return this.config; }

    //SAVE CONFIG
    public void save()
    {
        try { ConfigHandler.this.config.save(ConfigHandler.this.file); }
        catch(IOException ignored) {}
    }
}
