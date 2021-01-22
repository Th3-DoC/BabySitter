package th3doc.babysitter.utils.commands.entity;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.commands.manager.CommandManager;
import th3doc.babysitter.utils.commands.rewards.subcmd.Gift;
import th3doc.babysitter.utils.commands.rewards.subcmd.RankReward;
import th3doc.babysitter.utils.debug.Debug;

import java.util.Arrays;
import java.util.List;

public class Spawn extends CommandManager implements CommandExecutor
{
    
    final private Main main;
    final private Debug debug;
    private AdminPlayer player;
    public Spawn(Main main)
    {
        super(main, Utils.Perm.ENTITY_CMD.txt,
              Arrays.asList(
                      new Gift(main), new RankReward(main)));
        this.main = main;
        this.debug = main.debug();
    }
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        return super.onCommand(sender, command, label, args);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        return null;
    }
}
