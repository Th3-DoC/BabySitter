package th3doc.babysitter.utils.commands.rewards.subcmd;

import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.commands.manager.SubCommand;
import th3doc.babysitter.utils.menu.Menu;
import th3doc.babysitter.utils.menu.menus.FirstJoinMenu;

import java.util.Collections;
import java.util.List;

public class FirstJoin extends SubCommand {
    private enum Args { EDIT_SECTION }
    
    private enum ThisChat {
        GUI_OPEN("GUI Open, Items Will Save When Closed");
        
        public String txt;
        
        ThisChat(String txt) { this.txt = txt; }
    }
    
    // command // gift[0] // editSection[1]-addSection[1]-removeSection[1]-activateGifts[1] // true?false[2]-section[2]-rank[2] // rankSection[3]
    @Override
    public String getName() {
        return "First_Join";
    }
    
    @Override
    public String getDescription() {
        return "Manage First Join Items.";
    }
    
    @Override
    public String getSyntax() {
        return "/rewards first_join <option>";
    }
    
    @Override
    public boolean perform(BasicPlayer player, String[] args) {
        // rewards // first-join[0] // option[1] //
        if(args.length == 2 && args[1].equalsIgnoreCase(Args.EDIT_SECTION.name())) {
            Menu menu = new FirstJoinMenu(player.getMenuUtil());
            menu.open();
            return true;
        }
        return cancel(player);
    }
    
    @Override
    public List<String> tabComplete1(String[] args) {
        return Collections.singletonList("Edit_Section");
    }
}
