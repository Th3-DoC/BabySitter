package th3doc.babysitter.utils.commands.entity;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabExecutor;
import th3doc.babysitter.Main;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.commands.manager.CommandManager;
import th3doc.babysitter.utils.commands.rewards.subcmd.FirstJoin;
import th3doc.babysitter.utils.commands.rewards.subcmd.Gift;
import th3doc.babysitter.utils.commands.rewards.subcmd.RankReward;

import java.util.Arrays;

public class EntityCmd extends CommandManager implements CommandExecutor, TabExecutor {
    public EntityCmd(Main main) {
        super(main, Utils.Perm.ENTITY_CMD.txt,
              Arrays.asList(
                      new Gift(main), new RankReward(main), new FirstJoin()
              ));
    }
}