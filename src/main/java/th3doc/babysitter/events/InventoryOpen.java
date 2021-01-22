package th3doc.babysitter.events;

import net.minecraft.server.v1_16_R3.TileEntityChestTrapped;
import org.bukkit.block.*;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Mule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.menu.Menu;

public class InventoryOpen implements Listener {
    private final Main main;
    
    public InventoryOpen(Main main) { this.main = main; }
    
    @EventHandler
    public void inventoryOpenEvent(InventoryOpenEvent e) {
        InventoryHolder invHolder = e.getInventory().getHolder();
        if(invHolder instanceof Menu) {
            Menu menu = (Menu) invHolder;
            menu.menuOpenHandler(e);
        }
        
        
        if(main.players().getCustomPlayer(e.getPlayer().getUniqueId()) instanceof AdminPlayer) {
            AdminPlayer player = (AdminPlayer) main.players().getCustomPlayer(e.getPlayer().getUniqueId());
            if(player.getState(AdminPlayer.State.ADMIN) && !player.hasPermission(Utils.Perm._invBypass.txt)) { // !main.getPlayer(p.getUniqueId()).admin().gui().isEditingInv()
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
                   ih instanceof Llama) {
                    for(final ItemStack item : e.getInventory().getStorageContents().clone()) {
                        if(item != null) {
                            player.saveInventoryToCheck(new ItemStack(item));
                        } else { player.saveInventoryToCheck(null); }
                    }
                }
            }
        }
    }
}
