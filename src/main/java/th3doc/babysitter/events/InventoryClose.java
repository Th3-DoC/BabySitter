package th3doc.babysitter.events;

import net.minecraft.server.v1_16_R3.BlockChestTrapped;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.data.States;

public class InventoryClose implements Listener {

    //CONSTRUCTOR
    private final Main main;
    public InventoryClose(Main main) { this.main = main; }

    @EventHandler
    public void inventoryCloseEvent(InventoryCloseEvent e)
    {
        Player p = (Player) e.getPlayer();
        //WE PROBABLY WANT THIS TO TIE IN WITH OUR OFFLINE INVENTORY EDITOR
        //CHECK ADMIN HAS INVENTORY OPEN AND HAS NOT CHANGED
        if(main.getPlayer(p.getUniqueId()).isAdmin() &&
           main.getPlayer(p.getUniqueId()).admin().getConfig().getState(States.ADMIN))
        {
            if(main.getPlayer(p.getUniqueId()).inventory().isCheckingInv())
            {
                InventoryHolder ih = e.getInventory().getHolder();
                if(ih instanceof Chest ||
                   ih instanceof DoubleChest ||
                   ih instanceof ShulkerBox ||
                   ih instanceof Barrel ||
                   ih instanceof BlockChestTrapped)
                {
                    final ItemStack[] compare = main.getPlayer(p.getUniqueId()).inventory().invToCheck();
                    final ItemStack[] inv = e.getInventory().getHolder().getInventory().getStorageContents();
                    int i = 0;
                    for(ItemStack item : inv)
                    {
                        if(item == null && compare[i] == null)
                        {
                            i++;
                            continue;
                        }
                        else if((item == null && compare[i] != null) ||
                                (item != null && compare[i] == null) ||
                                !item.equals(inv[i]))
                        {
                            for(ItemStack removeItem : e.getInventory().getHolder().getInventory().getStorageContents())
                            {
                                e.getInventory().getHolder().getInventory().remove(removeItem);
                            }
                            e.getInventory().getHolder().getInventory().setContents(compare);
                            break;
                        }
                        i++;
                    }
                }
                main.getPlayer(p.getUniqueId()).inventory().stopCheckingInv();
            }
    
            //SAVE INVENTORY IF EDITING
            if(main.getPlayer(p.getUniqueId()).inventory().isEditingInv())
            {
                p.sendMessage("inventory close was editing, saving now");
                main.getPlayer(p.getUniqueId()).inventory().saveInvEdit(e);
            }
        }
    }
}
