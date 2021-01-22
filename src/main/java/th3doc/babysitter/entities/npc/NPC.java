package th3doc.babysitter.entities.npc;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.MerchantRecipe;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.debug.Debug;

import java.util.*;

public class NPC implements EntitySubInterface
{
    
    //VARIABLES
    final private Main main;
    final private Debug debug;
    
    
    //CONSTRUCTOR
    public NPC(Main main)
    {
        this.main = main;
        this.debug = main.debug();
    }
    
    /**
     *
     *
     * Set Name
     * @param nameList
     * @param name
     * @return
     */
    public void setNameList(Collection<String> nameList, UUID uuid, String name, String type)
    {
        if(debug.entities())
        { debug.message("startingName = " + name); }
        StringBuilder newName = new StringBuilder(name + ":" + "[" + type + "]");
        List<String> types = new ArrayList<>();
        for(String storedName : nameList.toArray(new String[0]))
        {
            String[] args = storedName.split(":");
            if(args[1].equals("[" + type + "]")) { types.add(storedName); }
        }
        //if contains same add suffix[Zombie][1][2][etc]
        for(int i = types.size();i<1000;i++)
        {
            if(!types.contains(newName.toString())) { break; }
            if(types.contains(newName.toString() + ":" + "[" + i + "]"))
            {
                continue;
            }
            newName.append(":").append("[").append(i).append("]");
            break;
        }
        if(debug.entities())
        { debug.message("newName = " + newName.toString()); }
        main.entities().addToNameList(uuid, newName.toString());
        if(debug.entities())
        { debug.message(main.entities().getNameList().toString()); }
    }
    /**
     *
     *
     * Recipe List
     * @param recipes
     * @param start
     * @return
     */
    public List<MerchantRecipe> getRecipeList(String[] recipes, int start)
    {
        //selling:uses:buying:amount:buying:amount:maxUses:priceMultiplier
        List<String> temp = new ArrayList<>(Arrays.asList(recipes).subList(start, (recipes.length)));
        //DEBUG
        if(debug.entities())
        { debug.message("serializedList = " + temp); }
        String[] array = temp.toArray(new String[0]);
        //DEBUG
        if(debug.entities())
        { debug.message("serializedArray = " + Arrays.toString(array)); }
        if(array.length > 0)
        {
            if(array.length == 1) { if((array[0].split(":")).length == 8) { return deSerializeRecipes(array); } }
            else
            {
                for(String serialized : array)
                {
                    if((serialized.split(":")).length == 8) { continue; }
                    return null;
                }
                return deSerializeRecipes(array);
            }
        }
        return null;
    }
    /**
     *
     *
     * Get Pet
     * @param ent
     * @param loc
     * @param god
     * @param ai
     * @return
     */
    public LivingEntity getPet(String ent, Location loc, boolean god, boolean ai)
    {
        LivingEntity temp;
        switch(ent.toLowerCase())
        {
            case "cat":
                temp = loc.getWorld().spawn(loc.add(2, 0, 2),
                                                Cat.class);
                break;
            case "wolf":
                temp = loc.getWorld().spawn(loc.add(2, 0, 2),
                                                 Wolf.class);
                break;
            case "llama":
                temp = loc.getWorld().spawn(loc.add(2, 0, 2),
                                                 Llama.class);
                break;
            case "rabbit":
                temp = loc.getWorld().spawn(loc.add(2, 0, 2),
                                                   Rabbit.class);
                break;
            default: temp = null;
        }
        if(temp != null)
        {
            temp.setInvulnerable(god);
            temp.setAI(ai);
        }
        return temp;
    }
    /**
     *
     *
     * Set Age
     * @param age
     * @param ent
     * @return
     */
    public LivingEntity setAge(String age, LivingEntity ent)
    {
        Villager villager = null;
        Zombie zombie = null;
        if(ent instanceof Villager) { villager = (Villager) ent; }
        else if(ent instanceof Zombie) { zombie = (Zombie) ent; }
        else { return ent; }
        if(age.toLowerCase().equals("baby"))//villager of zombie
        {
            if(villager != null) { villager.setBaby(); }
            if(zombie != null) { zombie.setBaby(); }
        }
        else if(age.toLowerCase().equals("adult"))
        {
            if(villager != null) { villager.setAdult(); }
            if(zombie != null) { zombie.setAdult(); }
        }
        else { return null; }
        LivingEntity temp;
        if(villager != null) { temp = villager; }
        else { temp = zombie; }
        return temp;
    }
    /**
     *
     *
     * Villager Type
     * @param type
     * @param villager
     * @return
     */
    public LivingEntity villagerType(String type, LivingEntity villager)
    {
        Villager temp = (Villager) villager;
        Villager.Type temp1;
        switch(type.toLowerCase())
        {
            case "desert":
                temp1 = Villager.Type.DESERT;
                break;
            case "jungle":
                temp1 = Villager.Type.JUNGLE;
                break;
            case "savanna":
                temp1 = Villager.Type.SAVANNA;
                break;
            case "snow":
                temp1 = Villager.Type.SNOW;
                break;
            case "swamp":
                temp1 = Villager.Type.SWAMP;
                break;
            case "taiga":
                temp1 = Villager.Type.TAIGA;
                break;
            default:
                temp1 = Villager.Type.PLAINS;
        }
        temp.setVillagerType(temp1);
        return temp;
    }
    /**
     *
     *
     * Villager Profession
     * @param profession
     * @param villager
     * @return
     */
    public LivingEntity villagerProfession(String profession, LivingEntity villager)
    {
        Villager temp = (Villager) villager;
        Villager.Profession temp1;
        switch(profession.toLowerCase())
        {
            case "armorer":
                temp1 = Villager.Profession.ARMORER;
                break;
            case "butcher":
                temp1 = Villager.Profession.BUTCHER;
                break;
            case "cartographer":
                temp1 = Villager.Profession.CARTOGRAPHER;
                break;
            case "cleric":
                temp1 = Villager.Profession.CLERIC;
                break;
            case "farmer":
                temp1 = Villager.Profession.FARMER;
                break;
            case "fisherman":
                temp1 = Villager.Profession.FISHERMAN;
                break;
            case "fletcher":
                temp1 = Villager.Profession.FLETCHER;
                break;
            case "leatherworker":
                temp1 = Villager.Profession.LEATHERWORKER;
                break;
            case "librarian":
                temp1 = Villager.Profession.LIBRARIAN;
                break;
            case "mason":
                temp1 = Villager.Profession.MASON;
                break;
            case "nitwit":
                temp1 = Villager.Profession.NITWIT;
                break;
            case "shepherd":
                temp1 = Villager.Profession.SHEPHERD;
                break;
            case "toolsmith":
                temp1 = Villager.Profession.TOOLSMITH;
                break;
            case "weaponsmith":
                temp1 = Villager.Profession.WEAPONSMITH;
                break;
            default:
                temp1 = Villager.Profession.NONE;
        }
        temp.setProfession(temp1);
        temp.setVillagerLevel(5);
        temp.setVillagerExperience(100);
        return temp;
    }
    
