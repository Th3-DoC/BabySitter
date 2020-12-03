package th3doc.babysitter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.States;

import java.util.Arrays;

public class PlayerCommandPreProcess implements Listener {

    //CONSTRUCTOR
    private Main main;
    public PlayerCommandPreProcess(Main main) { this.main = main; }

    @EventHandler
    public void commandPreProcess(PlayerCommandPreprocessEvent e)
    {
        /**
         *
         * TELEPORT USER ONLY
         *
         */
        //TELEPORT COMMAND CHECK
        if ((e.getMessage().toLowerCase().contains("teleport") || e.getMessage().toLowerCase().contains("tp"))
                && !e.getPlayer().hasPermission(Perm._tpBypass.txt))
        {
            String[] message = e.getMessage().split(" ");
            //CHECK PLAYER IS OUR USER
            for (Player player : main.getServer().getOnlinePlayers())
            {
                if (message[1].contains(player.getName()))
                {
                    if (!player.getName().equals(e.getPlayer().getName())) {
                        e.getPlayer().sendMessage(Chat._tpAdminOnly.txt);
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
        /**
         *
         * DISABLE CREATIVE FOR ADMINS
         *
         */
        //CREATIVE CANCEL
        if (e.getMessage().toLowerCase().contains("creative")
                && !main.getConfig().getBoolean(Config._allowCreative.txt)
                && !e.getPlayer().hasPermission(Perm._creativeBypass.txt))
        {
            e.getPlayer().sendMessage(Chat._creativeDisabled.txt);
            e.setCancelled(true);
        }
        /**
         *
         * DISABLE GIVE TO ADMINS IN SURVIVAL
         *
         */
        //GIVE CANCEL
        if (e.getMessage().contains("give")
                && (!main.getConfig().getBoolean(Config._allowGive.txt)
                    || !main.getConfig().getBoolean(Config._adminGive.txt))
                && !e.getPlayer().hasPermission(Perm._giveBypass.txt))
        {
            boolean allowGive = main.getConfig().getBoolean(Config._allowGive.txt);
            boolean adminGive = main.getConfig().getBoolean(Config._adminGive.txt);
            //CASE ALLOW GIVE
            if (!allowGive)
            {
                e.getPlayer().sendMessage(Chat._giveDisabled.txt);
                e.setCancelled(true);
            }
            //CASE ADMIN GIVE
            if (!adminGive)
            {
                String[] args = e.getMessage().split(" ");
                //CHECK IF LENGTH IS <= 1
                if (args.length <= 1) {
                    return;
                }
                //CHECK SECOND ARGUMENT FOR PLAYER NAME
                if (Arrays.toString(main.getServer().getOnlinePlayers().toArray()).contains(args[1])
                        && main.player().isAdmin(args[1])
                        && !main.player().admin().getState(args[1], States.Babysit))
                {
                    e.getPlayer().sendMessage(Chat._adminInSurvival.txt);
                    e.setCancelled(true);
                }
            }
        }
    }
}
