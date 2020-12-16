package th3doc.babysitter.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.Perm;

public class BackCommand implements CommandExecutor {

    //CONSTRUCTOR
    private final Main main;
    public BackCommand(Main main) { this.main = main; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        //INSTANCE OF PLAYER
        if (!(sender instanceof Player))
        {
            Bukkit.getLogger().info(Chat._noConsole.txt);
            return false;
        }
        
        Player p = (Player) sender;
        if (p.hasPermission(Perm._tpBypass.txt)
                && main.getPlayer(p.getUniqueId()).location().getSurvivalLastKnown() != null)
        {
            p.teleport(main.getPlayer(p.getUniqueId()).location().getSurvivalLastKnown());
        }
        return false;
    }
}