    public boolean spawnEntity(BasicPlayer player, Block block, String[] args)
    {
        LivingEntity ent;
    
        //args[0]
        if(args.length > 2)
        {
            //DEBUG
            if(debug.cmds())
            { debug.message("entityType = args[1]"); }
            //OPERATIONS
            final String entClassName = "org.bukkit.entity." + formatUpperToFirstCapital(true, args[1], "");
            Class entClass;
            try { entClass = Class.forName(entClassName); }
            catch(ClassNotFoundException ignored) { player.message(Utils.Chat.INVALID_ENTITY_SPAWN.txt); return false; }
            Location targetBlock = block.getLocation();
            org.bukkit.entity.Entity temp1 = player.getWorld().spawn(targetBlock.add(0.5, 1, 0.5),
                                                                     entClass);
            ent = (LivingEntity) temp1;
            ent.setRemoveWhenFarAway(false);
        }
        else { player.message(Utils.Chat.INVALID_CMD.txt); return false; }
    
        if(args.length > 7)
        {
            //args[2] & args[3]
            //DEBUG
            if(debug.cmds())
            { debug.message("color|name = args[2] & args[3]"); }
            //OPERATIONS
            String colorCode = "";
            if(isValidColorCode(args[2]))
            {
                colorCode = args[2];
            }
            //DEBUG
            if(debug.cmds())
            { debug.message("setColoredName = " + ChatColor.of(colorCode) + args[3]); }
            ent.setCustomName(ChatColor.of(colorCode) + args[3]);
            
            //args[4]
            //DEBUG
            if(debug.cmds())
            { debug.message("visibleName = args[4]"); }
            //OPERATIONS
            ent.setCustomNameVisible(Boolean.parseBoolean(args[4]));
            
            //args[5]
            //DEBUG
            if(debug.cmds())
            { debug.message("god = args[5]"); }
            //OPERATIONS
            ent.setInvulnerable(Boolean.parseBoolean(args[5]));
            //args[6]
            //DEBUG
            if(debug.cmds())
            { debug.message("ai = args[6]"); }
            //OPERATIONS
            ent.setAI(Boolean.parseBoolean(args[6]));
            //villager
            if(args[1].toLowerCase().equals(EntityType.VILLAGER.name().toLowerCase()))
            {
                //DEBUG
                if(debug.cmds())
                { debug.message("villager"); }
                // command // villager[1] // color[2] // name[3] // visible[4]
                // invulnerable[5] // ai[6] // age[7] // type[8] // profession[9]
                // recipes[10]++
            
                //OPERATIONS
                Villager villager = (Villager) ent;
                villager.setBreed(false);
                //args[9]
                if(args.length > 8)
                {
                    //DEBUG
                    if(debug.cmds())
                    { debug.message("args[7]"); }
                    //OPERATIONS
                    if(setAge(args[7], ent) != null) { setAge(args[7], ent); }
                    else { return cancelSpawn(ent); }
                    villager.setAgeLock(true);
                }
                else { return cancelSpawn(ent); }
                //args[8]
                if(args.length > 9)
                {
                    //DEBUG
                    if(debug.cmds())
                    { debug.message("args[8]"); }
                    //OPERATIONS
                    villagerType(args[8], ent);//villager specific
                }
                else { return cancelSpawn(ent); }
                //args[9]
                if(args.length > 10)
                {
                    //DEBUG
                    if(debug.cmds())
                    { debug.message("args[9]"); }
                    //OPERATIONS
                    villagerProfession(args[9], ent);//villager specific
                }
                else { return cancelSpawn(ent); }
                //args[10]++
                //DEBUG
                if(debug.cmds())
                { debug.message("args[10]"); }
                //OPERATIONS
                if(getRecipeList(args, 10) != null)
                {
                    villager.setRecipes(getRecipeList(args, 10));
                    //DEBUG
                    if(debug.cmds())
                    { debug.message("recipes set"); }
                }
                else { return cancelSpawn(ent); }
                main.entities().addEntity(villager, args[3]);
                main.entities().save();
                return true;
            }
            else if(args[1].toLowerCase().equals(EntityType.ZOMBIE.name().toLowerCase()))
            {
                //DEBUG
                if(debug.cmds())
                { debug.message("zombie"); }
                // command // zombie[1] // color[2] // name[3] // visible[4]
                // invulnerable[5] // ai[6] // age[7] // recipes[8]++
            
                //OPERATIONS
                Zombie zombie = (Zombie) ent;
                //args[7]
                if(args.length > 8)
                {
                    //DEBUG
                    if(debug.cmds())
                    { debug.message("args[7]"); }
                    //OPERATIONS
                    if(setAge(args[7], ent) != null) { setAge(args[7], ent); }
                    else { return cancelSpawn(ent); }
                }
                else { return cancelSpawn(ent); }
                //args[8]
                //DEBUG
                if(debug.cmds())
                { debug.message("args[8]"); }
                //OPERATIONS
                if(getRecipeList(args, 8) != null)
                {
                    main.entities().addEntity(zombie, args[3]);
                    main.entities().setTrades(zombie.getUniqueId(), getRecipeList(args, 8));
                    //DEBUG
                    if(debug.cmds())
                    { debug.message("recipes set"); }
                    main.entities().save();
                    return true;
                    
                }
                else { return cancelSpawn(ent); }
            }
            else
            {
                //DEBUG
                if(debug.cmds())
                { debug.message("entity"); }
                // command // entity[1] // color[2] // name[3] // visible[4]
                // invulnerable[5] // ai[6] // recipes[7]++
                
                //args[7]
                //DEBUG
                if(debug.cmds())
                { debug.message("args[7]"); }
                //OPERATIONS
                if(getRecipeList(args, 7) != null)
                {
                    main.entities().addEntity(ent, args[3]);
                    main.entities().setTrades(ent.getUniqueId(), getRecipeList(args, 7));
                    //DEBUG
                    if(debug.cmds())
                    { debug.message("recipes set"); }
                    main.entities().save();
                    return true;
                }
                else { return cancelSpawn(ent); }
            }
        } else { return false; }
    }
    
