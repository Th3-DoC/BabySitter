package th3doc.babysitter.utils.config;

import org.bukkit.configuration.InvalidConfigurationException;
import th3doc.babysitter.Main;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.debug.Debug;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

//TODO obsolete default config with command based saving of lists etc. build basic lists in config to load at start.
//TODO Allow canceling commands by online user with ("/command", "subcommand", args[5]) command pre process ? auto generate from player input! save for reload/restart

public class DefaultConfig {
    
    private enum Paths
    {
        CONFIG_YML("config.yml"),
        ENDERMAN_GRIEF("enderman-griefing"),
        ALLOW_CREATIVE("allow-creative"),
        ALLOW_GIVE("allow-give"),
        ADMIN_GIVE("admin-to-admin-give"),
        ADMIN_PERM_LIST("admin-permissions-list"),
        SPECIAL_PERM_LIST("special-permissions-list"),
        SPECIAL_PERM_ALWAYS("special-permissions-always"),
        PERMISSION("permission"),
        VALUE("value"),
        SPECIAL_PERMS("special-permissions"),
        SPECIAL_RANKS("special-ranks"),
        ADMIN_RANKS("admin-ranks"),
        SAFE_BLOCK_PLACE("safe-blocks-to-place"),
        SAFE_BLOCKS("safe-blocks"),
        ADMIN_FLIGHT("admin-survival-flight"),
        FORCE_SPECTATE("force-spectate"),
        CONFIG_SAVE_TIME("config-save-intervals"),
        IS_SLEEPING_IGNORED("is-sleeping-ignored"),
        SAFE_ITEM_DROP("safe-items-to-drop");
        
        public String path;
    
        Paths(String path) {
            this.path = path;
        }
    }
    
    //VARIABLES
    final private Debug debug;
    final private boolean endermanGriefing;
    final private boolean specialPermissions;
    final private boolean specialPermsAlways;
    final private boolean allowCreative;
    final private boolean adminFlight;
    final private boolean adminGive;
    final private boolean allowGive;
    final private boolean forceSpectate;
    final private boolean safeBlockPlace;
    final private boolean safeItemDrops;
    final private List<String> specialRanks;
    final private List<String> adminRanks;
    final private List<String> safeBlocks;
    final private HashMap<String, Boolean> specialPermissionList;
    final private HashMap<String, Boolean> adminPermissionList;
    final private long configSaveTime;
    final private boolean isSleepingIgnored;
    
    
    //CONSTRUCTOR
    public DefaultConfig(Main main)
    {
        this.debug = main.debug();
        //DEBUG
        if(debug.utils())
        { debug.message("loading default config"); }
        //**********
        //config.yml
        //**********
        File file = new File(main.getDataFolder(), Paths.CONFIG_YML.path);
    
        if (!main.getDataFolder().exists()) { main.getDataFolder().mkdirs(); }
        if (!file.exists())
        {
            main.getLogger().info(Utils.Chat._configMissing.txt);
            main.saveDefaultConfig();
        }
        main.getLogger().info(Utils.Chat._configFound.txt);
        try { main.getConfig().load(file); }
        catch (IOException | InvalidConfigurationException e) { e.printStackTrace(); }
        
        // initialize advanced variables
        this.specialPermissionList = new HashMap<>();
        this.adminPermissionList = new HashMap<>();
        
        // load config values
        this.configSaveTime = ((main.getConfig().getLong(Paths.CONFIG_SAVE_TIME.path) * 20) * 60);
        this.specialPermissions = main.getConfig().getBoolean(Paths.SPECIAL_PERMS.path);
        this.specialPermsAlways = main.getConfig().getBoolean(Paths.SPECIAL_PERM_ALWAYS.path);
        this.allowCreative = main.getConfig().getBoolean(Paths.ALLOW_CREATIVE.path);
        this.adminFlight = main.getConfig().getBoolean(Paths.ADMIN_FLIGHT.path);
        this.adminGive = main.getConfig().getBoolean(Paths.ADMIN_GIVE.path);
        this.allowGive = main.getConfig().getBoolean(Paths.ALLOW_GIVE.path);
        this.forceSpectate = main.getConfig().getBoolean(Paths.FORCE_SPECTATE.path);
        this.safeBlockPlace = main.getConfig().getBoolean(Paths.SAFE_BLOCK_PLACE.path);
        this.safeItemDrops = main.getConfig().getBoolean(Paths.SAFE_ITEM_DROP.path);
        this.specialRanks = main.getConfig().getStringList(Paths.SPECIAL_RANKS.path);
        this.adminRanks = main.getConfig().getStringList(Paths.ADMIN_RANKS.path);
        this.safeBlocks = main.getConfig().getStringList(Paths.SAFE_BLOCKS.path);
        this.endermanGriefing = main.getConfig().getBoolean(Paths.ENDERMAN_GRIEF.path);
        this.isSleepingIgnored = main.getConfig().getBoolean(Paths.IS_SLEEPING_IGNORED.path);
        // load advanced variables
        for(String key : main.getConfig().getConfigurationSection(Paths.SPECIAL_PERM_LIST.path).getKeys(false))
        {
            if(!key.equals(""))
            {
                String permission = main.getConfig().getConfigurationSection(Paths.SPECIAL_PERM_LIST.path)
                                        .getConfigurationSection(key).getString(Paths.PERMISSION.path);
                boolean value = main.getConfig().getConfigurationSection(Paths.SPECIAL_PERM_LIST.path)
                                    .getConfigurationSection(key).getBoolean(Paths.VALUE.path);
                if((permission != null) && (!permission.equals("")))
                {
                    specialPermissionList.put(permission, value);
                }
            }
        }
        for(String key : main.getConfig().getConfigurationSection(Paths.ADMIN_PERM_LIST.path).getKeys(false))
        {
            if(!key.equals(""))
            {
                String permission = main.getConfig().getConfigurationSection(Paths.ADMIN_PERM_LIST.path)
                                        .getConfigurationSection(key).getString(Paths.PERMISSION.path);
                boolean value = main.getConfig().getConfigurationSection(Paths.ADMIN_PERM_LIST.path)
                                    .getConfigurationSection(key).getBoolean(Paths.VALUE.path);
                if((permission != null) && (!permission.equals("")))
                {
                    adminPermissionList.put(permission, value);
                }
            }
        }
        //DEBUG
        if(debug.utils())
        { debug.message("loaded default config"); }
    }
    
    
    //GETTERS
    public boolean isEndermanGriefingAllowed() { return this.endermanGriefing; }
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
    public long getConfigSaveTime() { return this.configSaveTime; }
    public boolean getSleepingIgnored() { return this.isSleepingIgnored; }
}
