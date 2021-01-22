package th3doc.babysitter.events;

import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import th3doc.babysitter.Main;
import th3doc.babysitter.utils.debug.Debug;

public class PlayerInteractEntity implements Listener {
    final private Main main;
    final private Debug debug;
    
    public PlayerInteractEntity(Main main) { this.main = main; this.debug = main.debug(); }
    
    
    @EventHandler
    public void playerInteractEvent(PlayerInteractAtEntityEvent e) {
        //DEBUG
        if(debug.events()) { debug.message("PlayerInteractEvent Called"); }
        if(e.getRightClicked() instanceof Zombie ||
           e.getRightClicked() instanceof Skeleton) {
            //DEBUG
            if(debug.events()) { debug.message("event called on trade-able entity"); }
            if((main.entities().getTrades(e.getRightClicked().getUniqueId())) != null) {
                //DEBUG
                if(debug.events()) { debug.message("trades available"); }
                main.entities().openTrade(e.getPlayer(),
                                          e.getRightClicked().getUniqueId(),
                                          e.getRightClicked().getName(),
                                          main.entities().getTrades(e.getRightClicked().getUniqueId()));
            }
        }
    }
}
