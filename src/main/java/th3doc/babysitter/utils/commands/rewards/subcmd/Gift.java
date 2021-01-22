package th3doc.babysitter.utils.commands.rewards.subcmd;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.commands.manager.SubCommand;
import th3doc.babysitter.utils.config.Config;
import th3doc.babysitter.utils.debug.Debug;
import th3doc.babysitter.utils.menu.Menu;
import th3doc.babysitter.utils.menu.menus.GiftMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gift extends SubCommand
{
    private enum Args { EDIT_SECTION, ADD_SECTION, REMOVE_SECTION, RESET_GIFTS}
    
    private enum ThisChat
    {
        SECTION_EXISTS("Section Already Exists"), GUI_OPEN("GUI Open, Items Will Save When Closed"), SECTION_REMOVED("Section Removed"),
        INVALID_SECTION("Invalid Section");
        
        public String txt;
        ThisChat(String txt) { this.txt = txt; }
    }
    
    final private Main main;
    final private Debug debug;
    private BasicPlayer player;
    public Gift(Main main)
    {
        this.main = main;
        this.debug = main.debug(); }
    // command // gift[0] // editSection[1]-addSection[1]-removeSection[1]-activateGifts[1] // true?false[2]-section[2]-rank[2] // rankSection[3]
    @Override
    public String getName()
    {
        return "Gifts";
    }
    
    @Override
    public String getDescription()
    {
        return "Manage Gift Sections.";
    }
    
    @Override
    public String getSyntax()
    {
        return "/rewards gift <option> <section> \n or ... /rewards gift reset_gifts <true?false>";
    }
    
    @Override
    public boolean perform(BasicPlayer player, String[] args)
    {
        // rewards // gift[0] // option[1] // section[2] //
        // rewards // gift[0] // reset_gifts[1] // true?false[2] //
        if(args.length == 3)
        {
            this.player = player;
            List<String> list = main.players().rewards().getGiftSections();
            ItemStack[] items;
            switch(Args.valueOf(args[1].toUpperCase()))
            {
                case REMOVE_SECTION:
                    //remove gift section
                    if(list.contains(args[2]))
                    {
                        main.players().rewards().removeGiftSection(args[2]);
                        return confirm(player, ThisChat.SECTION_REMOVED.txt);
                    } else { return confirm(player, ThisChat.INVALID_SECTION.txt); }
                case ADD_SECTION:
                    //add gift section
                    if(!list.contains(args[2]))
                    {
                        items = new ItemStack[27];
                        break;
                    } else { return confirm(player, ThisChat.SECTION_EXISTS.txt); }
                case EDIT_SECTION:
                    //edit gift section
                    if(list.contains(args[2]))
                    {
                        items = main.players().rewards().editGiftSection(args[2]);
                        break;
                    } else { return confirm(player, ThisChat.INVALID_SECTION.txt); }
                case RESET_GIFTS:
                    //reset all player list configs to receive new gifts
                    boolean args2 = Boolean.parseBoolean(args[2]);
                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            List<String> offlinePlayers = main.players().getCustomOfflinePlayerList();
                            for(String name : offlinePlayers)
                            {
                                Config config = main.players().getOfflinePlayerConfig(main.players().getCustomPlayerUUID(name).toString());
                                config.set(BasicPlayer.Paths.GIFTING_PLAYER.path, args2);
                                config.save();
                            }
                            
                        }
                    }.runTaskAsynchronously(main);
                    List<String> onlinePlayers = main.players().getCustomOnlinePlayers();
                    for(String name : onlinePlayers)
                    {
                        BasicPlayer p = main.players().getCustomPlayer(name);
                        p.config().set(BasicPlayer.Paths.GIFTING_PLAYER.path, args2);
                    }
                    main.players().rewards().setIsGifting(args2);
                    return confirm(player, "Setting Gift-Season " + args2);
                default: return cancel(player);
            }
            //open GUI name = args[2]
            Menu menu = new GiftMenu(player.getMenuUtil(), args[2], items);
            menu.open();
            return true;
        } else { return cancel(player); }
    }
    
    @Override
    public List<String> tabComplete1(String[] args) {
        return Arrays.asList("Reset_Gifts", "Edit_Section", "Add_Section", "Remove_Section");
    }
    
    @Override
    public List<String> tabComplete2(String[] args) {
        List<String> options = new ArrayList<>();
        if(args[0].equalsIgnoreCase("gifts") && args[1].equalsIgnoreCase("reset_gifts"))
        {
            options.add("True"); options.add("False");
        }
        else if(args[0].equalsIgnoreCase("gifts") && !main.players().rewards().getGiftSections().isEmpty())
        {
            options.addAll(main.players().rewards().getGiftSections());
        }
        return options;
    }
}
