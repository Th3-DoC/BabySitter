package th3doc.babysitter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.Main;

public class PlayerQuit implements Listener {

    //CONSTRUCTOR
    private final Main main;
    public PlayerQuit(Main main) { this.main = main; }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        //REMOVE FROM MEMORY
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                main.getPlayer(p.getUniqueId()).memoryDump();
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        main.removePlayer(p.getUniqueId());
                    }
                }.runTaskLaterAsynchronously(main, 20L);
            }
        }.runTaskLater(main, 10L);
    }
}
