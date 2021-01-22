package th3doc.babysitter.utils.menu.menus;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.utils.menu.Menu;
import th3doc.babysitter.utils.menu.MenuUtil;

public class FirstJoinMenu extends Menu {
    public FirstJoinMenu(MenuUtil util) {
        super(util);
    }
    
    @Override
    public String getMenuName() {
        return "First Join Item's";
    }
    
    @Override
    public int getSlots() {
        return 54;
    }
    
    @Override
    public void menuClickHandler(InventoryClickEvent e) {
        final int[] lockedObj = new int[]{9,10,11,12,13,14,15,16,17,27,28,29,30,31,32,33,34,35};
        if((e.getCurrentItem() != null && e.getCurrentItem().equals(new ItemStack(Material.GREEN_STAINED_GLASS_PANE))))
        {
            for(int i : lockedObj)
            {
                if(i == e.getSlot()) { e.setCancelled(true); }
            }
        }
    }
    
    @Override
    public void menuOpenHandler(InventoryOpenEvent e) {
    
    }
    
    @Override
    public void menuCloseHandler(InventoryCloseEvent e) {
        getUtil().getMain().players().rewards().setFirstJoinItems(e.getInventory().getContents());
    }
    
    @Override
    public void setMenuItems() {
        ItemStack[] inv;
        inv = getUtil().getMain().players().rewards().getFirstJoinItems(0,54);
        final ItemStack filler = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        //0-8
        setInventory(0, inv[0]); setInventory(1, inv[1]); setInventory(2, inv[2]);
        setInventory(3, inv[3]); setInventory(4, inv[4]); setInventory(5, inv[5]);
        setInventory(6, inv[6]); setInventory(7, inv[7]); setInventory(8, inv[8]);
        //9-17
        setInventory(9, filler); setInventory(10, filler); setInventory(11, filler);
        setInventory(12, filler); setInventory(13, filler); setInventory(14, filler);
        setInventory(15, filler); setInventory(16, filler); setInventory(17, filler);
        //18-26
        setInventory(18, inv[18]); setInventory(19, inv[19]); setInventory(20, inv[20]);
        setInventory(21, inv[21]); setInventory(22, inv[22]); setInventory(23, inv[23]);
        setInventory(24, inv[24]); setInventory(25, inv[25]); setInventory(26, inv[26]);
        //27-35
        setInventory(27, filler); setInventory(28, filler); setInventory(29, filler);
        setInventory(30, filler); setInventory(31, filler); setInventory(32, filler);
        setInventory(33, filler); setInventory(34, filler); setInventory(35, filler);
        //36-53
        for(int i=36;i<54;i++) { setInventory(i, inv[i]); }
    
    
    
    
    }
}
