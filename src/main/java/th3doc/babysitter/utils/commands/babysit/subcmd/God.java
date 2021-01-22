package th3doc.babysitter.utils.commands.babysit.subcmd;

import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.commands.manager.SubCommand;

public class God extends SubCommand
{
    @Override
    public String getName() {
        return "God";
    }
    
    @Override
    public String getDescription() {
        return "Toggle God Mode";
    }
    
    @Override
    public String getSyntax() {
        return "/bs god";
    }
    
    @Override
    public boolean perform(BasicPlayer player, String[] args) {
        if (player instanceof AdminPlayer) {
            AdminPlayer p = (AdminPlayer) player;
            if(p.isInvulnerable()) {
                p.setInvulnerable(false);
                p.sendMessage(Utils.Chat._godOff.txt);
            } else {
                p.setInvulnerable(true);
                p.sendMessage(Utils.Chat._godOn.txt);
            }
        }
        return false;
    }
}
