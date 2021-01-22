package th3doc.babysitter.utils.commands.restore.subcmd;

import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.commands.manager.SubCommand;
import th3doc.babysitter.utils.config.Config;

import java.util.Collections;
import java.util.List;

public class Confirm  extends SubCommand {
    final private Main main;
    
    public Confirm(Main main) {
        this.main = main;
    }
    
    @Override
    public String getName() {
        return "Confirm";
    }
    
    @Override
    public String getDescription() {
        return "Restore OutDated Config Data";
    }
    
    @Override
    public String getSyntax() {
        return "/restore CONFIRM";
    }
    
    @Override
    public boolean perform(BasicPlayer player, String[] args) {
        if(args[0].matches("CONFIRM")) {
            player.message("is CONFIRM");
            Config config = new Config(main, BasicPlayer.Paths.PLAYER_FOLDER.name, player.getUniqueId().toString(), "invConfig.yml");
            ItemStack[] inv = ((List<String>) config.get("survivalInv.inv")).toArray(new ItemStack[0]);
            ItemStack[] eChest = ((List<String>) config.get("survivalInv.eChest")).toArray(new ItemStack[0]);
            
            if(inv.length > 0) {
                player.message("length is good");
                player.getInventory().setContents(inv);
                player.getEnderChest().setContents(eChest);
                return true;
            }
        }
        
        return cancel(player);
    }
    
    @Override
    public List<String> tabComplete1(String[] args) {
        return Collections.singletonList("Confirm");
    }
}
