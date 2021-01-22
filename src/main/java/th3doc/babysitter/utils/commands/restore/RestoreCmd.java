package th3doc.babysitter.utils.commands.restore;

import th3doc.babysitter.Main;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.commands.manager.CommandManager;
import th3doc.babysitter.utils.commands.restore.subcmd.Confirm;

import java.util.Collections;

public class RestoreCmd extends CommandManager {
    public RestoreCmd(Main main) {
        super(main, Utils.Perm.RESTORE_CMD.txt,
              Collections.singletonList(
                      new Confirm(main)));
    }
}
