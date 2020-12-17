package th3doc.babysitter.player.inventories;

import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.player.PlayerHandler;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventories {
    
    //VARIABLES
    private final PlayerHandler player;
    private final InventoryConfig config;
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
    
    
    //SETTERS
    public void stopCheckingInv() { invToCheck.clear(); }
    public void saveInventoryToCheck(ItemStack item) { invToCheck.add(item); }
}
