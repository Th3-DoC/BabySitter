package th3doc.babysitter.utils.config;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import th3doc.babysitter.Main;
import th3doc.babysitter.utils.debug.Debug;

import java.util.*;

public class Save extends BukkitRunnable
{
    
    final private Main main;
    final private Debug debug;
    final private HashMap<UUID, Config> playerSave;
    final private LinkedList<Config> rewardsSave;
    final private LinkedList<Config> playerListSave;
    final private LinkedList<Config> entityListSave;
    private BukkitTask id;
    
    public Save(Main main)
    {
        this.main = main;
        this.debug = main.debug();
        this.playerSave = new HashMap<>();
        this.rewardsSave = new LinkedList<>();
        this.playerListSave = new LinkedList<>();
        this.entityListSave = new LinkedList<>();
    }
    
    public void savePlayer(UUID uuid, Config config) { this.playerSave.put(uuid, config); }
    public void removePlayer(UUID uuid) { playerSave.remove(uuid); }
    public void saveRewards(Config config) { this.rewardsSave.add(config); }
    public void savePlayerList(Config config) { this.playerListSave.add(config); }
    public void saveEntities(Config config) { this.entityListSave.add(config); }
    public void cancel() {
        /*DEBUG*/if(debug.utils()) { debug.message("rushing que"); }
        id.cancel();
        for(Config config : playerSave.values()) { config.save(); }
        for(Config config : rewardsSave) { config.save(); }
        for(Config config : playerListSave) { config.save(); }
        for(Config config : entityListSave) { config.save(); }
        /*DEBUG*/if(debug.utils()) { debug.message("que done"); }
    }
    
    @Override
    public void run()
    {
        //DEBUG
        if(debug.utils())
        { debug.message("S Save Time = " + main.utils().getConfig().getConfigSaveTime()); }
        id = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(debug.utils())
                { debug.message("Checking Configs To Save ..."); }
                // player-list
                if(!playerListSave.isEmpty())
                {
                    Collection<Config> list = new ArrayList<>(playerListSave);
                    playerListSave.clear();
                    for(Config config : list)
                    {
                        //DEBUG
                        if(debug.utils())
                        { debug.message("*----------------------------------------------------*" +
                                        "\n saving = " + "Saving Player List" +
                                        "*----------------------------------------------------*"); }
                        config.save();
                    }
                }
                // rewards
                if(!rewardsSave.isEmpty())
                {
                    Collection<Config> list = new ArrayList<>(rewardsSave);
                    rewardsSave.clear();
                    for(Config config : list)
                    {
                        //DEBUG
                        if(debug.utils())
                        { debug.message("*----------------------------------------------------*" +
                                        "\n saving = " + "Saving Rewards" +
                                        "*----------------------------------------------------*"); }
                        config.save();
                    }
                }
                if(!entityListSave.isEmpty())
                {
                    Collection<Config> list = new ArrayList<>(entityListSave);
                    entityListSave.clear();
                    for(Config config : list)
                    {
                        //DEBUG
                        if(debug.utils())
                        { debug.message("*----------------------------------------------------*" +
                                        "\n saving = " + "Saving Entities" +
                                        "*----------------------------------------------------*"); }
                        config.save();
                    }
                }
                if(!playerSave.isEmpty())
                {
                    Collection<Config> list = new ArrayList<>(playerSave.values());
                    playerSave.clear();
                    for(Config config : list)
                    {
                        //DEBUG
                        if(debug.utils())
                        { debug.message("*----------------------------------------------------*" +
                                        "\n saving = " + config.getOwnerID() +
                                        "*----------------------------------------------------*"); }
                        config.save();
                    }
                }
            }
        }.runTaskTimerAsynchronously(main, 0, main.utils().getConfig().getConfigSaveTime());
    }
}
