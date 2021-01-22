package th3doc.babysitter.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.BasicPlayer;

public class PlayerTeleport implements Listener {
    final private Main main;
    
    public PlayerTeleport(Main main) { this.main = main; }
    
    @EventHandler
    public void teleport(PlayerTeleportEvent e) {
        if(main.players().isCustomPlayerOnline(e.getPlayer().getName())) {
            BasicPlayer p = main.players().getCustomPlayer(e.getPlayer().getUniqueId());
            if(p.isTeleportWatch()) {
                // give first join items 18-27, set teleport-watch false
                ItemStack[] items = main.players().rewards().getFirstJoinItems(18, 27);
                for(ItemStack item : items) { p.getInventory().addItem(item); }
                p.setTeleportWatch(false);
            }
        }
    }
}
