package th3doc.babysitter.utils.commands.entity;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.util.StringUtil;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.npc.Entities;
import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EntityCommand implements CommandExecutor, TabCompleter {//TODO add rotate entity
    
    //VARIABLES
    final private Main main;
    
    
    //CONSTRUCTOR
    public EntityCommand(Main main) { this.main = main; }
    
    
    // command // delete[0] // name[1]
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
//        Players player = main.getPlayer(((Player) sender).getUniqueId());
        if(sender instanceof AdminPlayer)
        {
            Entities entities = main.entities();
            AdminPlayer player = (AdminPlayer) sender;
            if(player.hasPermission(Utils.Perm.ENTITY_CMD.txt))
            {
                switch(args[0].toLowerCase())
                {
                    case "spawn":
                        Block block = player.targetBlock(10);
                        if(block != null)
                        {
                            if(entities.spawnEntity(player, block, args)) { return true; }
                            else { player.message(Utils.Chat.INVALID_CMD.txt); return false; }
                        }
                        else { player.message(Utils.Chat.INVALID_TARGET_BLOCK.txt); return false; }
                        
                    case "delete":
                        if(args.length == 2)
                        {
                            if(entities.isEntity(entities.getUUID(args[1])) != null) { entities.removeEntity(args[1]);return true; }
                            else { player.message(Utils.Chat.INVALID_CMD.txt);return false; }
                        }
                        else { return false; }
                    case "modify":
                        if(entities.modifyEntity(args)) { return true; }
                        else { player.message(Utils.Chat.INVALID_CMD.txt); return false; }
                }
            }
        }
        return false;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender instanceof Player)) { return null; }
        final Player p = (Player) sender;
        final List<String> tabComplete = new ArrayList<>();
    
        if (p.hasPermission(Utils.Perm.ENTITY_CMD.txt))
        {
            if(args.length == 1)
            {
                List<String> options = new ArrayList<>();
                options.add("Delete"); options.add("Modify");
                options.add("Spawn");
                StringUtil.copyPartialMatches(args[0], options, tabComplete);
                Collections.sort(tabComplete);
            }
            /**
             * DELETE FUNCTION
             */
            if(args[0].toLowerCase().equals("delete"))
            {
                if(args.length == 2)
                {
                    List<String> entity = Arrays.asList(main.entities().getNameList().toArray(new String[0]));
                    StringUtil.copyPartialMatches(args[1], entity, tabComplete);
                    Collections.sort(tabComplete);
                }
            }
            /**
             * SPAWN FUNCTION
             */
            if(args[0].toLowerCase().equals("spawn"))
            {
                String recipe = "buying:amount:selling:amount:selling:amount:maxUses:priceMultiplier";
                // command // villager[1] // color[2] // name[3] // visible[4]
                // invulnerable[5] // ai[6] // age[7] // type[8] // profession[9]
                // recipes[10]++
    
                // command // zombie[1] // color[2] // name[3] // visible[4]
                // invulnerable[5] // ai[6] // age[7] // recipes[8]++
    
                // command // zombie[1] // color[2] // name[3] // visible[4]
                // invulnerable[5] // ai[6] // recipes[7]++
    
                if(args.length == 2)
                {
                    List<String> entities = Arrays.asList("Villager", "Zombie", "Skeleton");
                    StringUtil.copyPartialMatches(args[1], entities, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 3)
                {
                    List<String> color = Arrays.asList("-ColourCodes-", "?null?", "#FF0000", "#F00");
                    StringUtil.copyPartialMatches(args[2], color, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 4)
                {
                    List<String> name = Collections.singletonList("-UniqueName-");
                    StringUtil.copyPartialMatches(args[3], name, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 5)
                {
                    List<String> visible = Arrays.asList("-VisibleName-", "True", "False");
                    StringUtil.copyPartialMatches(args[4], visible, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 6)
                {
                    List<String> god = Arrays.asList("-God-", "True", "False");
                    StringUtil.copyPartialMatches(args[5], god, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 7)
                {
                    List<String> ai = Arrays.asList("-AI-", "True", "False");
                    StringUtil.copyPartialMatches(args[6], ai, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 8)
                {
                    if(args[1].toLowerCase().equals(EntityType.VILLAGER.name().toLowerCase()) ||
                       args[1].toLowerCase().equals(EntityType.ZOMBIE.name().toLowerCase()))
                    {
                        List<String> adult = Arrays.asList("Adult", "Baby");
                        StringUtil.copyPartialMatches(args[7], adult, tabComplete);
                    }
                    else { tabComplete.add(recipe); }
                    Collections.sort(tabComplete);
                }
                if(args.length == 9)
                {
                    if(args[1].toLowerCase().equals(EntityType.VILLAGER.name().toLowerCase()))
                    {
                        List<String> type = new ArrayList<>();
                        for(Villager.Type villagerType : Villager.Type.values())
                        {
                            String vType = villagerType.name().substring(0, 1).toUpperCase() +
                                           villagerType.name().substring(1).toLowerCase();
                            type.add(vType);
                        }
                        StringUtil.copyPartialMatches(args[8], type, tabComplete);
                    }
                    else { tabComplete.add(recipe); }
                    Collections.sort(tabComplete);
    
                }
                if(args.length == 10)
                {
                    if(args[1].toLowerCase().equals(EntityType.VILLAGER.name().toLowerCase()))
                    {
                        List<String> prof = new ArrayList<>();
                        for(Villager.Profession vProf : Villager.Profession.values())
                        {
                            String pType = vProf.name().substring(0, 1).toUpperCase() +
                                           vProf.name().substring(1).toLowerCase();
                            prof.add(pType);
                        }
                        StringUtil.copyPartialMatches(args[9], prof, tabComplete);
                    }
                    else { tabComplete.add(recipe); }
                    Collections.sort(tabComplete);
        
                }
                if(args.length >= 11)
                {
                    tabComplete.add(recipe);
                    Collections.sort(tabComplete);
                }
                return tabComplete;
            }
            /**
             * MODIFY FUNCTION
             */
            if(args[0].toLowerCase().equals("modify"))
            {
                // modify[0] // name[1] // modOption[2] // optionParams[3]
                // name[1]
                if(args.length == 2)
                {
                    List<String> entity = Arrays.asList(main.entities().getNameList().toArray(new String[0]));
                    StringUtil.copyPartialMatches(args[1], entity, tabComplete);
                    Collections.sort(tabComplete);
                }
                // modOption[2]
                if(args.length == 3) {
                    List<String> modOption = Arrays.asList("Age", "AgeLock", "Breedable", "CanPickUpItems", "Color",  "HasAI", "HasGravity", "IsAware", "IsGliding",
                                                           "IsGlowing", "IsGod", "IsInvisible", "IsSilent", "IsSwimming", "Profession", "AddRecipes", "Type", "Name",
                                                           "NameIsVisible");
                    StringUtil.copyPartialMatches(args[2], modOption, tabComplete);
                    Collections.sort(tabComplete);
                }
                // modSettings[3]
                if(args.length == 4)
                {
                    List<String> modSetting;
                    switch(args[2].toLowerCase())
                    {
                        case "age": modSetting = Arrays.asList("Adult", "Baby"); break;
                        case "color": modSetting = Collections.singletonList("-EnterHexCode-"); break;
                        case "profession": modSetting = Arrays.asList("armorer", "butcher", "cartographer", "cleric", "farmer", "fisherman", "fletcher",
                                                                      "leatherworker", "librarian", "mason", "nitwit", "shepherd", "toolsmith", "weaponsmith"); break;
                        case "addrecipes": modSetting = Collections.singletonList("buying:amount:selling:amount:selling:amount:maxUses:priceMultiplier"); break;
                        case "resetrecipes": modSetting = Collections.singletonList("CONFIRM"); break;
                        case "type": modSetting = Arrays.asList("desert", "jungle", "savanna", "snow", "swamp", "taiga"); break;
                        case "name": modSetting = Collections.singletonList("-UniqueName-"); break;
                        default: modSetting = Arrays.asList("True", "False");
                    }
                    StringUtil.copyPartialMatches(args[3], modSetting, tabComplete);
                    Collections.sort(tabComplete);
                }
//                if(args.length == 9)
//                {
//                    List<String> leash = Arrays.asList("?null?", "Cat", "Wolf", "Llama", "Rabbit");
//                    StringUtil.copyPartialMatches(args[8], leash, tabComplete);
//                    Collections.sort(tabComplete);
//                }
            }
        }
        return tabComplete;
    }
}
