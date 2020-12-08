package th3doc.babysitter.player.admin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;
import th3doc.babysitter.player.PlayerHandler;

import java.util.UUID;

public class AdminConfig {
    
    //CONSTRUCTOR
    private PlayerHandler player;
    
    public AdminConfig(PlayerHandler player)
    {
        //variables
        this.player = player;
//        p.getAddress();
//        p.getPlayerTime();
//        p.loadData();
//        p.sendSignChange();
//        p.addPassenger();
//        p.eject();
//        p.getFirstPlayed();
//        p.getTicksLived();
//        p.hasPlayedBefore();
        //config
        ConfigHandler config = new ConfigHandler(main
                , Config._playerData.txt
                , p.getUniqueId().toString()
                , Config._adminConfig.txt);
    
        //check config values, create if empty
        //STATES
        if (!config.getConfig().isSet(Config._states.txt))
        {
            config.getConfig().createSection(Config._states.txt);
            ConfigurationSection states = config.getConfig().getConfigurationSection(Config._states.txt);
            //ADMIN STATE
            if (!states.isSet(Config._adminState.txt))
            {
                states.createSection(Config._adminState.txt);
                states.set(Config._adminState.txt, "false");
            }
            //VANISH STATE
            if (!states.isSet(Config._vanishState.txt))
            {
                states.createSection(Config._vanishState.txt);
                states.set(Config._vanishState.txt, "false");
            }
            //FLY STATE
            if (!states.isSet(Config._flyState.txt))
            {
                states.createSection(Config._flyState.txt);
                states.set(Config._flyState.txt, "false");
            }
            //SAVE CONFIG
            config.save();
        }
        //INITIALIZE VARIABLES
        states.put(p.getName(), config.getConfig().getConfigurationSection(Config._states.txt));
    }
    
}
