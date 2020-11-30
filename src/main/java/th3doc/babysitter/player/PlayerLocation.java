package th3doc.babysitter.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;

import java.util.HashMap;

public class PlayerLocation {

    //CONSTRUCTOR
    private Main main;
    public PlayerLocation(Main main) { this.main = main; }

    //LOCATION DATA
    HashMap<String, Location> survivalLastKnown = new HashMap<>();
    public Location getSurvivalLastKnown(Player p) { return survivalLastKnown.get(p.getName()); }
    public void setSurvivalLastKnown(Player p, Location loc)
    {
        ConfigHandler config =
                new ConfigHandler(main, Config._playerData.txt, p.getUniqueId().toString(), Config._playerConfig.txt);
        survivalLastKnown.put(p.getName(), loc);
        config.getConfig().set(Config._survivalLocation.txt, loc);
        config.save();
    }

    //INITIALIZE
    public void initialize(Player p)
    {
        /**
         *
         * LOAD/CREATE CONFIG
         *
         */
        //LOAD INVENTORY CONFIG
        ConfigHandler config =
                new ConfigHandler(main, Config._playerData.txt, p.getUniqueId().toString(), Config._playerConfig.txt);

        //CHECK CONFIG VALUES, CREATE IF EMPTY
        //SURVIVAL LOCATION
        if (!config.getConfig().isSet(Config._survivalLocation.txt))
        {
            config.getConfig().createSection(Config._survivalLocation.txt);
            config.getConfig().set(Config._survivalLocation.txt, p.getLocation());
        }
        //SAVE CONFIG
        config.save();
        /**
         * END LOAD/CREATE CONFIG
         */
        /**
         *
         * LOAD CONFIG DATA
         *
         */
        //SURVIVAL LOCATION
        survivalLastKnown.put(p.getName()
                , config.getConfig().getLocation(Config._survivalLocation.txt));
        /**
         * END LOAD CONFIG DATA
         */
    }

    public void memoryDump(Player p) { survivalLastKnown.remove(p.getName()); }
}
