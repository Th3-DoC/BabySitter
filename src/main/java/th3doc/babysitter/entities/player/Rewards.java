package th3doc.babysitter.entities.player;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.npc.EntityUtils;
import th3doc.babysitter.utils.config.Config;
import th3doc.babysitter.utils.debug.Debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Rewards implements EntityUtils
{
    
    public enum Paths
    {
        REWARDS_YML("Rewards.yml", "rewards-config"),
        DATA_FOLDER("Plugin_Data", ""),
        REWARD_SECTIONS("reward", "rewards-config.rewards"),
        GIFT_SECTIONS("gift-sections", "rewards-config.gifts"),
        IS_GIFTING("Is_Gifting", "rewards-config.is-gifting"),
        FIRST_JOIN_ITEMS("first-join-items", "rewards-config.first-join-items");
        
        public String txt;
        public String path;
        Paths(String txt, String path) { this.txt = txt;this.path = path; }
    }
    
    //VARIABLES
    final private HashMap<String, String> rewardSections;// name, base64-ItemStack[]
    final private HashMap<String, String> giftSections;// name, base64-ItemStack[]
    final private Main main;
    final private Debug debug;
    final private Config config;
    private String firstJoinItems;// base64-ItemStack[]
    private boolean isGifting;
    
    
    //CONSTRUCTOR
    public Rewards(Main main)
    {
        this.rewardSections = new HashMap<>();
        this.giftSections = new HashMap<>();
        this.main = main;
        this.debug = main.debug();
        //DEBUG
        if(debug.rewards())
        { debug.message("Rewards Initializing"); }
        this.config = new Config(main,
                                 Paths.DATA_FOLDER.txt,
                                 "",
                                 Paths.REWARDS_YML.txt);
        if(!config.isSet(Paths.IS_GIFTING.path)) {
            config.set(Paths.IS_GIFTING.path, false);
        }
        this.isGifting = config.getBoo(Paths.IS_GIFTING.path);
        //CHECK CONFIG VALUES, CREATE IF EMPTY
        boolean save = false;
        //GIFT SECTIONS
        if(!config.isSet(Paths.GIFT_SECTIONS.path)) {
            ItemStack[] items = new ItemStack[27];
            items[0] = new ItemStack(Material.RED_SHULKER_BOX);
            config.set(Paths.GIFT_SECTIONS.path + "."  + "default", serializeItemArray(items));
            save = true;
        }
        else {
//            List<String> keys = Arrays.asList(this.config.getConfigSection(Paths.GIFT_SECTIONS.path).getKeys(false).toArray(new String[0]));
//            if(keys.size() == 1)
//            {
//                this.giftSections.put(Paths.GIFT_SECTIONS.path + keys.get(0),
//                                      this.config.getStr(Paths.GIFT_SECTIONS.path + keys.get(0)));
//            }
//            else if(keys.size() >=2)
//            {
                for(String key : config.getConfigSection(Paths.GIFT_SECTIONS.path).getKeys(false))
                {
                    if(config.isSet(Paths.GIFT_SECTIONS.path + "." + key))
                    {
                        giftSections.put(Paths.GIFT_SECTIONS.path + "." + key,
                                              config.getStr(Paths.GIFT_SECTIONS.path + "." + key));
                    }
                }
//            }
        }
        //DEBUG
        if(debug.rewards())
        { debug.message("gifts = " + giftSections.keySet()); }
        //REWARDS SECTIONS
        if (!config.isSet(Paths.REWARD_SECTIONS.path))
        {
            config.set(Paths.REWARD_SECTIONS.path + "." + "default.default",
                                   serializeItemArray(new ItemStack[27]));
            save = true;
        }
        if(this.config.isSet(Paths.REWARD_SECTIONS.path))
        {
//            List<String> keys = Arrays.asList(this.config.getConfigSection(Paths.REWARD_SECTIONS.path).getKeys(false).toArray(new String[0]));
//            if(keys.size() == 1)
//            {
                // key = group
                //loop key, key.keyOFkey = group.name
                //.put( path.group.name, config.get(path.group.name) )
//                List<String> keys0 = Arrays.asList(this.config.getConfigSection(Paths.REWARD_SECTIONS.path + keys.get(0)).getKeys(false).toArray(new String[0]));
//                if(keys0.size() == 1)
//                {
//                        String keyOFkey = keys0.get(0);
//                        String pathName = keys.get(0) + "." + keyOFkey;
//                        this.rewardSections.put(Paths.REWARD_SECTIONS.path + pathName,
//                                                this.config.getStr(Paths.REWARD_SECTIONS.path + pathName));
//                        save = true;
//                }
//                else
//                {
//                    for(String key0 : this.config.getConfigSection(Paths.REWARD_SECTIONS.path + "." + keys.get(0)).getKeys(false))
//                    {
//                        String pathName = keys.get(0) + "." + key0;
//                        this.rewardSections.put(Paths.REWARD_SECTIONS.path + pathName,
//                                                this.config.getStr(Paths.REWARD_SECTIONS.path + pathName));
//                        save = true;
//                    }
////                }
//            }
//            else if(keys.size() > 1)
//            {
                for(String key : this.config.getConfigSection(Paths.REWARD_SECTIONS.path).getKeys(false))
                {
                    if(this.config.isSet(Paths.REWARD_SECTIONS.path + "." + key))
                    {
//                        List<String> keys0 = Arrays.asList(this.config.getConfigSection(Paths.REWARD_SECTIONS.path).getKeys(false).toArray(new String[0]));
//                        if(keys0.size() == 1)
//                        {
//                            String keyOFkey = this.config.getStr(Paths.REWARD_SECTIONS.path + keys0.get(0));
//                            String pathName = keys0.get(0) + "." + keyOFkey;
//                            this.rewardSections.put(Paths.REWARD_SECTIONS.path + pathName,
//                                                    this.config.getStr(Paths.REWARD_SECTIONS.path + pathName));
//                            save = true;
//                        }
//                        else if(keys0.size() > 1)
//                        {
                            for(String key0 : this.config.getConfigSection(Paths.REWARD_SECTIONS.path + "." + key).getKeys(false))
                            {
                                this.rewardSections.put(Paths.REWARD_SECTIONS.path + "." + key + "." + key0,
                                                        this.config.getStr(Paths.REWARD_SECTIONS.path + "." + key + "." + key0));
                                save = true;
                            }
//                        }
                    }
                }
//            }
        }
        //DEBUG
        if(debug.rewards())
        { debug.message("rewards = " + this.rewardSections.keySet()); }
        // FIRST JOIN ITEMS
        if (!this.config.isSet(Paths.FIRST_JOIN_ITEMS.path))
        {
            this.config.set(Paths.FIRST_JOIN_ITEMS.path, serializeItemArray(new ItemStack[54]));
            save = true;
        }
        this.firstJoinItems = this.config.getStr(Paths.FIRST_JOIN_ITEMS.path);
        //SAVE CONFIG
        if(save)
        {
            this.config.save();
            if(debug.rewards())
            { debug.message("Rewards saved"); }
        }
        if(debug.rewards())
        { debug.message("Rewards Initialized"); }
    }
    
    public boolean isGifting() {
        return isGifting;
    }
    
    public void setIsGifting(boolean b) {
        isGifting = b;
        config.set(Paths.IS_GIFTING.path, b);
    }
    
    public boolean isRewardSection(String name) { return this.rewardSections.containsKey(Paths.REWARD_SECTIONS.path + "." + name); }// send group.name
    
    public List<String> getRewardSections()
    {
        // check path.group.name
        // return list of group.name
        List<String> list = new ArrayList<>();
        if(!this.rewardSections.isEmpty())
        {
            for(String path : this.rewardSections.keySet())
            {
                String[] args = path.split("\\.");
                if(args.length >= 3)
                {
                    list.add(args[args.length - 2] + "." + args[args.length - 1]);
                }
            }
        }
        return list;
    }
    
    public ItemStack[] getRewardItems(String group, String name) { return deSerializeItemArray(this.rewardSections.get(Paths.REWARD_SECTIONS.path + "." + group + "." + name),0,27); }// run async as needed
    
    public void addRewardSection(String name, String group, ItemStack[] items)// run async as needed
    {
        String base64 = serializeItemArray(items);
        this.rewardSections.put(Paths.REWARD_SECTIONS.path + "." + group.toLowerCase() + "." + name, base64);
        this.config.set(Paths.REWARD_SECTIONS.path + "." + group.toLowerCase() + "." + name, base64);
        this.main.utils().save().saveRewards(this.config);
    }
    public void removeRewardSection(String name)
    {
        this.rewardSections.remove(Paths.REWARD_SECTIONS.path + "." + name);
        this.config.set(Paths.REWARD_SECTIONS.path + "." + name, null);
        this.main.utils().save().saveRewards(this.config);
    }
    
    public boolean isGiftSection(String name) { return this.giftSections.containsKey(Paths.GIFT_SECTIONS.path + "." + name); }
    
    public List<String> getGiftSections()
    {
        // check path.name
        // return list of name
        List<String> list = new ArrayList<>();
        if(!this.giftSections.isEmpty())
        {
            for(String path : this.giftSections.keySet())
            {
                String[] args = path.split("\\.");
                if(args.length >= 2) { list.add(args[args.length - 1]); }
            }
        }
        return list;
    }
    
    public void getGiftItems(BasicPlayer player) {
        if(debug.players()) { debug.message("getting gift items"); }
        Set<String> keys = config.getConfigSection(Paths.GIFT_SECTIONS.path).getKeys(false);
        ItemStack[] gifts = new ItemStack[keys.size()];
        for(String gift : keys) {
            ItemStack[] items = deSerializeItemArray(giftSections.get(Paths.GIFT_SECTIONS.path + "." + gift),0,27);
            Material shulker = items[0].getType();
            items[0] = null;
            loadShulker(player, items, shulker);
        }
        if(debug.players()) { debug.message("getting gift items"); }
    }
    
    public ItemStack[] editGiftSection(String name) {
        return deSerializeItemArray(giftSections.get(Paths.GIFT_SECTIONS.path + "." + name),0,27);
    }
    
    public void addGiftSection(String name, ItemStack[] items)
    {
        String base64 = serializeItemArray(items);
        giftSections.put(Paths.GIFT_SECTIONS.path + "." + name, base64);
        config.set(Paths.GIFT_SECTIONS.path + "." + name, base64);
        main.utils().save().saveRewards(config);
    }
    public void removeGiftSection(String name)
    {
        this.giftSections.remove( Paths.GIFT_SECTIONS.path + "." + name);
        this.config.set(Paths.GIFT_SECTIONS.path + "." + name, null);
        this.main.utils().save().saveRewards(config);
    }
    
    // FIRST JOIN ITEMS
    public ItemStack[] getFirstJoinItems(int start, int finish) {
        return deSerializeItemArray(this.firstJoinItems, start, finish);
    }
    
    public void setFirstJoinItems(ItemStack[] items) {
        firstJoinItems = serializeItemArray(items);
        config.set(Paths.FIRST_JOIN_ITEMS.path, firstJoinItems);
        main.utils().save().saveRewards(config);
    }
    
    private boolean loadShulker(Player p, ItemStack[] items, Material shulker)
    {
        //DEBUG
        if(debug.rewards())
        { debug.message("loading shulker"); }
        if(items.length <= 27)
        {
            if(shulker.name().toLowerCase().contains("shulker_box"))
            {
                ItemStack itemStack = new ItemStack(shulker);
                BlockStateMeta bsm = (BlockStateMeta) itemStack.getItemMeta();
                try { bsm.getBlockState(); }
                catch(NullPointerException e) { return false; }
                ShulkerBox box = (ShulkerBox) bsm.getBlockState();
                for(ItemStack item : items) { if(item != null) { box.getInventory().addItem(item); } }
                box.update();
                bsm.setBlockState(box);
                itemStack.setItemMeta(bsm);
                p.getInventory().addItem(itemStack);
                //DEBUG
                if(debug.rewards())
                { debug.message("loaded shulker"); }
                return true;
            }
        }
        //DEBUG
        if(debug.rewards())
        { debug.message("loading shulker failed"); }
        return false;
    }
}
