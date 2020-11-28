package th3doc.babysitter.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.Perm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GiveCommand implements CommandExecutor, TabCompleter {

    private Main main;
    public GiveCommand(Main main) { this.main = main; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }
        Player p = (Player) sender;

        if (p.hasPermission(Perm._giveCommand.txt))
        {
            if (args.length <= 1
                    || (args.length >= 4 && !args[1].contains(Material.ENCHANTED_BOOK.name()))
                    || (!(main.getServer().getPlayer(args[0]) instanceof Player))
                    || Material.getMaterial(args[1]) == null)
            {
                p.sendMessage(Chat._invalidGive.txt);
                return false;
            } else
            {
                int amount = 1;
                if (args.length == 3 && !args[1].contains(Material.ENCHANTED_BOOK.name()))
                {
                    try { amount = Integer.parseInt(args[2]); }
                    catch (NumberFormatException e) { p.sendMessage(Chat._invalidGive.txt); }
                }
                if (args.length >= 4 || args[1].contains(Material.ENCHANTED_BOOK.name()))
                {
                    if (args.length > 4) { p.sendMessage(Chat._invalidGive.txt); return false; }
                    else { p.sendMessage("Enchanted Books Coming !"); return false; }
                }
                Material material = Material.getMaterial(args[1]);
                ItemStack item = new ItemStack(material, amount);
                main.getServer().getPlayer(args[0]).getInventory().addItem(item);
            }
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
        if (p.hasPermission(Perm._giveCommand.txt)) {

            if (args.length == 1) {
                List<String> players = new ArrayList<>();
                for (Player player : main.getServer().getOnlinePlayers()) {
                    players.add(player.getName());
                }
                StringUtil.copyPartialMatches(args[0], players, tabComplete);
                Collections.sort(tabComplete);
            }
            if (args.length == 2) {
                List<String> items = new ArrayList<>();
                for (Material material : Material.values()) {
                    items.add(material.name());
                }
                StringUtil.copyPartialMatches(args[1], items, tabComplete);
                Collections.sort(tabComplete);
            }
            if (args.length == 3 && args[1].contains(Material.ENCHANTED_BOOK.name())) {
                List<String> items = new ArrayList<>();
                items.add("UNBREAKING"); items.add("FORTUNE");
                StringUtil.copyPartialMatches(args[2], items, tabComplete);
                Collections.sort(tabComplete);
            }
            return tabComplete;
        }
        return null;
    }
}
