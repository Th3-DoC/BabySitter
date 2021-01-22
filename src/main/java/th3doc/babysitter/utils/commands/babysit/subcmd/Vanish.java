package th3doc.babysitter.utils.commands.babysit.subcmd;

import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.commands.manager.SubCommand;

public class Vanish extends SubCommand
{
    @Override
    public String getName() {
        return "Vanish";
    }
    
    @Override
    public String getDescription() {
        return "Toggle Invisibility From Players And The List";
    }
    
    @Override
    public String getSyntax() {
        return "/bs vanish";
    }
    
    @Override
    public boolean perform(BasicPlayer player, String[] args) {
        if (player instanceof AdminPlayer) {
            AdminPlayer p = (AdminPlayer) player;
            p.toggleVanish();
            return true;
        }
        return false;
    }
}
