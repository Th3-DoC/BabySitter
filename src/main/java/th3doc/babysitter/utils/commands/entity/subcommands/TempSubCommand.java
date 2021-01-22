package th3doc.babysitter.utils.commands.entity.subcommands;


import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.commands.manager.SubCommand;
import th3doc.babysitter.utils.debug.Debug;

import java.util.List;

public class TempSubCommand extends SubCommand
{
    private enum Args
    {
        EDIT_SECTION("edit-section"), ADD_SECTION("add-section"), REMOVE_SECTION("remove-section"), ACTIVATE_GIFTS("activate-gifts");
        
        public String txt;
        Args(String txt) { this.txt = txt; }
    }
    
    private enum ThisChat
    {
        SECTION_EXISTS("Section Already Exists"), GUI_OPEN("GUI Open, Items Will Save When Closed"), SECTION_REMOVED("Section Removed"),
        INVALID_SECTION("Invalid Section");
        
        public String txt;
        ThisChat(String txt) { this.txt = txt; }
    }
    
    final private Main main;
    final private Debug debug;
    private AdminPlayer player;
    public TempSubCommand(Main main) { this.main = main;this.debug = main.debug(); }
    // command // gift[0] // editSection[1]-addSection[1]-removeSection[1]-activateGifts[1] // true?false[2]-section[2]-rank[2] // rankSection[3]
    @Override
    public String getName()
    {
        return "Gift";
    }
    
    @Override
    public String getDescription()
    {
        return "Manage Gift Sections.";
    }
    
    @Override
    public String getSyntax()
    {
        return "\n /rewards gift <option> <section> \n /rewards gift activate-gifts <true?false>";
    }
    
    @Override
    public boolean perform(BasicPlayer player, String[] args)
    {
        // rewards // gift[0] // option[1] // section[2] //
        // rewards // gift[0] // activate-gifts[1] // true?false[2] //
        return cancel();
    }
    
    private boolean confirm(String message) { player.message(message); return true; }
    private boolean cancel() { player.message(Utils.Chat.INVALID_CMD.txt + getSyntax()); return false; }
    
    
    @Override
    public List<String> tabComplete1(String[] args) {
        return null;
    }
    
    @Override
    public List<String> tabComplete2(String[] args) {
        return null;
    }
    
    @Override
    public List<String> tabComplete3(String[] args) {
        return null;
    }
    
    @Override
    public List<String> tabComplete4(String[] args) {
        return null;
    }
    
    @Override
    public List<String> tabComplete5(String[] args) {
        return null;
    }
    
    @Override
    public List<String> tabComplete6(String[] args) {
        return null;
    }
    
    @Override
    public List<String> tabComplete7(String[] args) {
        return null;
    }
    
    @Override
    public List<String> tabComplete8(String[] args) {
        return null;
    }
    
    @Override
    public List<String> tabComplete9(String[] args) {
        return null;
    }
    
    @Override
    public List<String> tabComplete10(String[] args) {
        return null;
    }
    
    @Override
    public List<String> tabComplete11(String[] args) {
        return null;
    }
}
