package th3doc.babysitter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.States;

public class PlayerJoin implements Listener {

    //CONSTRUCTOR
    private Main main;
    public PlayerJoin(Main main) { this.main = main; }

    @EventHandler
    public void playerJoined(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        //INITIALIZE PLAYER
        main.newPlayer(p);

        //CHECK VANISH STATE && ADMIN STATE
        if (main.player(p.getUniqueId()).isAdmin(p.getName()))
        {
            if ((main.player(p.getUniqueId()).admin().getState(p.getName(), States.Vanish)
                    && main.player(p.getUniqueId()).admin().getState(p.getName(), States.Babysit))
                || (main.player(p.getUniqueId()).admin().getState(p.getName(), States.Vanish)
                    && p.hasPermission(Perm._permBypass.txt)))
            {
                e.setJoinMessage("");
            }
        }

        //CHECK FOR VANISHED ADMIN AND HIDE FROM PLAYER
        main.player(p.getUniqueId()).vanishAdmin(p);
    }
}
