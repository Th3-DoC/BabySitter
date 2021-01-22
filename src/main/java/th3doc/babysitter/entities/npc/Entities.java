package th3doc.babysitter.entities.npc;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import th3doc.babysitter.Main;
import th3doc.babysitter.utils.config.Config;
import th3doc.babysitter.utils.debug.Debug;

import java.util.*;

public class Entities extends NPC
{
    
    public enum Paths
    {
        ENTITY_YML("Entities.yml", "entities"),
        DATA_FOLDER("Plugin_Data", ""),
        ENTITY("entity", ".entity"),
        RECIPES("recipes", ".recipes"),
        NAME_LIST("name-list", ".name-list");
        
        public String txt;
        public String path;
    
        Paths(String txt, String path) {
            this.txt = txt;
            this.path = path;
        }
    }
    
    //VARIABLES
    final private HashMap<UUID, String> nameList;
    final private HashMap<UUID, List<MerchantRecipe>> trades;
    final private HashMap<UUID, LivingEntity> entities;
    final private Main main;
    final private Debug debug;
    final private Config config;
    
    //CONSTRUCTOR
    public Entities(Main main)
    {
        super(main);
        this.main = main;
        this.debug = main.debug();
        this.nameList = new HashMap<>();
        this.entities = new HashMap<>();
        this.trades = new HashMap<>();
        //DEBUG
        if(debug.entities())
        { debug.message("Entities"); }
        boolean save = false;
        this.config = new Config(main,
                                 Paths.DATA_FOLDER.txt,
                                 "",
                                 Paths.ENTITY_YML.txt);
        //VILLAGERS
        if(!config.isSet(Paths.ENTITY_YML.path))
        {
            //DEBUG
            if(debug.entities())
            { debug.message("entities none existent"); }
            config.create(Paths.ENTITY_YML.path);
            save = true;
        }
        else
        {
            //DEBUG
            if(debug.entities())
            { debug.message("loading entities"); }
            if(config.getConfigSection(Paths.ENTITY_YML.path).getKeys(false).size() >= 1)
            {
                for(String entKey : config.getConfigSection(Paths.ENTITY_YML.path).getKeys(false))
                {
                    LivingEntity ent = deSerializeEntity(main, config.getStr(Paths.ENTITY_YML.path + "." +
                                                                             entKey +
                                                                             Paths.ENTITY.path));
                    if(ent != null)
                    {
                        this.nameList.put(ent.getUniqueId(), config.getStr(Paths.ENTITY_YML.path + "." +
                                                                           entKey +
                                                                           Paths.NAME_LIST.path));
                        this.entities.put(ent.getUniqueId(), ent);
                        if(!ent.getType().equals(EntityType.VILLAGER))
                        {
                            //trades
                            List<String> recipes = new ArrayList<>();
                            for(String recipeKey : config.getConfigSection(Paths.ENTITY_YML.path + "." +
                                                                           entKey +
                                                                           Paths.RECIPES.path).getKeys(false))
                            {
                                String recipe = config.getStr(Paths.ENTITY_YML.path + "." + entKey +
                                                              Paths.RECIPES.path +
                                                              "." + recipeKey);
                                recipes.add(recipe);
                            }
                            this.trades.put(ent.getUniqueId(), deSerializeRecipes(recipes.toArray(new String[0])));
                        }
                    }
                }
            }
            //DEBUG
            if(debug.entities())
            { debug.message("entities loaded"); }
        }
        if(save)
        {
            config.save();
            //DEBUG
            if(debug.entities())
            { debug.message("config saved on load"); }
        }
    }
    
    
    //TRADES
    public List<MerchantRecipe> getTrades(UUID uuid) { return this.trades.get(uuid); }
    public void setTrades(UUID uuid, List<MerchantRecipe> list) { this.trades.put(uuid, list); }
    public void openTrade(Player p, UUID entUUID, String entName, List<MerchantRecipe> list)
    {
        //save merchant with uuid for config set time standard of 24hrs
        //get merchant unless we need to reset it.
        Merchant merchant = Bukkit.createMerchant(entName);
        merchant.setRecipes(list);
        p.openMerchant(merchant, true);
    }
    
    
    //NAME LIST
    public void addToNameList(UUID uuid, String name) { this.nameList.put(uuid, name); }
    public Collection<String> getNameList()
    {
        return this.nameList.values();
    }
    public UUID getUUID(String name)
    {
        for(UUID uuid : this.nameList.keySet())
        {
            if(this.nameList.get(uuid).equals(name))
            {
                return uuid;
            }
        }
        return null;
    }
    
    
    //ENTITY
    public LivingEntity isEntity(UUID uuid)
    {
        return this.entities.getOrDefault(uuid, null);
    }
    
    public void addEntity(LivingEntity ent, String colorlessName)
    {
        setNameList(getNameList(), ent.getUniqueId(), colorlessName, ent.getType().name());
        this.entities.put(ent.getUniqueId(), ent);
        save();
    }
    
    public void removeEntity(String name)
    {
        (this.entities.get(getUUID(name))).remove();
        this.entities.remove(getUUID(name));
        this.nameList.remove(getUUID(name));
        save();
    }
    
    public void resetEntity(LivingEntity ent)
    {
        this.entities.put(ent.getUniqueId(), ent);
        save();
    }
    
    public boolean isEntityPlayer(String name)
    {
        Player p = main.getServer().getPlayer(name);
        return p != null;
    }
    
    //SAVE
    public void save()
    {
        //DEBUG
        if(debug.entities())
        { debug.message("initiated entity save"); }
        int i = 0;
        config.set(Paths.ENTITY_YML.path, null);
        for(UUID uuid : this.nameList.keySet())
        {
            LivingEntity ent = this.entities.get(uuid);
            //entity
            config.set(Paths.ENTITY_YML.path + "." + uuid + Paths.ENTITY.path,
                                   (serializeEntity(ent)));
            //name-list
            config.set(Paths.ENTITY_YML.path + "." + i + Paths.NAME_LIST.path, this.nameList.get(ent.getUniqueId()));
            //recipes
            EntityType type = ent.getType();
            if(type != EntityType.VILLAGER)
            {
                if(this.trades.get(ent.getUniqueId()) != null && !this.trades.get(ent.getUniqueId()).isEmpty())
                {
                    String[] recipes = serializeRecipes(this.trades.get(ent.getUniqueId()));
                    int r = 0;
                    for(String recipe : recipes)
                    {
                        config.create(Paths.ENTITY_YML.path +
                                                         "." + i +
                                                         "." + Paths.RECIPES.path +
                                                         "." + r);
                        config.set(Paths.ENTITY_YML.path +
                                               "." + i +
                                               "." + Paths.RECIPES.path +
                                               "." + r, recipe);
                        r++;
                    }
                }
            }
            i++;
        }
       main.utils().save().saveEntities(config);
        //DEBUG
        if(debug.entities())
        { debug.message("entities saved to yml"); }
    }
}
