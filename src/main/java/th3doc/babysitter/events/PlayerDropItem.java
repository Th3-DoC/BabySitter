package th3doc.babysitter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.States;

public class PlayerDropItem implements Listener {
    
    //CONSTRUCTOR
    private final Main main;
    public PlayerDropItem(Main main) { this.main = main; }
    
    @EventHandler
    public void playerDropItemEvent(PlayerDropItemEvent e) 
    {
        Player p = e.getPlayer();
        if(main.getPlayer(p.getUniqueId()).isAdmin()
                && main.getPlayer(p.getUniqueId()).admin().getConfig().getState(States.ADMIN))
        {
            if(!p.hasPermission(Perm._itemDropBypass.txt)
                        && main.defaultConfig().isSafeItemEnabled())
            {
                if(!main.defaultConfig().getSafeBlocks().contains(e.getItemDrop().getName()))
                {
                    p.sendMessage(Chat._cancelItemDrop.txt);
                    e.setCancelled(true);
                }
            }
        }
    }
}
