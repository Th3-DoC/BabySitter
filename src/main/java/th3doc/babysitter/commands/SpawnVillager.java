package th3doc.babysitter.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.util.StringUtil;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.Perm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpawnVillager implements CommandExecutor, TabCompleter {
    
    //VARIABLES
    final private Main main;
    
    
    //CONSTRUCTOR
    public SpawnVillager(Main main) { this.main = main; }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender instanceof Player))
        {
            main.getLogger().info(Chat._noConsole.txt);
            return false;
        }
        Player p = (Player) sender;
    
        
        // command // villager[0] // invulnerable[1] // ai[2] // type[3]
        // profession[4] // color[5] // name[6] // visible[7] // age[8]
        // silent[9] // silly extras[10] // leash[11] // trades[12]++
        if(p.hasPermission(Perm._spawnCommand.txt))
        {
            //variables to check
            Block block = p.getTargetBlockExact(10);
            if(block != null)
            {
                Location targetBlock = block.getLocation();
                if(args[0].toLowerCase().equals(EntityType.VILLAGER.name().toLowerCase()))
                {
                    //args[0]
                    p.sendMessage("args[0]");
                    Villager villager = p.getWorld().spawn(targetBlock.add(0.5, 1, 0.5),
                                                           Villager.class);
                    villager.setTarget(p);
                    villager.setTarget(null);
                    villager.setBreed(false);
                    villager.setCanPickupItems(false);
                    villager.setRemoveWhenFarAway(false);
                    //args[1]
                    p.sendMessage("args[1]");
                    villager.setInvulnerable(Boolean.parseBoolean(args[1]));
                    //args[2]
                    p.sendMessage("args[2]");
                    villager.setAI(Boolean.parseBoolean(args[2]));
                    //args[3]
                    p.sendMessage("args[3]");
                    Villager.Type type;
                    switch(args[3].toLowerCase())
                    {
                        case "desert":
                            type = Villager.Type.DESERT;
                            break;
                        case "jungle":
                            type = Villager.Type.JUNGLE;
                            break;
                        case "savanna":
                            type = Villager.Type.SAVANNA;
                            break;
                        case "snow":
                            type = Villager.Type.SNOW;
                            break;
                        case "swamp":
                            type = Villager.Type.SWAMP;
                            break;
                        case "taiga":
                            type = Villager.Type.TAIGA;
                            break;
                        default:
                            type = Villager.Type.PLAINS;
                    }
                    villager.setVillagerType(type);
                    //args[4]
                    p.sendMessage("args[4]");
                    Villager.Profession profession;
                    switch(args[4].toLowerCase())
                    {
                        case "armorer":
                            profession = Villager.Profession.ARMORER;
                            break;
                        case "butcher":
                            profession = Villager.Profession.BUTCHER;
                            break;
                        case "cartographer":
                            profession = Villager.Profession.CARTOGRAPHER;
                            break;
                        case "cleric":
                            profession = Villager.Profession.CLERIC;
                            break;
                        case "farmer":
                            profession = Villager.Profession.FARMER;
                            break;
                        case "fisherman":
                            profession = Villager.Profession.FISHERMAN;
                            break;
                        case "fletcher":
                            profession = Villager.Profession.FLETCHER;
                            break;
                        case "leatherworker":
                            profession = Villager.Profession.LEATHERWORKER;
                            break;
                        case "librarian":
                            profession = Villager.Profession.LIBRARIAN;
                            break;
                        case "mason":
                            profession = Villager.Profession.MASON;
                            break;
                        case "nitwit":
                            profession = Villager.Profession.NITWIT;
                            break;
                        case "shepherd":
                            profession = Villager.Profession.SHEPHERD;
                            break;
                        case "toolsmith":
                            profession = Villager.Profession.TOOLSMITH;
                            break;
                        case "weaponsmith":
                            profession = Villager.Profession.WEAPONSMITH;
                            break;
                        default:
                            profession = Villager.Profession.NONE;
                    }
                    villager.setProfession(profession);
                    villager.setVillagerLevel(5);
                    villager.setVillagerExperience(100);
                    //args[5]
                    p.sendMessage("args[5]");
                    String colorCode = "";
                    if(isValidHexCode(args[5]))
                    {
                        colorCode = args[5];
                    }
                    //args[6]
                    p.sendMessage("args[6]");
                    if(!args[6].toLowerCase().equals("null"))
                    {
                        char f = args[6].charAt(0);
                        villager.setCustomName(ChatColor.COLOR_CHAR +
                                               colorCode +
                                               f +
                                               args[6]);
                    }
                    //args[7]
                    p.sendMessage("args[7]");
                    villager.setCustomNameVisible(Boolean.parseBoolean(args[7]));
                    //args[8]
                    p.sendMessage("args[8]");
                    if(args[8].toLowerCase().equals("baby"))
                    {
                        villager.setBaby();
                    }
                    else
                    {
                        villager.setAdult();
                    }
                    villager.setAgeLock(true);
                    //args[9]
                    p.sendMessage("args[9]");
                    villager.setSilent(Boolean.parseBoolean(args[9]));
                    //args[10]
                    p.sendMessage("args[10]");
                    switch(args[10].toLowerCase())
                    {
                        case "gliding":
                            villager.setGliding(true);
                            break;
                        case "glowing":
                            villager.setGlowing(true);
                            break;
                        case "gravity":
                            villager.setGravity(true);
                            break;
                        case "swimming":
                            villager.setSwimming(true);
                            break;
                        case "invisible":
                            villager.setInvisible(true);
                            break;
                    }
                    //args[11]
                    p.sendMessage("args[11]");
                    switch(args[11].toLowerCase())
                    {
                        case "cat":
                            Cat cat = p.getWorld().spawn(villager.getLocation().add(2, 0, 2),
                                                         Cat.class);
                            cat.setInvulnerable(true);
                            villager.setLeashHolder(cat);
                            break;
                        case "wolf":
                            Wolf wolf = p.getWorld().spawn(villager.getLocation().add(2, 0, 2),
                                                           Wolf.class);
                            wolf.setInvulnerable(true);
                            villager.setLeashHolder(wolf);
                            break;
                        case "llama":
                            Llama llama = p.getWorld().spawn(villager.getLocation().add(2, 0, 2),
                                                             Llama.class);
                            llama.setInvulnerable(true);
                            villager.setLeashHolder(llama);
                            break;
                        case "rabbit":
                            Rabbit rabbit = p.getWorld().spawn(villager.getLocation().add(2, 0, 2),
                                                               Rabbit.class);
                            rabbit.setInvulnerable(true);
                            villager.setLeashHolder(rabbit);
                            break;
                    }
                    //args[12]++
                    p.sendMessage("args[12]++");
                    if(args.length >= 13)
                    {
                        List<MerchantRecipe> recipes = new ArrayList<>();
                        //selling:uses:buying:amount:buying:amount
                        for(int i = 12; i < (args.length); i++)
                        {
                            p.sendMessage("args[" + i + "]");
                            String[] str = args[i].split(":");
                            if(Material.getMaterial(str[0]) != null)
                            {
                                p.sendMessage(str[0] + " " + str[1]);
                                MerchantRecipe recipe = new MerchantRecipe(new ItemStack(Material.getMaterial(str[0])),
                                                                           Integer.parseInt(str[1]));
                                for(int r = 2; r < 5; r++)
                                {
                                    if(Material.getMaterial(str[r]) != null && (r % 2) == 0)
                                    {
                                        p.sendMessage(str[r] + " " + str[r+1]);
                                        recipe.addIngredient(new ItemStack(Material.getMaterial(str[r]),
                                                                           Integer.parseInt(str[r + 1])));
                                    }
                            
                                }
                                recipes.add(recipe);
                                p.sendMessage("recipes added");
                            }
                    
                        }
                        villager.setRecipes(recipes);
                        p.sendMessage("recipes set");
                    }
            
                }
                //else if zombie?
            }
            else
            {
                p.sendMessage("Invalid Target Block");
            }
        }
        return false;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender instanceof Player)) { return null; }
        Player p = (Player) sender;
    
        
        // command // villager[0] // invulnerable[1] // ai[2] // type[3]
        // profession[4] // color[5] // name[6] // visible[7] // age[8]
        // silent[9] // silly extras[10] // leash[11] // trades[12]++
        if (p.hasPermission(Perm._spawnCommand.txt))
        {
            final List<String> tabComplete = new ArrayList<>();//TAB COMPLETE LIST
            final List<String> trueFalse = Arrays.asList("TRUE", "FALSE");
            if(args.length == 1)
            {
                List<String> entities = new ArrayList<>();
                entities.add(EntityType.VILLAGER.name());
        
                StringUtil.copyPartialMatches(args[0], entities, tabComplete);
                Collections.sort(tabComplete);
            }
            if(args.length == 2)
            {
                List<String> god = new ArrayList<>();
                god.add("?God?"); god.addAll(trueFalse);
                
        
                StringUtil.copyPartialMatches(args[1], god, tabComplete);
                Collections.sort(tabComplete);
            }
            if(args.length == 3)
            {
                List<String> ai = new ArrayList<>();
                ai.add("?AI?"); ai.addAll(trueFalse);
        
                StringUtil.copyPartialMatches(args[2], ai, tabComplete);
                Collections.sort(tabComplete);
            }
            if(args.length == 4)
            {
                List<String> type = new ArrayList<>();
                for(Villager.Type vType : Villager.Type.values())
                {
                    type.add(vType.name());
                }
        
                StringUtil.copyPartialMatches(args[3], type, tabComplete);
                Collections.sort(tabComplete);
            }
            if(args.length == 5)
            {
                List<String> prof = new ArrayList<>();
                for(Villager.Profession vProf : Villager.Profession.values())
                {
                    prof.add(vProf.name());
                }
        
                StringUtil.copyPartialMatches(args[4], prof, tabComplete);
                Collections.sort(tabComplete);
            }
            if(args.length == 6)
            {
                List<String> color = new ArrayList<>();
                color.add("?Colour#Codes?"); color.add("FF0000");
                color.add("F00");
        
                StringUtil.copyPartialMatches(args[5], color, tabComplete);
                Collections.sort(tabComplete);
            }
            if(args.length == 7)
            {
                List<String> name = Collections.singletonList("?Custom*Name?");
        
                StringUtil.copyPartialMatches(args[6], name, tabComplete);
                Collections.sort(tabComplete);
            }
            if(args.length == 8)
            {
                List<String> visible = new ArrayList<>();
                visible.add("?Visible*Name?"); visible.addAll(trueFalse);
        
                StringUtil.copyPartialMatches(args[7], visible, tabComplete);
                Collections.sort(tabComplete);
            }
            if(args.length == 9)
            {
                List<String> adult = new ArrayList<>();
                adult.add("Adult"); adult.add("Baby");
        
                StringUtil.copyPartialMatches(args[8], adult, tabComplete);
                Collections.sort(tabComplete);
            }
            // silent[9] // silly extras[10] // leash[11] // trades[12]++
            if(args.length == 10)
            {
                List<String> silent = new ArrayList<>();
                silent.add("?Silent?"); silent.addAll(trueFalse);
        
                StringUtil.copyPartialMatches(args[9], silent, tabComplete);
                Collections.sort(tabComplete);
            }
            if(args.length == 11)
            {
                List<String> extras = Arrays.asList("Gliding", "Glowing",
                                                    "Gravity", "Swimming",
                                                    "invisible");
        
                StringUtil.copyPartialMatches(args[10], extras, tabComplete);
                Collections.sort(tabComplete);
            }
            if(args.length == 12)
            {
                List<String> leash = Arrays.asList("Cat", "Wolf",
                                                   "Llama", "Rabbit");
        
                StringUtil.copyPartialMatches(args[11], leash, tabComplete);
                Collections.sort(tabComplete);
            }
            if(args.length >= 13)
            {
                tabComplete.add("buying:amount: selling:amount: selling:amount");
                Collections.sort(tabComplete);
            }
            return tabComplete;
        }
        return null;
    }
    
    private boolean isValidHexCode(String str)
    {
        // Regex to check valid hexadecimal color code.
        String regex = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        
        Pattern p = Pattern.compile(regex);
        if (str == null) {
            return false;
        }
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
