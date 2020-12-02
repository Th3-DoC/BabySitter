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
        Player p = (Player) e.getPlayer();
        //WE PROBABLY WANT THIS TO TIE IN WITH OUR OFFLINE INVENTORY EDITOR
        //CHECK ADMIN HAS INVENTORY OPEN AND HAS NOT CHANGED
        if(main.player().inventory().isCheckingInv(p)) {
            if(e.getInventory().getContents() != main.player().inventory().invToCheck(p))
            {
                e.getInventory().setContents(main.player().inventory().invToCheck(p));
                main.player().inventory().stopCheckingInv(p);
            }
        }
        
        //SAVE INVENTORY IF EDITING
        main.player().inventory().saveInvEdit((Player) e.getPlayer(), e);
    }
}
