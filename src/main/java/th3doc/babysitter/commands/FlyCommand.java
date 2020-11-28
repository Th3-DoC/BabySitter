package th3doc.babysitter.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.State;

import java.util.Arrays;
import java.util.List;

public class FlyCommand implements CommandExecutor {

    //CONSTRUCTOR
    private Main main;
    public FlyCommand(Main main) { this.main = main; }
    private List<String> inventoryTypes = Arrays.asList("inv", "echest");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //CHECK INSTANCE OF PLAYER
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().info(Chat._noConsole.txt);
            return false;
        }
        Player p = (Player) sender;

        if (p.hasPermission(Perm._flyCommand.txt))
        {
            if (!p.getAllowFlight())
            {
                p.setAllowFlight(true);
                p.setFlying(true);
                main.player().admin().setState(p, true, State.Fly);
                p.sendMessage(Chat._flyOn.txt);
                return false;
            }
            p.setFlying(false);
            p.setAllowFlight(false);
            main.player().admin().setState(p, false, State.Fly);
            p.sendMessage(Chat._flyOff.txt);
        }
        return false;
    }
}
