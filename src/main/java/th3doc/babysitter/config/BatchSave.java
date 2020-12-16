package th3doc.babysitter.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.Main;

import java.util.HashMap;
import java.util.UUID;

public class BatchSave extends BukkitRunnable {
    public static HashMap<UUID, ConfigHandler> batchSave = new HashMap<>();
    @Override
    public void run()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for(ConfigHandler config : batchSave.values())
                {
                    config.save();
                }
            }
        }.runTaskAsynchronously(JavaPlugin.getPlugin(Main.class));
    }
}
