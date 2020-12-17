package th3doc.babysitter.events;

import net.minecraft.server.v1_16_R3.TileEntityChestTrapped;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.States;

public class InventoryOpen implements Listener {
    
    //CONSTRUCTOR
    private final Main main;
    public InventoryOpen(Main main) { this.main = main; }
    
    @EventHandler
    public void inventoryOpenEvent(InventoryOpenEvent e) {
        Player p = (Player) e.getPlayer();
        if(main.getPlayer(p.getUniqueId()).isAdmin() &&
           main.getPlayer(p.getUniqueId()).admin().getConfig().getState(States.ADMIN) &&
           !p.hasPermission(Perm._invBypass.txt) &&
           !main.getPlayer(p.getUniqueId()).admin().gui().isEditingInv())
        {
            InventoryHolder ih = e.getInventory().getHolder();
            if(ih instanceof DoubleChest ||
               ih instanceof Chest ||
               ih instanceof ShulkerBox ||
               ih instanceof Barrel ||
               ih instanceof TileEntityChestTrapped ||
               ih instanceof BrewingStand ||
               ih instanceof Dispenser ||
               ih instanceof Dropper ||
               ih instanceof Furnace ||
               ih instanceof Hopper ||
               ih instanceof Minecart ||
               ih instanceof Donkey ||
               ih instanceof Mule ||
               ih instanceof Llama)
            {
                for(final ItemStack item : e.getInventory().getStorageContents().clone())
                {
                    if(item != null)
                    {
                        main.getPlayer(p.getUniqueId()).inventory().saveInventoryToCheck(new ItemStack(item));
                    } else { main.getPlayer(p.getUniqueId()).inventory().saveInventoryToCheck(null); }
                }
            }
        }
    }
}
