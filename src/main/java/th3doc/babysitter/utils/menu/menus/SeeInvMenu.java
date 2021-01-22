package th3doc.babysitter.utils.menu.menus;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.config.Config;
import th3doc.babysitter.utils.menu.Menu;
import th3doc.babysitter.utils.menu.MenuUtil;

import java.util.Arrays;

public class SeeInvMenu extends Menu
{
    public SeeInvMenu(MenuUtil util, BasicPlayer.Type playerType, String inventoryOwner, boolean editing) {
        super(util);
        getUtil().setPlayerType(playerType);
        getUtil().setInventoryOwner(inventoryOwner);
        getUtil().setEditing(editing);
    }
    
    @Override
    public String getMenuName() {
        return getUtil().getInventoryOwner() + "'s Inventory";
    }
    
    @Override
    public int getSlots() {
        return 54;
    }
    
    @Override
    public void menuClickHandler(InventoryClickEvent e) {
        final int[] lockedObj = new int[]{2,3,5,6,9,10,11,12,13,14,15,16,17};
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
        if(getUtil().isEditing())
        {
            ItemStack[] inv = e.getInventory().getContents();
            getUtil().getPlayer().message("sorted inv = " + Arrays.toString(Arrays.stream(inv).toArray()));
            ItemStack[] inv1 = new ItemStack[41];
            getUtil().getPlayer().message("inventory close was editing, saving now");
            for(int i = 0; i <= 41; i++) {
                    if(i < 36) {
                        inv1[i] = inv[i + 18];
                    } else if(i == 36) {
                        inv1[36] = inv[8];
                    } else if(i == 37) {
                        inv1[37] = inv[7];
                    } else if(i == 38) {
                        inv1[38] = inv[1];
                    } else if(i == 39) {
                        inv1[39] = inv[0];
                    } else {
                        inv1[40] = inv[4];
                    }
            }
            getUtil().getPlayer().message("sorted inv = " + Arrays.toString(Arrays.stream(inv1).toArray()));
            switch(getUtil().getPlayerType())
            {
                case OFFLINE:
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Config config = getUtil().getMain().players().getOfflinePlayerConfig(getUtil().getMain().players().getCustomPlayerUUID(getUtil().getInventoryOwner()).toString());
                            config.set(BasicPlayer.Paths.INV.path, getUtil().getPlayer().serializeItemArray(inv1));
                            config.set(BasicPlayer.Paths.EDITED.path, true);
                            config.save();
                        }
                    }.runTaskAsynchronously(getUtil().getMain());
                    return;
                case ONLINE:
                    getUtil().getMain().players().getCustomPlayer(getUtil().getInventoryOwner()).getPlayer().getInventory().setContents(inv1);
                    return;
                default: getUtil().getPlayer().message("Invalid Player Type"); return;
            }
        }
        getUtil().setPlayerType(null);
        getUtil().setInventoryOwner("");
        getUtil().setOriginalInv(null);
        getUtil().setEditing(false);
    }
    
    @Override
    public void setMenuItems() { //TODO add clause for admin inventories
        
            ItemStack[] inv;
            switch(getUtil().getPlayerType()) {
                case OFFLINE:
                    Config config = getUtil().getMain().players().getOfflinePlayerConfig(getUtil().getMain().players().getCustomPlayerUUID(getUtil().getInventoryOwner()).toString());
                    inv = getUtil().getPlayer().deSerializeItemArray(config.getStr(BasicPlayer.Paths.INV.path));
                    getUtil().getPlayer().message("offline " + Arrays.toString(Arrays.stream(inv).toArray()));
                    break;
                case ONLINE:
                    inv = getUtil().getMain().players().getCustomPlayer(getUtil().getInventoryOwner()).getPlayer().getInventory().getContents();
                    getUtil().getPlayer().message("online");
                    break;
                default: getUtil().getPlayer().message("illegal player type"); return;
            }
            getUtil().getPlayer().message("setting filler");
            final ItemStack filler = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
            //0-8
            getUtil().getPlayer().message("setting inv to " + Arrays.toString(Arrays.stream(inv).toArray()));
            setInventory(0, inv[39]); setInventory(1, inv[38]); setInventory(2, filler);
            setInventory(3, filler); setInventory(4, inv[40]); setInventory(5, filler);
            setInventory(6, filler); setInventory(7, inv[37]); setInventory(8, inv[36]);
            //9-17
            setInventory(9, filler); setInventory(10, filler); setInventory(11, filler);
            setInventory(12, filler); setInventory(13, filler); setInventory(14, filler);
            setInventory(15, filler); setInventory(16, filler); setInventory(17, filler);
            for(int i=0;i<36;i++) { setInventory(i+18, inv[i]); }
            getUtil().getPlayer().message("loaded");
    }
}
