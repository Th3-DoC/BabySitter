package th3doc.babysitter.utils.commands.see;

import th3doc.babysitter.Main;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.commands.manager.CommandManager;
import th3doc.babysitter.utils.commands.see.subcmd.SeeInv;

import java.util.Collections;

public class SeeCmd extends CommandManager {
    public SeeCmd(Main main) {
        super(main, Utils.Perm._invSeeCommand.txt,
              Collections.singletonList(new SeeInv(main)));
    }
}
