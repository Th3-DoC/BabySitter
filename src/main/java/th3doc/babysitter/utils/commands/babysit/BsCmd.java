package th3doc.babysitter.utils.commands.babysit;

import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.commands.babysit.subcmd.*;
import th3doc.babysitter.utils.commands.manager.CommandManager;

import java.util.Arrays;

public class BsCmd extends CommandManager {
    public BsCmd(Main main) {
        super(main, Utils.Perm._babysitCommand.txt,
              Arrays.asList(
                      new TpInSpec(main), new Back(), new ChestEdit(), new God(), new Fly(),
                      new Vanish()
              ));
    }
    
    @Override
    public boolean noSubCommandUse(BasicPlayer player) {
        if(player instanceof AdminPlayer) {
            ((AdminPlayer) player).bsMode();
            return true;
        }
        return false;
    }
}
