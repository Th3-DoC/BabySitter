package th3doc.babysitter.utils.menu.menus;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.utils.menu.Menu;
import th3doc.babysitter.utils.menu.MenuUtil;

public class GiftMenu  extends Menu {
    public GiftMenu(MenuUtil util, String inventoryOwner, ItemStack[] items) {
        super(util);
        getUtil().setInventoryOwner(inventoryOwner);
        getUtil().setOriginalInv(items);
    }
    
    @Override
    public String getMenuName() {
        return "Gift Section : " + getUtil().getInventoryOwner();
    }
    
    @Override
    public int getSlots() {
        return 27;
    }
    
    @Override
    public void menuClickHandler(InventoryClickEvent e) {
    
    }
    
    @Override
    public void menuOpenHandler(InventoryOpenEvent e) {
    
    }
    
    @Override
    public void menuCloseHandler(InventoryCloseEvent e) {
        getUtil().getMain().players().rewards()
                .addGiftSection(getUtil().getInventoryOwner(), e.getInventory().getContents());
    }
    
    @Override
    public void setMenuItems() {
        setInventory(getUtil().getOriginalInv());
    }
}
