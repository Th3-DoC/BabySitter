package th3doc.babysitter.utils.debug;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DebugMode implements CommandExecutor, TabCompleter {
    
    //VARIABLES
    final private Main main;
    private BasicPlayer player;
    
    
    //CONSTRUCTOR
    public DebugMode(Main main) { this.main = main; }
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player && sender.hasPermission(Utils.Perm.DEBUG.txt)) {
                main.debug().setDebug(args[0], args[1]);
            
        } else if(sender instanceof ConsoleCommandSender) { main.debug().setDebug(args[0], args[1]); }
        
        return false;
    }
    
    @Override
    public List< String > onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List< String > tabComplete = new ArrayList<>();
        if(sender.hasPermission(Utils.Perm.DEBUG.txt)) {
            if(args.length == 1) {
                List< String > section = new ArrayList<>();
                for(Debug.Paths path : Debug.Paths.values()) {
                    if(!path.txt.equalsIgnoreCase("debug.yml")) { section.add(path.txt); }
                }
                StringUtil.copyPartialMatches(args[0], section, tabComplete);
                Collections.sort(tabComplete);
            }
            if(args.length == 2) {
                List< String > boo = new ArrayList<>();
                boo.add("True"); boo.add("False");
                StringUtil.copyPartialMatches(args[1], boo, tabComplete);
                Collections.sort(tabComplete);
            }
        }
        return tabComplete;
    }
}
