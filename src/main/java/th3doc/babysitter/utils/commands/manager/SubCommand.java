package th3doc.babysitter.utils.commands.manager;

import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {
    /*
     * {         // BASIC ENUMS & VARS
     *
     //          private enum Args { DEFAULT }
     //
     //          private enum ThisChat
     //          {
     //              DEFAULT("default");
     //
     //              public String txt;
     //              ThisChat(String txt) { this.txt = txt; }
     //          }
     //
     //          final private Main main;
     //          final private Debug debug;
     //          private Players player;
     //          public SubCommand(Main main) { this.main = main;this.debug = main.debug(); }
     * }
     */
    
    
    
    public abstract String getName();
    
    public abstract String getDescription();
    
    public abstract String getSyntax();
    /*
     * {        //SYNTAX BASICS
     *
     //         return "\n /rewards gift <option> <section> \n /rewards gift activate-gifts <true?false>";
     *
     * }
     */
    
    
    
    /**
     * Execute Code For This SubCommand
     *
     * @param player executing the command
     * @param args sent by the player
     * @return execute the command true?false
     */
    public abstract boolean perform(BasicPlayer player, String[] args);
    /*
     * {        //PERFORM COMMAND BASICS
     *
     //         // rewards // gift[0] // activate-gifts[1] // true?false[2] //
     //         if(args.length == 3)
     //         {
     //             this.player = player;
     //             switch(Args.valueOf(args[1].toUpperCase))
     //             {
     //                 case DEFAULT:
     //                 default: return cancel(player);
     //             }
     //         } else { return cancel(player); }
     *
     * }
     */

    
    
    public List<String> tabComplete1(String[] args) { return new ArrayList<>(); }
    
    public List<String> tabComplete2(String[] args) { return new ArrayList<>(); }
    
    public List<String> tabComplete3(String[] args) { return new ArrayList<>(); }
    
    public List<String> tabComplete4(String[] args) { return new ArrayList<>(); }
    
    public List<String> tabComplete5(String[] args) { return new ArrayList<>(); }
    
    public List<String> tabComplete6(String[] args) { return new ArrayList<>(); }
    
    public List<String> tabComplete7(String[] args) { return new ArrayList<>(); }
    
    public List<String> tabComplete8(String[] args) { return new ArrayList<>(); }
    
    public List<String> tabComplete9(String[] args) { return new ArrayList<>(); }
    
    public List<String> tabComplete10(String[] args) { return new ArrayList<>(); }
    
    public List<String> tabComplete11(String[] args) { return new ArrayList<>(); }
    
    public boolean confirm(BasicPlayer player, String message) { player.message(message); return true; }
    public boolean cancel(BasicPlayer player) { player.message(Utils.Chat.INVALID_CMD.txt + "\n" + getSyntax()); return false; }
}
