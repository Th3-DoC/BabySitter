package th3doc.babysitter.player.inventories;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;
import th3doc.babysitter.player.PlayerConfig;
import th3doc.babysitter.player.PlayerHandler;
import th3doc.babysitter.player.data.InvType;
import th3doc.babysitter.player.data.Perm;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventories {
    
    //VARIABLES
    private final PlayerHandler player;
    private final InventoryConfig config;
    private String inventoryEdit = null;
    private final List<ItemStack> invToCheck;
    
    
    //CONSTRUCTOR
    public PlayerInventories(PlayerHandler player)
    {
        this.player = player;
        this.config = new InventoryConfig(player);
        this.invToCheck = new ArrayList<>();
    }
    
    //GETTERS
    public ItemStack[] invToCheck() { return invToCheck.toArray(new ItemStack[0]); }
    public boolean isCheckingInv() { return !invToCheck.isEmpty(); }
    public InventoryConfig getConfig() { return config; }
    public boolean isEditingInv() { return inventoryEdit != null; }
    
    //SETTERS
    public void setEditingInv(String guiName) { inventoryEdit = guiName; }
    public void stopCheckingInv() { invToCheck.clear(); }
    public void saveInventoryToCheck(ItemStack item) { invToCheck.add(item); }
    public void saveInvEdit(InventoryCloseEvent e)
    {
        //ARE WE EDITING AN INVENTORY
        if(inventoryEdit != null)
        {
            String[] title = e.getView().getTitle().split(" ");
            if(e.getView().getTitle().equals(inventoryEdit))
            {
                ItemStack[] event_inv = e.getInventory().getContents();
                List<ItemStack> save_inv = new ArrayList<>();
                for(ItemStack item : event_inv)
                {
                    if(save_inv.size() < 41) { save_inv.add(item); }
                    else { break; }
                }
                ItemStack[] new_inv = save_inv.toArray(new ItemStack[0]);
                if(player.getMain().getServer().getPlayer(title[0]) != null
                   && !player.getMain().getServer().getPlayer(title[0]).hasPermission(Perm._invBypass.txt))
                {
                    if(title[1].equals(InvType.Inventory.name()))
                    { player.getMain().getServer().getPlayer(title[0]).getInventory().setContents(new_inv); }
                    else if(title[1].equals(InvType.EnderChest.name()))
                    { player.getMain().getServer().getPlayer(title[0]).getEnderChest().setContents(new_inv); }
                }
                else if(PlayerConfig.playerList.containsKey(title[0]))
                {
                    String offlineUUID = PlayerConfig.playerList.get(title[0]);
                    new BukkitRunnable()
                    {

                        @Override
                        public void run()
                        {
                            ConfigHandler config = new ConfigHandler(player.getMain(),
                                                                     Config._playerData.txt,
                                                                     offlineUUID,
                                                                     Config._invConfig.txt);
                            if(!config.getConfig().getBoolean(Config._invBypass.txt))
                            {
                                if(title[1].equals(InvType.Inventory.name()))
                                {
                                    config.getConfig().getConfigurationSection(Config._survivalInv.txt)
                                          .set(Config._inv.txt, e.getInventory().getContents());
                                    config.getConfig().set(Config._edited.txt, true);
                                }
                                else if(title[1].equals(InvType.EnderChest.name()))
                                {
                                    config.getConfig().getConfigurationSection(Config._survivalInv.txt)
                                          .set(Config._eChest.txt, e.getInventory().getContents());
                                    config.getConfig().set(Config._edited.txt, true);
                                }
                            }
                        }
                        
                    }.runTaskAsynchronously(player.getMain());
                }
            }
        }
        inventoryEdit = null;
    }
}
