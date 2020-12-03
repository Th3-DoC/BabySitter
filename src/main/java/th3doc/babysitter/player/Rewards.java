package th3doc.babysitter.player;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;
import th3doc.babysitter.player.data.InvType;

import java.util.HashMap;
import java.util.List;

public class Rewards {
    
    //CONSTRUCTOR
    private Main main;
    public Rewards(Main main) { this.main = main;
        config = new ConfigHandler(this.main
                , Config._playerData.txt
                , ""
                , Config._rewardsConfig.txt);
    }
    
    //CONFIG
    private ConfigHandler config;
    
    //REWARDS SECTIONS
    private HashMap<String, ItemStack[]> rewardSections = new HashMap<>();
    public List<String> getRewardSections() { return (List<String>) (rewardSections.keySet()).stream(); }
    public ItemStack[] getRewardItems(String name) { return rewardSections.get(name); }
    
    //FIRST JOIN ITEMS
    private ItemStack[] firstJoinItems = new ItemStack[0];
    
    //INITIALIZE
    public void initialize()
    {
        /**
         *
         * LOAD/CREATE CONFIG
         *
         */
        //LOAD PLAYER CONFIG
        
        //CHECK CONFIG VALUES, CREATE IF EMPTY
        //SURVIVAL LOCATION
        if (!config.getConfig().isSet(Config._rewardSections.txt))
        {
            config.getConfig().createSection(Config._rewardSections.txt + "." + Config._baseSection.txt);
            config.getConfig().getConfigurationSection(Config._rewardSections.txt).set(Config._baseSection.txt, new ItemStack[0]);
        }
        if(config.getConfig().getConfigurationSection(Config._rewardSections.txt)
                .getConfigurationSection(Config._baseSection.txt) != null)
        {
            for(String key : config.getConfig().getConfigurationSection(Config._rewardSections.txt).getKeys(false))
            {
                if(((List<String>) config.getConfig()
                        .getConfigurationSection(Config._rewardSections.txt).get(key)).toArray(new ItemStack[0]) != null)
                {
                    rewardSections.put(key, ((List<String>) config.getConfig()
                            .getConfigurationSection(Config._rewardSections.txt).get(key)).toArray(new ItemStack[0]));
                }
            }
        }
        if (!config.getConfig().isSet(Config._firstJoinItems.txt))
        {
            config.getConfig().createSection(Config._firstJoinItems.txt);
            config.getConfig().set(Config._firstJoinItems.txt, new ItemStack[0]);
        }
        if(config.getConfig()
                .getConfigurationSection(Config._firstJoinItems.txt) != null) {
            firstJoinItems = ((List<String>) config.getConfig()
                    .getConfigurationSection(Config._firstJoinItems.txt)).toArray(new ItemStack[0]);
        }
        //SAVE CONFIG
        config.save();
        /**
         * END LOAD/CREATE CONFIG
         */
        
    }
    
    //SAVE INVENTORY TO FIRST JOIN/RANK
    public void saveInventory(InvType type, ItemStack[] items, String rewardsList)
    {
        InvType setType = null;
        if(type == InvType.FirstJoin) { setType = InvType.FirstJoin; }
        else if(type == InvType.RankPrize) { setType = InvType.RankPrize; }
        else { return; }
        boolean setList = false;
        if(config.getConfig().getConfigurationSection(Config._rewardSections.txt).isSet(rewardsList))
        {
            setList = true;
        }
    }
}
