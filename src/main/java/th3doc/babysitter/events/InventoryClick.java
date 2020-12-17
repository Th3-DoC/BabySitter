package th3doc.babysitter.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.data.States;

public class InventoryClick implements Listener {
    
    //VARIABLES
    final private Main main;
    
    
    //CONSTRUCTOR
    public InventoryClick(Main main) { this.main = main; }
    
    @EventHandler
    public void invClickEvent(InventoryClickEvent e)
    {
        final int[] lockedObj = new int[]{1,2,3,5,6,7,10,11,12,13,14,15,16};
        Player p = (Player) e.getWhoClicked();
        if(main.getPlayer(p.getUniqueId()).isAdmin() &&
           main.getPlayer(p.getUniqueId()).admin().getConfig().getState(States.ADMIN) &&
           main.getPlayer(p.getUniqueId()).admin().gui().isGuiOpen() &&
           (e.getCurrentItem() != null && e.getCurrentItem().equals(new ItemStack(Material.GREEN_STAINED_GLASS_PANE))))
        {
            for(int i : lockedObj)
            {
                if(i == e.getSlot()) { e.setCancelled(true); }
            }
        }
    }
}
