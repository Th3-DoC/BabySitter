package th3doc.babysitter.utils.menu.menus;


import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.utils.config.Config;
import th3doc.babysitter.utils.menu.Menu;
import th3doc.babysitter.utils.menu.MenuUtil;

public class SeeEchestMenu extends Menu
{
    public SeeEchestMenu(MenuUtil util, BasicPlayer.Type playerType, String inventoryOwner, boolean editing)
    {
        super(util);
        getUtil().setPlayerType(playerType);
        getUtil().setInventoryOwner(inventoryOwner);
        getUtil().setEditing(editing);
    }
    
    @Override
    public String getMenuName()
    {
        return getUtil().getInventoryOwner() + "'s E-Chest";
    }
    
    @Override
    public int getSlots()
    {
        return 27;
    }
    
    @Override
    public void menuClickHandler(InventoryClickEvent e) {}
    
    @Override
    public void menuOpenHandler(InventoryOpenEvent e) {}
    
    @Override
    public void menuCloseHandler(InventoryCloseEvent e)
    {
        if(getUtil().isEditing())
        {
            ItemStack[] inv = e.getInventory().getContents();
            switch(getUtil().getPlayerType())
            {
                case OFFLINE:
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Config config = getUtil().getMain().players().getOfflinePlayerConfig(getUtil().getMain().players().getCustomPlayerUUID(getUtil().getInventoryOwner()).toString());
                            config.set(BasicPlayer.Paths.E_CHEST.path, getUtil().getPlayer().serializeItemArray(inv));
                            config.set(BasicPlayer.Paths.EDITED.path, true);
                            config.save();
                        }
                    }.runTaskAsynchronously(getUtil().getMain());
                    return;
                case ONLINE:
                    getUtil().getMain().players().getCustomPlayer(getUtil().getInventoryOwner()).getEnderChest().setContents(inv);
                    return;
                default: getUtil().getPlayer().message("Invalid Player Type"); return;
            }
        }
        getUtil().setPlayerType(null);
        getUtil().setInventoryOwner("");
        getUtil().setOriginalInv(null);
        getUtil().setEditing(false);
        getUtil().getPlayer().setMenuOpen(false);
    }
    
    @Override
    public void setMenuItems() { //TODO add clause for admin inventories
        if(getInventory().getSize() <= 27)
        {
            ItemStack[] inv;
            switch(getUtil().getPlayerType()) {
                case OFFLINE:
                    Config config = getUtil().getMain().players().getOfflinePlayerConfig(getUtil().getMain().players().getCustomPlayerUUID(getUtil().getInventoryOwner()).toString());
                    inv = getUtil().getPlayer().deSerializeItemArray(config.getStr(BasicPlayer.Paths.E_CHEST.path));
                    break;
                case ONLINE:
                    inv = getUtil().getMain().players().getCustomPlayer(getUtil().getInventoryOwner()).getEnderChest().getContents();
                    break;
                default: getUtil().getPlayer().message("illegal player type"); return;
            }
            setInventory(inv);
        }
        getUtil().getPlayer().message("error ... inventory to large");
    }
    
}
