package th3doc.babysitter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.menu.Menu;

public class InventoryClick implements Listener
{
    
    //VARIABLES
    final private Main main;
    private BasicPlayer player;
    
    //CONSTRUCTOR
    public InventoryClick(Main main) { this.main = main; }
    
    @EventHandler
    public void invClickEvent(InventoryClickEvent e)
    {
        if(e.getWhoClicked() instanceof Player && main.players().isCustomPlayerOnline(e.getWhoClicked().getName())) {
            player = main.players().getCustomPlayer(e.getWhoClicked().getUniqueId());
            if(player.isMenuOpen() && e.getClickedInventory() != null) {
                InventoryHolder invHolder = e.getClickedInventory().getHolder();
                if(invHolder instanceof Menu) {
                    if(e.getCurrentItem() == null) { return; }
            
                    Menu menu = (Menu) invHolder;
                    menu.menuClickHandler(e);
                }
            }
        }
    }
}
