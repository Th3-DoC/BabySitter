package th3doc.babysitter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.States;

public class InventoryOpen implements Listener {
    
    //CONSTRUCTOR
    private Main main;
    public InventoryOpen(Main main) { this.main = main; }
    
    @EventHandler
    public void inventoryOpenEvent(InventoryOpenEvent e) {
        Player p = (Player) e.getPlayer();
        if(main.player().isAdmin(p .getName())
                && main.player().admin().getState(p.getName(), States.Admin))
        {
            if(!p.hasPermission(Perm._invBypass.txt))
            {
                if(!e.getInventory().getType().equals(InventoryType.CRAFTING)
                        && !e.getInventory().getType().equals(InventoryType.ENDER_CHEST)
                        && !e.getInventory().getType().equals(InventoryType.ANVIL)
                        && !e.getInventory().getType().equals(InventoryType.ENCHANTING)
                        && !e.getInventory().getType().equals(InventoryType.MERCHANT)
                        && !e.getInventory().getType().equals(InventoryType.SMITHING)
                        && !e.getInventory().getType().equals(InventoryType.STONECUTTER)
                        && !e.getInventory().getType().equals(InventoryType.WORKBENCH))
                {
                    //SAVE INVENTORY TO CHECK
                    main.player().inventory().saveInventory(p, e.getInventory().getContents());
                }
            }
        }
    }
}
