package th3doc.babysitter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.States;

public class PlayerDropItem implements Listener {
    
    //CONSTRUCTOR
    private Main main;
    public PlayerDropItem(Main main) { this.main = main; }
    
    @EventHandler
    public void playerDropItemEvent(PlayerDropItemEvent e) 
    {
        Player p = e.getPlayer();
        if(main.player().isAdmin(p .getName())
                && main.player().admin().getState(p.getName(), States.Admin))
        {
            if(!p.hasPermission(Perm._itemDropBypass.txt)
                        && main.getConfig().getBoolean(Config._safeItemDrop.txt))
            {
                if(!main.getConfig().getStringList(Config._safeBlocks.txt).contains(e.getItemDrop().getName()))
                {
                    p.sendMessage(Chat._cancelItemDrop.txt);
                    e.setCancelled(true);
                }
            }
        }
    }
}
