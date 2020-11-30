package th3doc.babysitter.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.gui.InvGUI;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.InvType;
import th3doc.babysitter.player.data.Perm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SeeInventoryCommand implements CommandExecutor, TabCompleter {

    //CONSTRUCTOR
    private Main main;
    public SeeInventoryCommand(Main main) { this.main = main; }
    private List<String> inventoryTypes = Arrays.asList(InvType.Inventory.name(), InvType.EnderChest.name());

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //CHECK INSTANCE OF PLAYER
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().info(Chat._noConsole.txt);
            return false;
        }
        Player p = (Player) sender;

        //CHECK PERMISSION
        if (p.hasPermission(Perm._invSeeCommand.txt))
        {
            //CHECK PLAYER IS VALID
            //CHECK ARGUMENT LENGTH IS VALID /SEE <PLAYER> <INVENTORY TYPE> <EDIT>
            if (args.length < 2
                    || args.length > 3
                    || (!(main.getServer().getPlayer(args[0]) instanceof Player))
                    || (args[0].contains(p.getName()) && !p.hasPermission(Perm._seeBypass.txt))
                    || !inventoryTypes.contains(args[1]))
            {
                p.sendMessage(Chat._invalidViewerCommand.txt);
                return false;
            }
            //CHECK SECOND ARGUMENT IS VALID
            String inv = args[1];
            //CHECK THIRD ARGUMENT EXISTS
            boolean edit = false;
            if (args.length == 3)
            {
                //CHECK THIRD ARGUMENT IS VALID
                if (args[2].equals(InvType.EditMode.name())
                        &&  p.hasPermission(Perm._invEdit.txt)) { edit = true; }
                else { p.sendMessage(Chat._invalidViewerCommand.txt); return false; }
            }
            new InvGUI(main, p, inv, main.getServer().getPlayer(args[0]), edit);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> tabComplete = new ArrayList<>();//TAB COMPLETE LIST

        if (!(sender instanceof Player)) {
            return null;
        }
        Player p = (Player) sender;
        if (p.hasPermission(Perm._invSeeCommand.txt)) {
            if (args.length == 1) {
                List<String> players = new ArrayList<>();
                for (Player player : main.getServer().getOnlinePlayers()) {
                    players.add(player.getName());
                }
                StringUtil.copyPartialMatches(args[0], players, tabComplete);
                Collections.sort(tabComplete);
            }
            if (args.length == 2) {
                StringUtil.copyPartialMatches(args[1], inventoryTypes, tabComplete);
                Collections.sort(tabComplete);
            }
            if (args.length == 3 && p.hasPermission(Perm._invEdit.txt)) {
                List<String> edit = new ArrayList<>();
                edit.add(0, InvType.EditMode.name());
                StringUtil.copyPartialMatches(args[2], edit, tabComplete);
                Collections.sort(tabComplete);
            }
            return tabComplete;
        }
        return Collections.emptyList();
    }
}
