package th3doc.babysitter.events;

import net.minecraft.server.v1_16_R3.TileEntityChestTrapped;
import org.bukkit.block.*;
import org.bukkit.entity.*;
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
        //VARIABLES
        Player p = (Player) e.getPlayer();
        boolean isAdmin = main.getPlayer(p.getUniqueId()).isAdmin();
        boolean adminState = main.getPlayer(p.getUniqueId()).admin().getConfig().getState(States.ADMIN);
        
        
        //RESET INVENTORY IF IN ADMIN MODE
        if(isAdmin && adminState &&
           main.getPlayer(p.getUniqueId()).inventory().isCheckingInv())
        {
            InventoryHolder ih = e.getInventory().getHolder();
            if(ih instanceof Chest ||
               ih instanceof DoubleChest ||
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
        if(isAdmin && adminState &&
           main.getPlayer(p.getUniqueId()).admin().gui().isEditingInv())
        {
            p.sendMessage("inventory close was editing, saving now");
            for(int i=0;i<=40;i++)
            {
                if(i < 36) { main.getPlayer(p.getUniqueId()).admin().gui().saveInvToEdit(e.getInventory().getStorageContents()[i+18]); }
                else if(i == 36) { main.getPlayer(p.getUniqueId()).admin().gui().saveInvToEdit(e.getInventory().getStorageContents()[17]); }
                else if(i == 37) { main.getPlayer(p.getUniqueId()).admin().gui().saveInvToEdit(e.getInventory().getStorageContents()[8]); }
                else if(i == 38) { main.getPlayer(p.getUniqueId()).admin().gui().saveInvToEdit(e.getInventory().getStorageContents()[9]); }
                else if(i == 39) { main.getPlayer(p.getUniqueId()).admin().gui().saveInvToEdit(e.getInventory().getStorageContents()[0]); }
                else { main.getPlayer(p.getUniqueId()).admin().gui().saveInvToEdit(e.getInventory().getStorageContents()[4]); }
            }
            main.getPlayer(p.getUniqueId()).admin().gui().saveInvEdit(e.getView().getTitle());
        }
        if(isAdmin && adminState &&
           main.getPlayer(p.getUniqueId()).admin().gui().isGuiOpen())
        {
            main.getPlayer(p.getUniqueId()).admin().gui().closeGui();
        }
    }
}
