package th3doc.babysitter.utils.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.entities.player.BasicPlayer;

public abstract class Menu implements InventoryHolder
{
    
    private Inventory inventory;
    
    private MenuUtil util;
    
    public Menu(MenuUtil util) { this.util = util; }
    
    public abstract String getMenuName();
    
    public abstract int getSlots();
    
    public abstract void menuClickHandler(InventoryClickEvent e);
    
    public abstract void menuOpenHandler(InventoryOpenEvent e);
    
    public abstract void menuCloseHandler(InventoryCloseEvent e);
    
    public abstract void setMenuItems();
    
    public void open()
    {
        BasicPlayer player = this.util.getPlayer();
        player.setMenuOpen(true);
        this.inventory = player.createInv(this, getSlots(), getMenuName());
        this.setMenuItems();
        player.openInventory(this.inventory);
    }
    
    public MenuUtil getUtil() { return this.util; }
    
    @Override
    public Inventory getInventory()
    {
        return this.inventory;
    }
    
    public void setInventory(int index, ItemStack itemStack)
    {
        this.inventory.setItem(index, itemStack);
    }
    
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    
    public void setInventory(ItemStack[] items) {
        this.inventory.setContents(items);
    }
}