    private boolean cancelSpawn(LivingEntity ent) { ent.remove(); return false; }
    
    public boolean modifyEntity(String[] args)
    {
        String entNameList = args[1];
        String modOption = args[2].toLowerCase();
        // color // name // visible // invulnerable // ai // age // type // profession // recipes
        //aware // breeding // pickup items // silent // silly extras // pets // armour // item in hand // age lock // trade refresh rate in hours //
        if(main.entities().isEntity(main.entities().getUUID(entNameList)) != null)
        {
            LivingEntity ent = main.entities().isEntity(main.entities().getUUID(entNameList));
            EntityType type  = ent.getType();
            switch(modOption)
            {
                case "age":// villager/zombie specific
                    // adult/baby[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("age"); }
                    String age = args[3].toLowerCase();
                    switch(type)
                    {
                        case VILLAGER:
                            //DEBUG
                            if(debug.entities())
                            { debug.message("villagerAge"); }
                            switch(age)
                            {
                                case "adult": ((Villager) ent).setAdult(); break;
                                case "baby": ((Villager) ent).setBaby(); break;
                                default: return false;
                            }
                            break;
                        case ZOMBIE:
                            //DEBUG
                            if(debug.entities())
                            { debug.message("zombieAge"); }
                            switch(age)
                            {
                                case "adult": ((Zombie) ent).setAdult(); break;
                                case "baby": ((Zombie) ent).setBaby(); break;
                                default: return false;
                            }
                            break;
                        default: return false;
                    }
                    break;
                case "agelock":// villager specific
                    // true/false[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("ageLock"); }
                    switch(args[3].toLowerCase())
                    {
                        case "true": ((Villager) ent).setAgeLock(true); break;
                        case "false": ((Villager) ent).setAgeLock(false); break;
                        default: return false;
                    }
                    break;
                case "breedable":// villager specific
                    // true/false[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("breedable"); }
                    switch(args[3].toLowerCase())
                    {
                        case "true": ((Villager) ent).setBreed(true); break;
                        case "false": ((Villager) ent).setBreed(false); break;
                        default: return false;
                    }
                    break;
                case "canpickupitems":
                    // true/false[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("canPickUpItems"); }
                    switch(args[3].toLowerCase())
                    {
                        case "true": ent.setCanPickupItems(true); break;
                        case "false": ent.setCanPickupItems(false); break;
                        default: return false;
                    }
                    break;
                case "color":
                    // hexCode[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("color"); }
                    if(isValidColorCode(args[3])) { ent.setCustomName(ChatColor.of(args[3]) + ent.getName()); break; }
                    else { return false; }
                case "hasai":
                    // true/false[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("hasAI"); }
                    switch(args[3].toLowerCase())
                    {
                        case "true": ent.setAI(true); break;
                        case "false": ent.setAI(false); break;
                        default: return false;
                    }
                    break;
                case "hasgravity":
                    //DEBUG
                    if(debug.entities())
                    { debug.message("hasGravity"); }
                    // true/false[3]
                    switch(args[3].toLowerCase())
                    {
                        case "true": ent.setGravity(true); break;
                        case "false": ent.setGravity(false); break;
                        default: return false;
                    }
                    break;
                case "isaware":
                    // true/false[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("isAware"); }
                    boolean boo = Boolean.parseBoolean(args[3]);
                    switch(type)
                    {
                        case VILLAGER: ((Villager) ent).setAware(boo); break;
                        case ZOMBIE: ((Zombie) ent).setAware(boo); break;
                        case SKELETON: ((Skeleton) ent).setAware(boo); break;
                        default: return false;
                    }
                    break;
                case "isgliding":
                    // true/false[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("isGliding"); }
                    switch(args[3].toLowerCase())
                    {
                        case "true": ent.setGliding(true); break;
                        case "false": ent.setGliding(false); break;
                        default: return false;
                    }
                    break;
                case "isglowing":
                    // true/false[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("isGlowing"); }
                    switch(args[3].toLowerCase())
                    {
                        case "true": ent.setGlowing(true); break;
                        case "false": ent.setGlowing(false); break;
                        default: return false;
                    }
                    break;
                case "isgod":
                    // true/false[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("isGod"); }
                    switch(args[3].toLowerCase())
                    {
                        case "true": ent.setInvulnerable(true); break;
                        case "false": ent.setInvulnerable(false); break;
                        default: return false;
                    }
                    break;
                case "isinvisible":
                    // true/false[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("isInvisible"); }
                    switch(args[3].toLowerCase())
                    {
                        case "true": ent.setInvisible(true); break;
                        case "false": ent.setInvisible(false); break;
                        default: return false;
                    }
                    break;
                case "issilent":
                    // true/false[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("isSilent"); }
                    switch(args[3].toLowerCase())
                    {
                        case "true": ent.setSilent(true); break;
                        case "false": ent.setSilent(false); break;
                        default: return false;
                    }
                    break;
                case "isswimming":
                    // true/false[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("isSwimming"); }
                    switch(args[3].toLowerCase())
                    {
                        case "true": ent.setSwimming(true); break;
                        case "false": ent.setSwimming(false); break;
                        default: return false;
                    }
                    break;
                case "profession":
                    // profession[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("villager profession"); }
                    if(ent.getType().equals(EntityType.VILLAGER)) {ent = villagerProfession(args[3], ent); break; }
                    else { return false; }
                case "addrecipes":
                    // serializedRecipe[3] // args[4] // SET ME UP
                    //DEBUG
                    if(debug.entities())
                    { debug.message("addRecipes"); }
                    return false;
                case "resetrecipes":
                    // serializedRecipe[3] // args[4] // SET ME UP
                    //DEBUG
                    if(debug.entities())
                    { debug.message("resetRecipes"); }
                    return false;
                case "type":
                    // type[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("type"); }
                    if(ent.getType().equals(EntityType.VILLAGER)) { ent = villagerType(args[3], ent); break; }
                    else { return false; }
                case "name":
                    // newName[3] // args[4] // SET ME UP
                    //DEBUG
                    if(debug.entities())
                    { debug.message("name"); }
                    return false;
                case "nameisvisible":
                    // true/false[3]
                    //DEBUG
                    if(debug.entities())
                    { debug.message("nameVisible"); }
                    switch(args[3].toLowerCase())
                    {
                        case "true": ent.setSwimming(true); break;
                        case "false": ent.setSwimming(false); break;
                        default: return false;
                    }
                    break;
                // pets // armour // item in hand // age lock // trade refresh rate in hours //
                default: return false;
            }
            main.entities().resetEntity(ent);
            return true;
        }
        return false;
    }
}
