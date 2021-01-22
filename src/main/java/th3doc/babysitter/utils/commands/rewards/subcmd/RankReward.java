package th3doc.babysitter.utils.commands.rewards.subcmd;

import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.commands.manager.SubCommand;
import th3doc.babysitter.utils.debug.Debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RankReward extends SubCommand
{
    private enum Args { EDIT_SECTION, ADD_SECTION, REMOVE_SECTION }
    
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
    public RankReward(Main main) { this.main = main;this.debug = main.debug(); }
    @Override
    public String getName()
    {
        return "Rank_Rewards";
    }
    
    @Override
    public String getDescription()
    {
        return "Manage Rank Sections.";
    }
    
    @Override
    public String getSyntax()
    {
        return "\n /rewards rank_rewards <option> <rank> <section>";
    }
    
    @Override
    public boolean perform(BasicPlayer player, String[] args)
    {
        // rewards // rank-reward[0] // option[1] // group[2] // section[3] //
        
        //check rank exists else cancel
        //map<path, itemstack[]>
        if(args.length == 4)
        {
            this.player = player;
            List<String> list = main.players().rewards().getRewardSections();
            switch(Args.valueOf(args[1].toUpperCase()))
            {
                case EDIT_SECTION:
                    //edit reward section
                    if(list != null && list.contains(args[2] + "." + args[3]))// contains name
                    {
                        ItemStack[] items = main.players().rewards().getRewardItems(args[2], args[3]);
                        //open GUI name = args[3] + Gift-Section size = 27 edit = true
                        return confirm(player, ThisChat.GUI_OPEN.txt);
                    } else { return confirm(player, ThisChat.INVALID_SECTION.txt); }
                case ADD_SECTION:
                    //add reward section
                    if(list == null)
                    {
                        //open GUI name = args[2].args[3] + Gift-Section size = 27 edit = true
                        return confirm(player, ThisChat.GUI_OPEN.txt);
                    } else { return confirm(player, ThisChat.SECTION_EXISTS.txt); }
                case REMOVE_SECTION:
                    //remove reward section
                    if(list != null && list.contains(args[2] + "." + args[3]))
                    {
                        main.players().rewards().removeRewardSection(args[3]);
                        return confirm(player, ThisChat.SECTION_REMOVED.txt);
                    } else { return confirm(player, ThisChat.INVALID_SECTION.txt); }
                default: return cancel(player);
            }
        } else { return cancel(player); }
    }
    
    @Override
    public List<String> tabComplete1(String[] args) {
        return Arrays.asList("Remove_Section", "Add_Section", "Edit_Section");
    }
    
    @Override
    public List<String> tabComplete2(String[] args) {
        return main.utils().getServerGroups();
    }
    
    @Override
    public List<String> tabComplete3(String[] args) {
        List<String> options = new ArrayList<>();
        List<String> temp = main.players().rewards().getRewardSections();
        for(String rankPath : temp)
        {
            String[] rankName = rankPath.split("\\.");
            if(args[2].equalsIgnoreCase(rankName[0])) { options.add(rankName[1]); }
        }
        return options;
    }
}
