package th3doc.babysitter.utils.commands.babysit.subcmd;

import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.commands.manager.SubCommand;

import java.util.List;

public class TpInSpec extends SubCommand {
    final private Main main;
    
    public TpInSpec(Main main) { this.main = main; }
    @Override
    public String getName() {
        return "Spectate";
    }
    
    @Override
    public String getDescription() {
        return "Spectate A Chosen Player";
    }
    
    @Override
    public String getSyntax() {
        return "/bs spectate <player>";
    }
    
    @Override
    public boolean perform(BasicPlayer player, String[] args) {
        //if !adminState run bsMode then forceSpec
        if(player instanceof AdminPlayer) {
            AdminPlayer p = (AdminPlayer) player;
            if(args.length == 1) {
                p.removeSpecPassenger();
            } else if( args.length == 2) {
                if(!p.getState(AdminPlayer.State.ADMIN)) {
                    p.bsMode();
                }
                p.spectatePlayer(args);
            } else {
                return cancel(player);
            }
        } else { return false; }
        return true;
    }
    
    @Override
    public List<String> tabComplete1(String[] args) {
        return main.players().getCustomOnlinePlayers();
    }
}
