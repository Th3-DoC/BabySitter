package th3doc.babysitter.utils;


import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.BasicPlayer;
import th3doc.babysitter.events.*;
import th3doc.babysitter.utils.commands.babysit.BsCmd;
import th3doc.babysitter.utils.commands.entity.EntityCommand;
import th3doc.babysitter.utils.commands.give.GiveCommand;
import th3doc.babysitter.utils.commands.restore.RestoreCmd;
import th3doc.babysitter.utils.commands.rewards.RewardsCmd;
import th3doc.babysitter.utils.commands.see.SeeCmd;
import th3doc.babysitter.utils.config.DefaultConfig;
import th3doc.babysitter.utils.config.Save;
import th3doc.babysitter.utils.debug.Debug;
import th3doc.babysitter.utils.debug.DebugMode;
import th3doc.babysitter.utils.debug.TestCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Utils
{
    public enum Calender { DATE, TIME }
    
    public enum Perm {
        
        _babysitCommand("babysitter.command.bs"),
        _vanishCommand("babysitter.command.vanish"),
        _godCommand("babysitter.command.god"),
        RESTORE_CMD("babysitter.command.restore"),
        BACK_CMD("babysitter.command.back"),
        _flyCommand("babysitter.command.fly"),
        _giveCommand("babysitter.command.give"),
        _invSeeCommand("babysitter.command.see"),
        ENTITY_CMD("babysitter.command.entity"),
        _invEdit("babysitter.command.edit"),
        DEBUG("babysitter.command.debug"),
        FORCE_SPEC_BYPASS("babysitter.bypass.spec"),
        _blockPlaceBypass("babysitter.bypass.blocks"),
        _giveBypass("babysitter.bypass.give"),
        _tpBypass("babysitter.bypass.tp"),
        _creativeBypass("babysitter.bypass.creative"),
        _permBypass("babysitter.bypass.perms"),
        _invBypass("babysitter.bypass.inv"),
        _seeBypass("babysitter.bypass.see"),
        _itemDropBypass("babysitter.bypass.item"),
        _flyBypass("babysitter.bypass.flight"),
        REWARDS_CMD("babysitter.command.rewards"),
        _vanishBypass("babysitter.bypass.vanish");
        
        public String txt;
        
        Perm(String txt) {
            this.txt = txt;
        }
    }
    
    public enum Chat {
        /**
         * CONFIG
         */
        _onEnable(ChatColor.GREEN + "" + ChatColor.ITALIC + "BabySitter Enabled !"),
        _configMissing(ChatColor.GREEN + "" + ChatColor.ITALIC + "Configuration file not found! Creating file."),
        _configFound(ChatColor.GREEN + "" + ChatColor.ITALIC + "Configuration file found! Loading file."),
        /**
         * BABYSITTER
         */
        _babySittingTime(ChatColor.GREEN + "" + ChatColor.ITALIC + "BabySitting Time!"),
        _babySittingDone(ChatColor.GREEN + "" + ChatColor.ITALIC + "BabySitting Done, For Now..."),
        _godOff(ChatColor.GREEN + "" + ChatColor.ITALIC + "God_Mode OFF"),
        _godOn(ChatColor.GREEN + "" + ChatColor.ITALIC + "God_Mode ON"),
        _flyOn(ChatColor.GREEN + "" + ChatColor.ITALIC + "Fly_Mode ON"),
        _flyOff(ChatColor.GREEN + "" + ChatColor.ITALIC + "Fly_Mode OFF"),
        CHEST_ON(ChatColor.GREEN + "" + ChatColor.ITALIC + "Chest_Edit ON"),
        CHEST_OFF(ChatColor.GREEN + "" + ChatColor.ITALIC + "Chest_Edit OFF"),
        _vanishOn(ChatColor.YELLOW + "PSSSST Vanish Is Still Active XD"),
        _fakeLogOut(" left the game"),//Chat Colour Handled by before p.getname()
        _fakeLogIn(" joined the game"),
        INVALID_ENTITY_SPAWN(ChatColor.RED + "" + ChatColor.ITALIC + "Invalid Entity Selection"),
        INVALID_TARGET_BLOCK(ChatColor.RED + "" + ChatColor.ITALIC + "Invalid Target Block, You Must Be Within 10 Blocks"),
        /**
         * PERMISSION
         */
        INVALID_CMD(ChatColor.RED + "Invalid Command, Please Use ... "),
        _noConsole(ChatColor.RED + "" + ChatColor.ITALIC + "This Command Cannot Be Run From Console!"),
        _cancelBlockPlace(ChatColor.RED + "" + ChatColor.ITALIC + "That Is Not A Safe Block To Place There."),
        _invalidGive(ChatColor.RED + "" + ChatColor.ITALIC + "Invalid Command, Please Use ...\n /give <item> <count> <player>"),
        _invalidViewerCommand(ChatColor.RED + "" + ChatColor.ITALIC + "Invalid Selection <USE>/see <player> <inventoryType> <edit>"),
        _creativeDisabled(ChatColor.RED + "" + ChatColor.ITALIC + "Creative Game Mode Disabled."),
        _giveDisabled(ChatColor.RED + "" + ChatColor.ITALIC + "Give Command is Disabled."),
        _adminInSurvival(ChatColor.RED + "" + ChatColor.ITALIC + "You Can Not /Give Admin's In Survival."),
        _noSLoc(ChatColor.RED + "" + ChatColor.ITALIC + "Error Loading Survival Location, Please Advise An Admin."),
        _tpAdminOnly(ChatColor.RED + "" + ChatColor.ITALIC + "You May Only Teleport Yourself."),
        _cancelItemDrop(ChatColor.RED + "" + ChatColor.ITALIC + "It Is Not Safe To Drop That Item."),
        UNIQUE_NAME(ChatColor.RED + "" + ChatColor.ITALIC + "Invalid Name, Make sure its Unique!"),
        INVALID_TARGET(ChatColor.RED + "" + ChatColor.ITALIC + "Invalid Target. Please Try Again.");
        
        public String txt;
        
        Chat(String txt) {
            this.txt = txt;
        }
    }
    // END ENUM
    final private Debug debug;
    final private Permission perms;
    private ProtocolManager protocolManager;
    final private Main main;
    final private DefaultConfig config;
    final private Save save;
    private List<String> groups;
    
    public Utils(Main main)
    {
        this.debug = main.debug();
        //DEBUG
        if(debug.utils())
        { debug.message("loading utils"); }
        this.config = new DefaultConfig(main);
        this.perms = getPermissions();
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.main = main;
        this.save = new Save(main);
        this.groups = new ArrayList<>();
        refreshGroups();
        registerCommands();
        registerEvents();
        //DEBUG
        if(debug.utils())
        { debug.message("utils loaded"); }
    }
    
    
    
    
    public DefaultConfig getConfig() { return this.config; }
    
    /**
     * Que Config Files To Save
     *
     * @return save functions
     */
    public Save save() { return this.save; }
    
    public ProtocolManager protoLib() {
        return protocolManager;
    }
    
    /**
     * Use Vault To Find A Compatible Permissions Plugin & Get Groups
     *
     * @return
     */
    private Permission getPermissions()
    {
        try { return Bukkit.getServer().getServicesManager().getRegistration(Permission.class).getProvider(); }
        catch(NullPointerException ignored)
        {
            this.main.getLogger().info("Error Loading Permission Handler, stopping plugin.");
            this.main.getServer().getPluginManager().getPlugin(this.main.getName());
            return null;
        }
    }
    
    /**
     * Add Player Permission
     *
     * @param offlinePlayer to set permission
     * @param permission to set
     */
    public void addPlayerPermission(OfflinePlayer offlinePlayer, String permission)
    {
        this.perms.playerAdd(null, offlinePlayer, permission);
    }
    
    /**
     * Remove Player Permission
     *
     * @param offlinePlayer to set permission
     * @param permission to set
     */
    public void removePlayerPermission(OfflinePlayer offlinePlayer, String permission)
    {
        this.perms.playerRemove(null, offlinePlayer, permission);
    }
    
    /**
     * Refresh Groups Every 5 Minutes
     */
    private void refreshGroups()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                getGroups();
            }
        }.runTaskTimerAsynchronously(this.main, 0L, 6000L);
    }
    private void getGroups()
    {
        List<String> temp = this.groups;
        List<String> newList = Arrays.asList(this.perms.getGroups());
        if(!temp.isEmpty() && !temp.equals(newList)) { this.groups.clear(); }
        if(!temp.equals(newList)) { this.groups = newList; }
    }
    
    /**
     * Get Server Groups
     *
     * @return list of server groups
     */
    public List<String> getServerGroups() { return this.groups; }
    
    /**
     * Get Player Groups
     *
     * @param player groups to acquire
     * @return players groups
     */
    public List<String> getPlayerGroups(Player player) { return Arrays.asList(this.perms.getPlayerGroups(player)); }
    
    /**
     * Check Group Permission Nodes
     *
     * @param group to check
     * @param permission to check
     * @return true?false
     */
    public boolean checkGroupPermission(String group, String permission)
    {
        return perms.groupHas("", group, permission);
    }
    
    /**
     * Check Player Groups Against All Groups, Including Parent Groups
     *
     * @param type of player, @ nullable
     * @return true?false
     */
    public boolean checkPermGroup(Player player, BasicPlayer.Type type) {
        List<String> playerGroups = getPlayerGroups(player);
        if(debug.utils()) { debug.message("player groups = " + playerGroups); }
        List<String> finalPlayerGroups = new ArrayList<>(playerGroups);
        for(String group : playerGroups)
        {
            for(String parentGroup : getServerGroups())
            {
                if(!finalPlayerGroups.contains(parentGroup)) {
                    if(checkGroupPermission(group, "group." + parentGroup)) {
                        finalPlayerGroups.add(parentGroup);
                    }
                }
            }
        }
        List<String> adminGroups = getConfig().getAdminRanks();
        List<String> specialGroups = getConfig().getSpecialRanks();
        if(debug.utils()) { debug.message("final player groups" + Arrays.toString(finalPlayerGroups.toArray())); }
        if(type == BasicPlayer.Type.ADMIN)
        {
            if(debug.utils()) { debug.message("admin groups" + Arrays.toString(adminGroups.toArray())); }
            for(String adminGroup : adminGroups) { return finalPlayerGroups.contains(adminGroup); }
        }
        else if(type == BasicPlayer.Type.SPECIAL_ADMIN)
        {
            if(debug.utils()) { debug.message("special groups" + Arrays.toString(specialGroups.toArray())); }
            for(String specialGroup : specialGroups) { return finalPlayerGroups.contains(specialGroup); }
        }
        return false;
    }
    
    /**
     * Register Commands
     */
    private void registerCommands()
    {
        HashMap<String, CommandExecutor> cmdMap = new HashMap<>();
    
        cmdMap.put("debug", new DebugMode(main));
        cmdMap.put("test", new TestCommand(main));
        cmdMap.put("babysit", new BsCmd(main));
        cmdMap.put("see", new SeeCmd(main));
        cmdMap.put("entity", new EntityCommand(main));
        cmdMap.put("rewards", new RewardsCmd(main));
        cmdMap.put("giveplayer", new GiveCommand(main));
        cmdMap.put("restore", new RestoreCmd(main));
        
        for(String cmd : cmdMap.keySet()) { this.main.getCommand(cmd).setExecutor(cmdMap.get(cmd)); }
    }
    
    /**
     * Register Events
     */
    private void registerEvents()
    {
        List<Listener> events = new ArrayList<>();
        
        events.add(new InventoryClose(main)); events.add(new CommandPreProcess(main));
        events.add(new PlayerJoin(main));     events.add(new InventoryOpen(main));
        events.add(new PlayerQuit(main));     events.add(new InventoryClick(main));
        events.add(new BlockPlace(main));     events.add(new EntityBlockChange(main));
        events.add(new PlayerDropItem(main)); events.add(new PlayerInteractEntity(main));
        
        for(Listener listener : events) { this.main.getServer().getPluginManager().registerEvents(listener, this.main); }
    }
}
