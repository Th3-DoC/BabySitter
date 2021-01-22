package th3doc.babysitter.utils.commands.see.subcmd;

import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.commands.manager.SubCommand;
import th3doc.babysitter.utils.menu.Menu;
import th3doc.babysitter.utils.menu.menus.SeeEchestMenu;
import th3doc.babysitter.utils.menu.menus.SeeInvMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SeeInv extends SubCommand {//TODO load admin survivalInv if admin is in admin state
    private enum Args { ONLINE, OFFLINE }
    
    private enum ThisChat
    {
        GUI_OPEN("GUI Open, Items Will Save When Closed");
        
        public String txt;
        ThisChat(String txt) { this.txt = txt; }
    }
    
    final private Main main;
    private BasicPlayer player;
    
    public SeeInv(Main main) { this.main = main; }
    
    @Override
    public String getName() {
        return "Inventory";
    }
    
    @Override
    public String getDescription() {
        return "View Player Inventories";
    }
    
    @Override
    public String getSyntax() {
        return "/see inventory <online|offline> <player> <inventory type> <edit_mode>";
    }
    
    @Override
    public boolean perform(BasicPlayer player, String[] args) {
        if(args.length == 4 || (args.length == 5 && args[4].equalsIgnoreCase("edit_mode"))) {
            this.player = player;
            ItemStack[] items;
            boolean edit = false;
            if(args.length == 5 &&
               args[4].equalsIgnoreCase("edit_mode") && player.hasPermission(Utils.Perm._invEdit.txt)) { edit = true; }
            switch(Args.valueOf(args[1].toUpperCase())) {
                case ONLINE:
                    if(main.players().isCustomPlayerOnline(args[2])) {
                        if(args[3].equalsIgnoreCase(BasicPlayer.InvType.ENDER_CHEST.name()) ||
                           args[3].equalsIgnoreCase(BasicPlayer.InvType.INVENTORY.name())) {
                            switch(BasicPlayer.InvType.valueOf(args[3].toUpperCase())) {
                                case ENDER_CHEST:
                                    Menu menu = new SeeEchestMenu(player.getMenuUtil(), BasicPlayer.Type.ONLINE, args[2], edit);
                                    menu.open();
                                    return true;
                                case INVENTORY:
                                    Menu menu1 = new SeeInvMenu(player.getMenuUtil(), BasicPlayer.Type.ONLINE, args[2], edit);
                                    menu1.open();
                                    return true;
                                default:
                                    return cancel(player);
                            }
                        } else return cancel(player);
                    } else { return cancel(player); }
                case OFFLINE:
                    if(main.players().getCustomPlayerList().contains(args[2])) {
                        if(args[3].equalsIgnoreCase(BasicPlayer.InvType.ENDER_CHEST.name()) ||
                           args[3].equalsIgnoreCase(BasicPlayer.InvType.INVENTORY.name())) {
                            switch(BasicPlayer.InvType.valueOf(args[3].toUpperCase())) {
                                case ENDER_CHEST:
                                    Menu menu = new SeeEchestMenu(player.getMenuUtil(), BasicPlayer.Type.OFFLINE, args[2], edit);
                                    menu.open();
                                    return true;
                                case INVENTORY:
                                    Menu menu1 = new SeeInvMenu(player.getMenuUtil(), BasicPlayer.Type.OFFLINE, args[2], edit);
                                    menu1.open();
                                    return true;
                                default:
                                    return cancel(player);
                            }
                        } else return cancel(player);
                    } else { return cancel(player); }
                default: return cancel(player);
            }
        } else { return cancel(player); }
    }
    
    @Override
    public List<String> tabComplete1(String[] args) {
        return Arrays.asList("Offline", "Online");
    }
    
    @Override
    public List<String> tabComplete2(String[] args) {
        if (args[1].equalsIgnoreCase("offline")) {
            return main.players().getCustomOfflinePlayerList();
        }
        if(args[1].equalsIgnoreCase("online")) {
            return main.players().getCustomOnlinePlayers();
        }
        return new ArrayList<>();
    }
    
    @Override
    public List<String> tabComplete3(String[] args) {
        return Arrays.asList("Ender_Chest", "Inventory");
    }
    
    @Override
    public List<String> tabComplete4(String[] args) {
    
        if (args.length == 5) { //TODO add sender to tabComplete args
            return Collections.singletonList("Edit_Mode");
        }
        return new ArrayList<>();
    }
}
