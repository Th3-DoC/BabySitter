package th3doc.babysitter.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.utils.Utils;

public class PlayerDropItem implements Listener {
    private final Main main;
    
    public PlayerDropItem(Main main) { this.main = main; }
    
    @EventHandler
    public void playerDropItemEvent(PlayerDropItemEvent e) {
        if(main.players().getCustomPlayer(e.getPlayer().getUniqueId()) instanceof AdminPlayer) {
            AdminPlayer player = (AdminPlayer) main.players().getCustomPlayer(e.getPlayer().getUniqueId());
            if(player.getState(AdminPlayer.State.ADMIN) &&
               main.utils().getConfig().isSafeItemEnabled() &&
               !player.hasPermission(Utils.Perm._itemDropBypass.txt)) {
                if(!main.utils().getConfig().getSafeBlocks().contains(e.getItemDrop().getName())) {
                    player.sendMessage(Utils.Chat._cancelItemDrop.txt);
                    e.setCancelled(true);
                }
            }
            
        }
    }
}
