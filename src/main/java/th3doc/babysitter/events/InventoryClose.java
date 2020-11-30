package th3doc.babysitter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import th3doc.babysitter.Main;

public class InventoryClose implements Listener {

    //CONSTRUCTOR
    private Main main;
    public InventoryClose(Main main) { this.main = main; }

    @EventHandler
    public void inventoryCloseEvent(InventoryCloseEvent e)
    {
        //WE PROBABLY WANT THIS TO TIE IN WITH OUR OFFLINE INVENTORY EDITOR
        //CHECK ADMIN HAS INVENTORY OPEN AND HAS NOT CHANGED
        if(main.player().inventory().isCheckingInv((Player) e.getPlayer())) {
            if(e.getInventory().getContents() != main.player().inventory().invToCheck((Player) e.getPlayer()))
            {
                e.getInventory().setContents(main.player().inventory().invToCheck((Player) e.getPlayer()));
            }
        }
        
        //save player inv to config if its there inventory or echest closing
        
        //SAVE INVENTORY IF EDITING
        main.player().inventory().saveInvEdit((Player) e.getPlayer(), e);
    }
}
