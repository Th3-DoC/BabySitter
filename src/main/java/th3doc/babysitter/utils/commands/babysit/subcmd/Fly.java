package th3doc.babysitter.utils.commands.babysit.subcmd;

import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.commands.manager.SubCommand;

public class Fly extends SubCommand
{
    @Override
    public String getName() {
        return "Fly";
    }
    
    @Override
    public String getDescription() {
        return "Toggle The Ability To Fly";
    }
    
    @Override
    public String getSyntax() {
        return "/bs fly";
    }
    
    @Override
    public boolean perform(BasicPlayer player, String[] args) {
        if(player instanceof AdminPlayer) {
            AdminPlayer p = (AdminPlayer) player;
            if(!p.getAllowFlight()) {
                p.setAllowFlight(true);
                p.setFlying(true);
                p.setState(true, AdminPlayer.State.FLY);
                p.sendMessage(Utils.Chat._flyOn.txt);
            } else {
                p.setFlying(false);
                p.setAllowFlight(false);
                p.setState(false, AdminPlayer.State.FLY);
                p.sendMessage(Utils.Chat._flyOff.txt);
            }
            
        }
        return false;
    }
}
