package th3doc.babysitter.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.AdminPlayer;
import th3doc.babysitter.utils.Utils;

public class BlockPlace implements Listener
{
    
    //CONSTRUCTOR
    private final Main main;
    public BlockPlace(Main main) { this.main = main; }
    
    @EventHandler
    public void blockPlace(BlockPlaceEvent e)
    {
        if(main.players().getCustomPlayer(e.getPlayer().getUniqueId()) instanceof AdminPlayer && !e.getPlayer().hasPermission(Utils.Perm._blockPlaceBypass.txt))
        {
            AdminPlayer player = (AdminPlayer) main.players().getCustomPlayer(e.getPlayer().getUniqueId());
            if(player.getState(AdminPlayer.State.ADMIN) && main.utils().getConfig().isSafeBlockEnabled())
            {
                if(!main.utils().getConfig().getSafeBlocks()
                        .contains(e.getBlock().getType().name()))
                {
                    player.sendMessage(Utils.Chat._cancelBlockPlace.txt);
                    e.setCancelled(true);
                }
            }
        }
    }
}
