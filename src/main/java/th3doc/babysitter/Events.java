package th3doc.babysitter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import th3doc.babysitter.enums.Chat;
import th3doc.babysitter.enums.Config;
import th3doc.babysitter.enums.Perm;

public class Events implements Listener {

    //CONSTRUCTOR
    private Main main;
    public Events(Main main) { this.main = main; }

    /**
     *
     * PLAYER JOIN EVENT
     *
     */
    @EventHandler
    public void playerJoined(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        //INITIALIZE PLAYER
        main.player().admin().initializeAdmin(p);

        //CHECK VANISH STATE && ADMIN STATE
        if (main.player().admin().config(p).getConfig().getBoolean(Config._vanishState.txt)
                && main.player().admin().config(p).getConfig().getBoolean(Config._adminState.txt))
        {
            e.setJoinMessage("");
        }

        //CHECK FOR VANISHED ADMIN AND HIDE FROM PLAYER
        main.player().admin().vanishJoinedPlayer(p);
    }
    /**
     * PLAYER JOIN END
     */

    /**
     *
     *PLAYER LEAVE EVENT
     *
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        //REMOVE FROM MEMORY
        main.player().admin().removeFromMemory(p);
    }
    /**
     * PLAYER LEAVE END
     */

    /**
     *
     * COMMAND PRE-PROCESS EVENT
     *
     */
    @EventHandler
    public void commandPreProcess(PlayerCommandPreprocessEvent e)
    {
        //CREATIVE CANCEL
        if (e.getMessage().toLowerCase().contains("creative")
                && !main.getConfig().getBoolean(Config._allowCreative.txt))
        {
            //CHECK PLAYER IS NOT OP
            if (!e.getPlayer().isOp()
                    && !e.getPlayer().hasPermission(Perm._opAdmin.txt))
            {
                e.getPlayer().sendMessage(Chat._creativeDisabled.txt);
                e.setCancelled(true);
            }
        }
        //GIVE CANCEL
        if ((e.getMessage().contains("give") && e.getPlayer().hasPermission(Perm._giveCommand.txt))
                && (!main.getConfig().getBoolean(Config._allowGive.txt) || !main.getConfig().getBoolean(Config._adminGive.txt))
                && (!e.getPlayer().hasPermission(Perm._opAdmin.txt) && !e.getPlayer().isOp())) {
            //CASE ALLOW GIVE
            if (!main.getConfig().getBoolean(Config._allowGive.txt))
            {
                e.getPlayer().sendMessage(Chat._giveDisabled.txt);
                e.setCancelled(true);
            }
            //CASE ADMIN GIVE
            if (!main.getConfig().getBoolean(Config._adminGive.txt))
            {
                String[] args = e.getMessage().split(" ");
                //CHECK IF LENGTH IS >= 1
                if (args.length <= 1) {
                    return;
                }
                //CHECK SECOND ARGUMENT FOR PLAYER NAME
                if (main.getServer().getPlayer(args[1]) instanceof Player)
                {
                    Player p = main.getServer().getPlayer(args[1]);
                    //CHECK IF RECEIVING PLAYER IS ADMIN
                    if (main.player().admin().list().contains(p.getUniqueId()))
                    {
                        //MAKE SURE ADMIN IS NOT IN SURVIVAL
                        if (!main.player().admin().config(p).getConfig().getBoolean(Config._adminState.txt))
                        {
                            e.getPlayer().sendMessage(Chat._adminInSurvival.txt);
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
    /**
     * COMMAND EVENT END
     */

    /**
     *
     * INVENTORY CLOSE EVENT
     *
     */
    @EventHandler
    public void inventoryCloseEvent(InventoryCloseEvent e)
    {
        main.player().admin().saveInvEdit((Player) e.getPlayer(), e);
    }
    /**
     * END INVENTORY CLOSE
     */

}
