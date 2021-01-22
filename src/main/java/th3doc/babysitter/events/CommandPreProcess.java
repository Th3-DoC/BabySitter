package th3doc.babysitter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.debug.Debug;

public class CommandPreProcess implements Listener {
    
    //CONSTRUCTOR
    final private Main main;
    final private Debug debug;
    private BasicPlayer player;
    
    public CommandPreProcess(Main main) { this.main = main;this.debug = main.debug(); }
    
    @EventHandler
    public void commandConsolePreProcess(ServerCommandEvent e) {
        /*DEBUG*/if(debug.events()) { debug.message("server-command-pre-process event called" + e.getCommand()); }
        String[] args = e.getCommand().split(" ");
        String cmd = args[0].toLowerCase();
        if(matches("reload", cmd)) { e.setCancelled(reloadControl()); }
        if(matches("restart", cmd)) { e.setCancelled(reloadControl()); }
        if(matches("op", cmd)) { e.setCancelled(opControl()); }
    }
    
    @EventHandler
    public void commandPreProcess(PlayerCommandPreprocessEvent e) {
        /*DEBUG*/if(debug.events()) { debug.message("player-command-pre-process event called"); }
        Player p = e.getPlayer();
        if(main.players().isCustomPlayerOnline(p.getName())) {
            /*DEBUG*/if(debug.events()) { debug.message("command-pre-process custom player online"); }
            player = main.players().getCustomPlayer(p.getUniqueId());
        }
        final String[] args = e.getMessage().split(" ");
        final String cmd = args[0].toLowerCase();
        if(matches("/teleport", cmd) || matches("/tp", cmd)) { e.setCancelled(teleportUserOnly(args)); }
        if(matches("/gamemode", cmd)) { e.setCancelled(disableCreative(args)); }
        if(matches("/giveplayer", cmd) || matches("/gp", cmd)) { e.setCancelled(giveCancel(args)); }
        if(matches("/reload", cmd)) { e.setCancelled(reloadControl()); }
        if(matches("/restart", cmd)) { e.setCancelled(reloadControl()); }
    }
    
    /**
     Check Command Matches What We Are Looking For!
     
     @param cmd
     @param arg
     @return
     */
    private boolean matches(String cmd, String arg) { return cmd.toLowerCase().equals(arg); }
    
    /**
     Block Admin TPing Players All Over Yonder With Default Config Values.
     
     @param args
     @return
     */
    private boolean teleportUserOnly(String[] args) {
        /*DEBUG*/if(debug.events()) { debug.message("teleport preprocess started"); }
        BasicPlayer p = main.players().getCustomPlayer(args[0]);
        if(p != null && !player.hasPermission(Utils.Perm._tpBypass.txt)) {
            /*DEBUG*/
            if(debug.events()) { debug.message("not null and no perm"); }
            if(player != p && args.length > 1) { player.message(Utils.Chat._tpAdminOnly.txt); return true; }
        }
        /*DEBUG*/if(debug.events()) { debug.message("teleport preprocess valid or has perm"); }
        return false;
    }
    
    /**
     Disable Creative Access Based On Default Config Values.
     
     @param args
     @return
     */
    private boolean disableCreative(String[] args) {
        /*DEBUG*/if(debug.events()) { debug.message("disable-creative process started"); }
        if(!main.utils().getConfig().canAdminUseCreative() &&
           !player.hasPermission(Utils.Perm._creativeBypass.txt)) {
            /*DEBUG*/
            if(debug.events()) { debug.message(" trying to disable creative"); }
            if(args[1].toLowerCase().equals("creative")) {
                player.message(Utils.Chat._creativeDisabled.txt); return true;
            }
        }
        /*DEBUG*/if(debug.events()) { debug.message("disable-creative false"); }
        return false;
    }
    
    /**
     Cancel the Modified Give Command Based On Default Config Values.
     
     @param args
     @return
     */
    private boolean giveCancel(String[] args) {
        /*DEBUG*/if(debug.events()) { debug.message("give-cancel process started"); }
        if(!player.hasPermission(Utils.Perm._giveBypass.txt)) {
            // cmd[0] // mat[1] // amt[2] // player[3]
            boolean allowGive = main.utils().getConfig().canAdminGive();
            boolean adminGive = main.utils().getConfig().canAdminGiveToAdmin();
            /*DEBUG*/if(debug.events()) { debug.message("trying to disable give"); }
            if(!allowGive) { player.message(Utils.Chat._giveDisabled.txt); return true; }
            if(!adminGive) {
                /*DEBUG*/if(debug.events()) { debug.message("admin give false, checking for admin in player argument"); }
                BasicPlayer p = main.players().getCustomPlayer(args[2]);
                if(p instanceof AdminPlayer && p != player) {
                    /*DEBUG*/if(debug.events()) { debug.message("found admin in player argument"); }
                    if(!((AdminPlayer) p).getState(AdminPlayer.State.ADMIN)) {
                        player.message(Utils.Chat._adminInSurvival.txt); return true;
                    }
                }
            }
        }
        /*DEBUG*/if(debug.events()) { debug.message("returning false"); }
        return false;
    }
    
    private boolean reloadControl() {
        /*DEBUG*/if(debug.events()) { debug.message("reload process started"); }
        main.setReloading(true);
        return false;
    }
    
    private boolean opControl() {
        /*DEBUG*/if(debug.events()) { debug.message("op process started"); }
        return true;
    }
}
