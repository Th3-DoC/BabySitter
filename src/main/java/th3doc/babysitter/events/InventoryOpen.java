package th3doc.babysitter.events;

import net.minecraft.server.v1_16_R3.TileEntityChestTrapped;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
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
        if(main.getPlayer(p.getUniqueId()).isAdmin()
                && main.getPlayer(p.getUniqueId()).admin().getConfig().getState(States.ADMIN))
        {
            if(!p.hasPermission(Perm._invBypass.txt) &&
               !main.getPlayer(p.getUniqueId()).inventory().isEditingInv())
            {
                InventoryHolder ih = e.getInventory().getHolder();
                if(ih instanceof DoubleChest ||
                   ih instanceof Chest ||
                   ih instanceof ShulkerBox ||
                   ih instanceof Barrel ||
                   ih instanceof TileEntityChestTrapped)
                {
                    for(final ItemStack item : e.getInventory().getStorageContents().clone())
                    {
                        if(item != null)
                        {
                            main.getPlayer(p.getUniqueId()).inventory().saveInventoryToCheck(new ItemStack(item));
                        } else { main.getPlayer(p.getUniqueId()).inventory().saveInventoryToCheck(null); }
                    }
                    p.sendMessage("inv to check saved");
                }
            }
        }
    }
}
