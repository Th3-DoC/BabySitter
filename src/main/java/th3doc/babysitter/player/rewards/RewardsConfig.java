package th3doc.babysitter.player.rewards;

import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;
import th3doc.babysitter.player.PlayerHandler;
import th3doc.babysitter.player.data.InvType;

import java.util.HashMap;
import java.util.List;

public class RewardsConfig {
    
    //VARIABLES
    private final HashMap<String, ItemStack[]> rewardSections;
    private final ItemStack[] firstJoinItems;
    
    
    //CONSTRUCTOR
    public  RewardsConfig(PlayerHandler player)
    {
        this.rewardSections = new HashMap<>();
        ItemStack[] tempJoinItems = new ItemStack[0];
        ConfigHandler config = new ConfigHandler(player.getMain()
                , Config._playerData.txt
                , ""
                , Config._rewardsConfig.txt);
    
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
            tempJoinItems = ((List<String>) config.getConfig()
                                                    .getConfigurationSection(Config._firstJoinItems.txt)).toArray(new ItemStack[0]);
        }
        //SAVE CONFIG
        this.firstJoinItems = tempJoinItems;
        config.save();
    }
    
    
    //GETTERS
    public List<String> getRewardSections() { return (List<String>) (rewardSections.keySet()).stream(); }
    public ItemStack[] getRewardItems(String name) { return rewardSections.get(name); }
    public ItemStack[] getFirstJoinItems() { return firstJoinItems; }
    
    
    //SAVE INVENTORY TO FIRST JOIN/RANK
    public void saveInventory(InvType type, ItemStack[] items, String rewardsList)
    {
        InvType setType = null;
        if(type == InvType.FirstJoin) { setType = InvType.FirstJoin; }
        else if(type == InvType.RankPrize) { setType = InvType.RankPrize; }
        else { return; }
        boolean setList = false;
        if(rewardSections.get(rewardsList) != null)
        {
            setList = true;
        }
    }
}
