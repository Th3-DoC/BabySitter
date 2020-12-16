package th3doc.babysitter.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.Perm;

public class GodCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //CHECK INSTANCE OF PLAYER
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().info(Chat._noConsole.txt);
            return false;
        }
        Player p = (Player) sender;

        if (p.hasPermission(Perm._godCommand.txt))
        {
            if (p.isInvulnerable())
            {
                p.setInvulnerable(false);
                p.sendMessage(Chat._godOff.txt);
            }
            else
            {
                p.setInvulnerable(true);
                p.sendMessage(Chat._godOn.txt);
            }
        }
        return false;
    }
}
