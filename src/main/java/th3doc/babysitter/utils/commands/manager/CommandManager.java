package th3doc.babysitter.utils.commands.manager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CommandManager implements CommandExecutor, TabCompleter {
    final private List<SubCommand> subCommands;
    final private String permission;
    final private Main main;
    final private String syntax;
    private BasicPlayer player;
    
    public CommandManager(Main main, String permission, List<Object> subCommandList)
    {
        this.subCommands = new ArrayList<>();
        this.permission = permission;
        for(Object obj : subCommandList) { subCommands.add((SubCommand) obj); }
        this.main = main;
        // Syntax
        StringBuilder stringBuilder = new StringBuilder();
        for(SubCommand subCommand : subCommands) {
            String syntax = subCommand.getSyntax();
            stringBuilder.append(syntax).append("\n");
        }
        this.syntax = stringBuilder.toString();
    }
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            consoleCommandUse(main);
        } else {
            player = main.players().getCustomPlayer(((Player) sender).getUniqueId());
            
            if(player.hasPermission(permission)) {
                if(args.length > 0) {
                    SubCommand subCommand = checkSubCommands(args[0]);
                    if(subCommand != null) {
                        return subCommand.perform(player, args);
                    } else { return cancel(); }
                } else { return noSubCommandUse(player); }
            }
        }
        return false;
    }
    
    /*COMMAND EXECUTOR METHODS*/
    private SubCommand checkSubCommands(String arg) {
        for(SubCommand subCommand : subCommands) {
            if(arg.equalsIgnoreCase(subCommand.getName())) { return subCommand; }
        }
        return null;
    }
    
    public boolean consoleCommandUse(Main main) { main.getServer().getLogger().info(Utils.Chat._noConsole.txt); return false; }
    
    public boolean noSubCommandUse(BasicPlayer player) { return cancel(); }
    
    private boolean cancel() { player.message(Utils.Chat.INVALID_CMD.txt + getSyntax()); return false; }
    
    private String getSyntax() {
        
        return "*--------------------------------------* \n" + this.syntax + "*--------------------------------------*";
    }
    /*END COMMAND EXECUTOR*/
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> tabComplete = new ArrayList<>();
        if(sender.hasPermission(permission) && args.length > 0 && main.players().isCustomPlayerOnline(sender.getName())) {
            BasicPlayer player = main.players().getCustomPlayer(sender.getName());
            // args[0] subcommand list
            if(args.length == 1)
            {
                List<String> options = new ArrayList<>();
                for(SubCommand subCommand : subCommands) { options.add(subCommand.getName()); }
                StringUtil.copyPartialMatches(args[0], options, tabComplete);
                Collections.sort(tabComplete);
            }
            SubCommand subCommand = checkSubCommands(args[0]);
            if(subCommand != null) {
                if(args.length == 2) {
                    List<String> options = subCommand.tabComplete1(args);
                    StringUtil.copyPartialMatches(args[1], options, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 3) {
                    List<String> options = subCommand.tabComplete2(args);
                    StringUtil.copyPartialMatches(args[2], options, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 4) {
                    List<String> options = subCommand.tabComplete3(args);
                    StringUtil.copyPartialMatches(args[3], options, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 5) {
                    List<String> options = subCommand.tabComplete4(args);
                    StringUtil.copyPartialMatches(args[4], options, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 6) {
                    List<String> options = subCommand.tabComplete5(args);
                    StringUtil.copyPartialMatches(args[5], options, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 7) {
                    List<String> options = subCommand.tabComplete6(args);
                    StringUtil.copyPartialMatches(args[6], options, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 8) {
                    List<String> options = subCommand.tabComplete7(args);
                    StringUtil.copyPartialMatches(args[7], options, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 9) {
                    List<String> options = subCommand.tabComplete8(args);
                    StringUtil.copyPartialMatches(args[8], options, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 10) {
                    List<String> options = subCommand.tabComplete9(args);
                    StringUtil.copyPartialMatches(args[9], options, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 11) {
                    List<String> options = subCommand.tabComplete10(args);
                    StringUtil.copyPartialMatches(args[10], options, tabComplete);
                    Collections.sort(tabComplete);
                }
                if(args.length == 12) {
                    List<String> options = subCommand.tabComplete11(args);
                    StringUtil.copyPartialMatches(args[11], options, tabComplete);
                    Collections.sort(tabComplete);
                }
            }
            return tabComplete;
        }
        return tabComplete;
    }
}
