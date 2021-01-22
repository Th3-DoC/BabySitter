package th3doc.babysitter.utils.commands.babysit.subcmd;

import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.commands.manager.SubCommand;

public class Back extends SubCommand {
    
    @Override
    public String getName() {
        return "Back";
    }
    
    @Override
    public String getDescription() {
        return "Sends You To Where Babysitter Was Activated";
    }
    
    @Override
    public String getSyntax() {
        return "/bs back";
    }
    
    @Override
    public boolean perform(BasicPlayer player, String[] args) {
        if(player instanceof AdminPlayer) {
            AdminPlayer p = (AdminPlayer) player;
            if(p.getSurvivalLastKnown() != null) {
                p.teleport(p.getSurvivalLastKnown());
                return true;
            }
            return false;
        }
        return false;
    }
}
