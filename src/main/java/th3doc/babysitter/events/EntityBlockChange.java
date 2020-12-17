package th3doc.babysitter.events;

import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import th3doc.babysitter.Main;

public class EntityBlockChange implements Listener {
    
    //VARIABLES
    final private Main main;
    
    
    //CONSTRUCTOR
    public EntityBlockChange(Main main) { this.main = main; }
    
    
    @EventHandler
    public void entityBlockChangeEvent(EntityChangeBlockEvent e)
    {
        //ENDERMAN GRIEFING
        if(e.getEntity() instanceof Enderman &&
           !main.defaultConfig().isEndermanGriefingAllowed())
        {
            e.setCancelled(true);
        }
    }
}
