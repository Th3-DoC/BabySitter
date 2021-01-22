package th3doc.babysitter.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {
    @EventHandler
    public void deathEvent(PlayerDeathEvent e) {
        //TODO if at spawn grid activate teleport watch, give items 18-27 after teleport watch
        //TODO set canPickUpItems(false) for all players within 4 chunk grid around a dead players items, unless trusted?
        //TODO add extra despawn time to the items set in config data
    }
}
