package th3doc.babysitter.player.admin;

import org.bukkit.configuration.ConfigurationSection;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;
import th3doc.babysitter.player.PlayerHandler;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.PlayerType;
import th3doc.babysitter.player.data.States;

import java.util.ArrayList;
import java.util.List;

public class AdminConfig {
    
    //VARIABLES
    public static List<String> adminList;
    public static List<String> vanishedAdmins;
    private final PlayerHandler player;
    private boolean adminState;
    private boolean flyState;
    private boolean vanishState;
    
    
    //CONSTRUCTOR
    public AdminConfig(PlayerHandler player)
    {
        adminList = new ArrayList<>();
        vanishedAdmins = new ArrayList<>();
        this.player = player;
        if (player.checkPermGroup(PlayerType.Admin, "")
            || player.getPlayer().hasPermission(Perm._permBypass.txt))
        {
            //config
            ConfigHandler config = new ConfigHandler(player.getMain()
                    , Config._playerData.txt
                    , player.getUUID().toString()
                    , Config._adminConfig.txt);
            
            //check config values, create if empty
            //STATES
            if(!config.getConfig().isSet(Config._states.txt))
            {
                config.getConfig().createSection(Config._states.txt);
                ConfigurationSection states = config.getConfig().getConfigurationSection(Config._states.txt);
                //ADMIN STATE
                if(!states.isSet(Config._adminState.txt))
                {
                    states.createSection(Config._adminState.txt);
                    states.set(Config._adminState.txt, "false");
                }
                //VANISH STATE
                if(!states.isSet(Config._vanishState.txt))
                {
                    states.createSection(Config._vanishState.txt);
                    states.set(Config._vanishState.txt, "false");
                }
                //FLY STATE
                if(!states.isSet(Config._flyState.txt))
                {
                    states.createSection(Config._flyState.txt);
                    states.set(Config._flyState.txt, "false");
                }
                //SAVE CONFIG
                config.save();
            }
            //INITIALIZE VARIABLES
            this.adminState = config.getConfig().getConfigurationSection(Config._states.txt).getBoolean(Config._adminState.txt);
            this.flyState = config.getConfig().getConfigurationSection(Config._states.txt).getBoolean(Config._flyState.txt);
            this.vanishState = config.getConfig().getConfigurationSection(Config._states.txt).getBoolean(Config._vanishState.txt);
        }
    }
    
    
    //GETTERS
    public boolean getState(States state)
    {
        if (state == States.ADMIN) { return adminState; }
        else if (state == States.FLY) { return flyState; }
        else if (state == States.VANISH) { return vanishState; }
        return false;
    }
    
    
    //SETTERS
    public void setState(boolean boo, States state)
    {
        if (state == States.ADMIN) { adminState = boo;}
        else if (state == States.VANISH) { vanishState = boo;}
        else if (state == States.FLY) { flyState = boo;}
    }
    
    
    //SAVE
    public void save()
    {
        ConfigHandler config = new ConfigHandler(player.getMain(),
                                                 Config._playerData.txt,
                                                 player.getUUID().toString(),
                                                 Config._adminConfig.txt);
        ConfigurationSection states =
                config.getConfig().getConfigurationSection(Config._states.txt);
        states.set(Config._adminState.txt, adminState);
        states.set(Config._flyState.txt, flyState);
        states.set(Config._vanishState.txt, vanishState);
        config.save();
    }
}
