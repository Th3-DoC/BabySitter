package th3doc.babysitter.utils.commands.babysit.subcmd;

import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.commands.manager.SubCommand;

public class ChestEdit extends SubCommand {
    @Override
    public String getName() {
        return "Chest_Edit";
    }
    
    @Override
    public String getDescription() {
        return "Allows Permanent Addition/Removal of Items From Chests.";
    }
    
    @Override
    public String getSyntax() {
        return "/bs chest_edit";
    }
    
    @Override
    public boolean perform(BasicPlayer player, String[] args) {
        if(player instanceof AdminPlayer) {
            AdminPlayer p = (AdminPlayer) player;
            if(p.isChestEdit()) {
                p.setChestEdit(false);
                p.message(Utils.Chat.CHEST_OFF.txt);
                return true;
            }
            p.setChestEdit(true);
            p.message(Utils.Chat.CHEST_ON.txt);
            return true;
        }
        return false;
    }
}
