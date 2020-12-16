package th3doc.babysitter;

import org.bukkit.configuration.InvalidConfigurationException;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.player.data.Chat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class DefaultConfig {
    
    //VARIABLES
    private final boolean specialPermissions;
    private final boolean specialPermsAlways;
    private final boolean allowCreative;
    private final boolean adminFlight;
    private final boolean adminGive;
    private final boolean allowGive;
    private final boolean forceSpectate;
    private final boolean safeBlockPlace;
    private final boolean safeItemDrops;
    private final List<String> specialRanks;
    private final List<String> adminRanks;
    private final List<String> safeBlocks;
    private final HashMap<String, Boolean> specialPermissionList;
    private final HashMap<String, Boolean> adminPermissionList;
    
    
    //CONSTRUCTOR
    public DefaultConfig(Main main)
    {
        //**********
        //config.yml
        //**********
        File file = new File(main.getDataFolder(), Config._config.txt);
    
        if (!main.getDataFolder().exists()) { main.getDataFolder().mkdirs(); }
        if (!file.exists())
        {
            main.getLogger().info(Chat._configMissing.txt);
            main.saveDefaultConfig();
        }
        main.getLogger().info(Chat._configFound.txt);
        try { main.getConfig().load(file); }
        catch (IOException | InvalidConfigurationException e) { e.printStackTrace(); }
        
        //initialize advanced variables
        this.specialPermissionList = new HashMap<>();
        this.adminPermissionList = new HashMap<>();
        
        //load config values
        this.specialPermissions = main.getConfig().getBoolean(Config._specialPermissions.txt);
        this.specialPermsAlways = main.getConfig().getBoolean(Config._specialPermsAlways.txt);
        this.allowCreative = main.getConfig().getBoolean(Config._allowCreative.txt);
        this.adminFlight = main.getConfig().getBoolean(Config._adminFlight.txt);
        this.adminGive = main.getConfig().getBoolean(Config._adminGive.txt);
        this.allowGive = main.getConfig().getBoolean(Config._allowGive.txt);
        this.forceSpectate = main.getConfig().getBoolean(Config._forceSpectate.txt);
        this.safeBlockPlace = main.getConfig().getBoolean(Config._safeBlockPlace.txt);
        this.safeItemDrops = main.getConfig().getBoolean(Config._safeItemDrop.txt);
        this.specialRanks = main.getConfig().getStringList(Config._specialRanks.txt);
        this.adminRanks = main.getConfig().getStringList(Config._adminRanks.txt);
        this.safeBlocks = main.getConfig().getStringList(Config._safeBlocks.txt);
        for(String key : main.getConfig().getConfigurationSection(Config._specialPermissionList.txt).getKeys(false))
        {
            if(!key.equals(""))
            {
                String permission = main.getConfig().getConfigurationSection(Config._specialPermissionList.txt)
                                        .getConfigurationSection(key).getString(Config._permission.txt);
                boolean value = main.getConfig().getConfigurationSection(Config._specialPermissionList.txt)
                                    .getConfigurationSection(key).getBoolean(Config._value.txt);
                if((permission != null) && (!permission.equals("")))
                {
                    specialPermissionList.put(permission, value);
                }
            }
        }
        for(String key : main.getConfig().getConfigurationSection(Config._adminPermissionList.txt).getKeys(false))
        {
            if(!key.equals(""))
            {
                String permission = main.getConfig().getConfigurationSection(Config._adminPermissionList.txt)
                                        .getConfigurationSection(key).getString(Config._permission.txt);
                boolean value = main.getConfig().getConfigurationSection(Config._adminPermissionList.txt)
                                    .getConfigurationSection(key).getBoolean(Config._value.txt);
                if((permission != null) && (!permission.equals("")))
                {
                    adminPermissionList.put(permission, value);
                }
            }
        }
    }
    
    
    //GETTERS
    public boolean isSpecialPermissionsActive() { return this.specialPermissions; }
    public boolean isSpecialPermsAlwaysActive() { return this.specialPermsAlways; }
    public boolean canAdminUseCreative() { return this.allowCreative; }
    public boolean canAdminFlySurvival() { return this.adminFlight; }
    public boolean canAdminGiveToAdmin() { return this.adminGive; }
    public boolean canAdminGive() { return this.allowGive; }
    public boolean isSpectateForced() { return this.forceSpectate; }
    public boolean isSafeBlockEnabled() { return this.safeBlockPlace; }
    public boolean isSafeItemEnabled() { return this.safeItemDrops; }
    public List<String> getSpecialRanks() { return this.specialRanks; }
    public List<String> getAdminRanks() { return this.adminRanks; }
    public List<String> getSafeBlocks() { return this.safeBlocks; }
    public HashMap<String, Boolean> getSpecialPermissionsList() { return this.specialPermissionList; }
    public HashMap<String, Boolean> getAdminPermissionsList() { return this.adminPermissionList; }
}
