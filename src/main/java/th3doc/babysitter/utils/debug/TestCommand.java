package th3doc.babysitter.utils.debug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.npc.Entities;
import th3doc.babysitter.entities.npc.EntityUtils;
import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.utils.config.Config;

public class TestCommand implements CommandExecutor, EntityUtils
{
    final private Main main;
    
    public TestCommand(Main main) { this.main = main; }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender instanceof AdminPlayer)
        {
            AdminPlayer player = (AdminPlayer) sender;
    
    
            Config config = new Config(main,
                                       Entities.Paths.DATA_FOLDER.txt,
                                       "",
                                       "test.yml");
    
            config.set("players.survivalWorld.basic.inv",
                                   serializeItemArray(player.getPlayer().getInventory().getContents()));
            config.save();
    
    
            if(config.isSet("players.survivalWorld.basic.inv"))
            {
            
            }
            else
            {
                player.message("error in test 2");
            }
        }
        return false;
    }
}
