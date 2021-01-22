package th3doc.babysitter.utils.commands.give;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.UtilsInterface;

import java.util.*;

public class GiveCommand implements CommandExecutor, TabCompleter, UtilsInterface
{

    final private Main main;
    public GiveCommand(Main main) { this.main = main; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    
        final Material material;
        int amount = 1;
        // command // material[0] // amt[1] // player[2]
        //CONSOLE USER
        if (!(sender instanceof Player)) {
            // 1
            final Player p;
            if(args.length == 3)
            {
                try
                {
                    material = Material.getMaterial(args[0].toUpperCase());
                    amount = Integer.parseInt(args[1]);
                    p = main.getServer().getPlayer(args[2]);
                    assert p != null;
                    return give(null, p, material, amount);
                }
                catch(IllegalArgumentException | NullPointerException ignored) { return consoleCancel(); }
            }
            else { return consoleCancel(); }
        }
        else
        {
            //PLAYER USER
            AdminPlayer player;
            if(main.players().isCustomPlayerOnline(sender.getName())) {
                player = (AdminPlayer) main.players().getCustomPlayer(sender.getName());
            } else { sender.sendMessage(Utils.Chat.INVALID_TARGET.txt); return false; }
            // 1
            if(player.hasPermission(Utils.Perm._giveCommand.txt))
            {
                // 1
                if(args.length < 1
                   || (args.length >= 4 && !args[0].toUpperCase().contains(Material.ENCHANTED_BOOK.name()))
                   || (args.length == 3 && main.getServer().getPlayer(args[2]) == null)
                   || (Material.getMaterial(args[0].toUpperCase())) == null) { return cancel(player); }
                else
                {
                    // 1
                    if(args.length == 1 && player.isItemMats(args[0], String.valueOf(amount)) != null)
                    {
                        Map<Material, Integer> itemData = player.isItemMats(args[0], String.valueOf(amount));
                        material = (itemData.keySet().toArray(new Material[0]))[0];
                        amount = (itemData.values().toArray(new Integer[0]))[0];
                        return give(player, player, material, amount);
                    }
                    // 2
                    if(args.length == 2 ||
                       (args.length == 3 && !args[0].toUpperCase().contains(Material.ENCHANTED_BOOK.name())))
                    {
                        if(player.isItemMats(args[0], args[1]) != null) {
                            Map<Material, Integer> itemData = player.isItemMats(args[0], args[1]);
                            material = (itemData.keySet().toArray(new Material[0]))[0];
                            amount = (itemData.values().toArray(new Integer[0]))[0];
                            if(args.length == 2) { return give(player, player, material, amount); }
                            else
                            {
                                Player p = main.getServer().getPlayer(args[2]);
                                Player pGave;
                                if(p != null) { pGave = p; }
                                else { return cancel(player); }
                                return give(player, pGave, material, amount);
                            }
                        }
                        else { return cancel(player); }
                    }
                    // 3
                    if(args.length >= 4 && args[0].toUpperCase().contains(Material.ENCHANTED_BOOK.name()))
                    {
                        if(args.length >= 5) { cancel(player); }
                        else { player.message("Enchanted Books Coming !"); return false; }
                    }else { return cancel(player); }
                }
            }
        }
        return false;
    }
    
    private boolean cancel(AdminPlayer p)  { p.message(Utils.Chat._invalidGive.txt); return false; }
    private boolean consoleCancel() { main.getServer().getLogger().info(Utils.Chat._invalidGive.txt); return false; }
    private boolean give(Player from, Player to, Material mat, int amt)
    {
        if(to == null) { return false; }
        to.getInventory().addItem(new ItemStack(mat, amt));
        to.sendMessage("You Received " +
                      amt + " " + "[" + formatUpperToFirstCapital(true, mat.name(), "_") + "]");
        if(from != to)
        {
            if(from == null) { main.getServer().getLogger().info("Server Sent " + to.getName() + " " +
                                                                 amt + " " + "[" + formatUpperToFirstCapital(true, mat.name(),"_") + "]"); }
            else { from.sendMessage("You Sent " + to.getName() + " " +
                                amt + " " + "[" + formatUpperToFirstCapital(true, mat.name(), "_") + "]"); }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> tabComplete = new ArrayList<>();//TAB COMPLETE LIST

        if (!(sender instanceof Player)) {
            return null;
        }
        Player p = (Player) sender;
        if (p.hasPermission(Utils.Perm._giveCommand.txt)) {
            // args[0]
            if (args.length == 1) {
                List<String> items = new ArrayList<>();
                for (Material material : Material.values()) {
                    items.add(material.name());
                }
                StringUtil.copyPartialMatches(args[0], items, tabComplete);
                Collections.sort(tabComplete);
            }
            // args[1]
            if (args.length == 2) {
                List<String> amount = Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                                                    "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
                                                    "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58",
                                                    "59", "60", "61", "62", "63", "64");
                StringUtil.copyPartialMatches(args[1], amount, tabComplete);
                Collections.sort(tabComplete);
            }
            // args[2]
            if (args.length == 3) {
                List<String> players = new ArrayList<>();
                for (Player player : main.getServer().getOnlinePlayers()) {
                    players.add(player.getName());
                }
                StringUtil.copyPartialMatches(args[2], players, tabComplete);
                Collections.sort(tabComplete);
            }
            // args[3]
            if (args.length == 4 && args[0].contains(Material.ENCHANTED_BOOK.name())) {
                List<String> items = Arrays.asList("Un-Breaking", "Fortune");
                StringUtil.copyPartialMatches(args[3], items, tabComplete);
                Collections.sort(tabComplete);
            }
            return tabComplete;
        }
        return null;
    }
}
