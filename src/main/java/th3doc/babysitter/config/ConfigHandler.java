package th3doc.babysitter.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import th3doc.babysitter.Main;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

    //VARIABLES
    private File file;
    private FileConfiguration config;

    //CONSTRUCTOR
    public ConfigHandler(Main main, String folder, String uuid, String yml) {

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
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    //GET CONFIG
    public FileConfiguration getConfig() { return this.config; }

    //SAVE CONFIG
    public void save() {
        try { this.config.save(this.file); }
        catch (IOException e) { e.printStackTrace(); }
    }
}
