package th3doc.babysitter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.States;

public class BlockPlace implements Listener {
    
    //CONSTRUCTOR
    private final Main main;
    public BlockPlace(Main main) { this.main = main; }
    
    @EventHandler
    public void blockPlace(BlockPlaceEvent e)
    {
        Player p = e.getPlayer();
        if(((main.getPlayer(p.getUniqueId()).isAdmin()
                && main.getPlayer(p.getUniqueId()).admin().getConfig().getState(States.ADMIN))
            && main.defaultConfig().isSafeBlockEnabled()))
        {
            if(!main.defaultConfig().getSafeBlocks()
                    .contains(e.getBlock().getType().name())
                && !p.hasPermission(Perm._blockPlaceBypass.txt))
            {
                p.sendMessage(Chat._cancelBlockPlace.txt);
                e.setCancelled(true);
            }
        }
    }
}
