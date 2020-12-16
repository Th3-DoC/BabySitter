package th3doc.babysitter.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import th3doc.babysitter.Main;

public class PlayerJoin implements Listener {

    //CONSTRUCTOR
    private final Main main;
    public PlayerJoin(Main main) { this.main = main; }

    @EventHandler
    public void playerJoined(PlayerJoinEvent e)
    {
        main.newPlayer(e.getPlayer());
        e.setJoinMessage("");
    }
}
