package th3doc.babysitter.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;
import th3doc.babysitter.player.PlayerConfig;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.InvType;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.PlayerType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SeeInventoryCommand implements CommandExecutor, TabCompleter {

    //CONSTRUCTOR
    private final Main main;
    public SeeInventoryCommand(Main main) { this.main = main; }
    private final List<String> inventoryTypes = Arrays.asList(InvType.Inventory.name(), InvType.EnderChest.name());
    private final List<String> playerType = Arrays.asList(PlayerType.Online.name(), PlayerType.Offline.name());

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
            String offlineUUID;
            ConfigHandler config;
            if(args[0].equals(PlayerType.Offline.name()))
            {
                offlineUUID = PlayerConfig.playerList.get(args[1]);
                config = new ConfigHandler(main, Config._playerData.txt, offlineUUID, Config._invConfig.txt);
                if(offlineUUID == null
                   || !config.getConfig().isSet(Config._survivalInv.txt)) { p.sendMessage(Chat._invalidViewerCommand.txt); return false; }
            }
            if (args.length < 3
                    || args.length > 4
                    || (main.getServer().getPlayer(args[1]) == null && args[0].equals(PlayerType.Online.name()))
                    || (args[1].contains(p.getName()) && !p.hasPermission(Perm._seeBypass.txt))
                    || !inventoryTypes.contains(args[2])
                    || !playerType.contains(args[0]))
            {
                p.sendMessage(Chat._invalidViewerCommand.txt);
                return false;
            }
            //CHECK FOURTH ARGUMENT EXISTS
            boolean edit = false;
            if (args.length == 4)
            {
                //CHECK FOURTH ARGUMENT IS VALID
                if (args[3].equals(InvType.EditMode.name())
                        &&  p.hasPermission(Perm._invEdit.txt)) { edit = true; }
                else { p.sendMessage(Chat._invalidViewerCommand.txt); return false; }
            }
            main.getPlayer(p.getUniqueId()).admin().gui().openInv(args[1], args[0], args[2], edit);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> tabComplete = new ArrayList<>();//TAB COMPLETE LIST

        if (!(sender instanceof Player)) { return null; }
        Player p = (Player) sender;
        if (p.hasPermission(Perm._invSeeCommand.txt)) {
            if(args.length == 1)
            {
                StringUtil.copyPartialMatches(args[0], playerType, tabComplete);
                Collections.sort(tabComplete);
            }
            if (args.length == 2)
            {
                //offline/online player lists
                List<String> players = new ArrayList<>();
                for (Player player : main.getServer().getOnlinePlayers())
                {
                    players.add(player.getName());
                }
                if(args[0].equals(PlayerType.Offline.name()))
                {
                    players.clear();
                    players.addAll(PlayerConfig.playerList.keySet());
                    for(Player player : main.getServer().getOnlinePlayers())
                    {
                        players.remove(player.getName());
                    }
                }
                StringUtil.copyPartialMatches(args[1], players, tabComplete);
                Collections.sort(tabComplete);
            }
            if (args.length == 3)
            {
                StringUtil.copyPartialMatches(args[2], inventoryTypes, tabComplete);
                Collections.sort(tabComplete);
            }
            if (args.length == 4 && p.hasPermission(Perm._invEdit.txt))
            {
                List<String> edit = new ArrayList<>();
                edit.add(0, InvType.EditMode.name());
                StringUtil.copyPartialMatches(args[3], edit, tabComplete);
                Collections.sort(tabComplete);
            }
            return tabComplete;
        }
        return Collections.emptyList();
    }
}
