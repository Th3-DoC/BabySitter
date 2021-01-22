package th3doc.babysitter.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.BasicPlayer;

public class PlayerQuit implements Listener {
    private final Main main;
    
    public PlayerQuit(Main main) { this.main = main; }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(main.players().isCustomPlayerOnline(e.getPlayer().getName())) {
            BasicPlayer player = main.players().getCustomPlayer(e.getPlayer().getUniqueId());
            //REMOVE FROM MEMORY
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.memoryDump();
                }
            }.runTaskLater(main, 10L);
        }
        
        if(main.players().getVanishedAdmins().contains(e.getPlayer().getName())) {
            e.setQuitMessage("");
        }
    }
}
