package th3doc.babysitter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import th3doc.babysitter.Main;

public class PlayerQuit implements Listener {

    //CONSTRUCTOR
    private Main main;
    public PlayerQuit(Main main) { this.main = main; }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        //REMOVE FROM MEMORY
        main.player().memoryDump(p);
    }
}
