package th3doc.babysitter.player.location;

import org.bukkit.Location;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;
import th3doc.babysitter.player.PlayerHandler;

public class PlayerLocation {
    
    //VARIABLES
    private final PlayerHandler player;
    private Location survivalLastKnown;

    
    //CONSTRUCTOR
    public PlayerLocation(PlayerHandler player)
    {
        this.player = player;
        //CHECK CONFIG VALUES, CREATE IF EMPTY
        ConfigHandler config = new ConfigHandler(player.getMain()
                , Config._playerData.txt
                , player.getUUID().toString()
                , Config._playerConfig.txt);
        //SURVIVAL LOCATION
        if (!config.getConfig().isSet(Config._survivalLocation.txt))
        {
            config.getConfig().createSection(Config._survivalLocation.txt);
            config.getConfig().set(Config._survivalLocation.txt, player.getPlayer().getLocation());
            //SAVE CONFIG
            config.save();
        }
        survivalLastKnown = config.getConfig().getLocation(Config._survivalLocation.txt);
    }

    
    //GETTERS
    public Location getSurvivalLastKnown() { return survivalLastKnown; }
    
    
    //SETTERS
    public void setSurvivalLastKnown(Location loc)
    {
        survivalLastKnown = loc;
    }
    
    
    //SAVE
    public void save()
    {
        //config
        ConfigHandler config = new ConfigHandler(player.getMain()
                , Config._playerData.txt
                , player.getUUID().toString()
                , Config._playerConfig.txt);
        //set files to save
        config.getConfig().set(Config._survivalLocation.txt, survivalLastKnown);
        //save config
        config.save();
    }
}
