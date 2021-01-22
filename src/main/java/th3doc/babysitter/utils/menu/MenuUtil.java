package th3doc.babysitter.utils.menu;


import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.BasicPlayer;

public class MenuUtil
{
    //INITIALIZE WITH PLAYER
    final private BasicPlayer player;
    private BasicPlayer.Type playerType;
    private String inventoryOwner;
    private boolean editing;
    private ItemStack[] originalInv;
    
    public MenuUtil(BasicPlayer player) { this.player = player; }
    
    public BasicPlayer getPlayer()
    {
        return player;
    }
    
    public Main getMain() {
        return player.main();
    }
    
    public BasicPlayer.Type getPlayerType() {
        return playerType;
    }
    
    public void setPlayerType(BasicPlayer.Type playerType) {
        this.playerType = playerType;
    }
    
    public String getInventoryOwner() {
        return inventoryOwner;
    }
    
    public void setInventoryOwner(String inventoryOwner) {
        this.inventoryOwner = inventoryOwner;
    }
    
    public boolean isEditing() {
        return editing;
    }
    
    public void setEditing(boolean editing) {
        this.editing = editing;
    }
    
    public ItemStack[] getOriginalInv() {
        return originalInv;
    }
    
    public void setOriginalInv(ItemStack[] originalInv) {
        this.originalInv = originalInv;
    }
}
