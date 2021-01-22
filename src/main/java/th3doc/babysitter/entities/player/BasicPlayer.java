package th3doc.babysitter.entities.player;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOCase;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.filefilter.PrefixFileFilter;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.npc.EntityUtils;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.UtilsInterface;
import th3doc.babysitter.utils.config.Config;
import th3doc.babysitter.utils.debug.Debug;
import th3doc.babysitter.utils.menu.Menu;
import th3doc.babysitter.utils.menu.MenuUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class BasicPlayer implements Player, PlayerInterface, UtilsInterface, EntityUtils {
    final private Main main;
    final private Player spigotPlayer;
    final private Debug debug;
    final private BasicPlayer player;
    final private Config config;
    final private MenuUtil menuUtil;
    private String lastTimeSet;
    private String playTime;
    private boolean isTeleportWatch;
    private ItemStack[] survivalInv;
    private ItemStack[] survivalEchest;
    private BukkitTask id = null;
    private boolean menuOpen;
    
    
    /**
     Bukkit Overrides
     */
    private boolean allowFlight;
    private boolean isFlying;
    
    /**
     Bukkit Ends
     */
    
    
    public BasicPlayer(Main main, Player spigotPlayer) {
        this.main = main;
        this.debug = main().debug();
        //DEBUG
        if(debug.players()) { debug.message("loading basic player"); }
        this.spigotPlayer = spigotPlayer;
        this.player = this;
        this.config = new Config(main,
                                 Paths.PLAYER_FOLDER.name,
                                 getUniqueId().toString(),
                                 Paths.PLAYER_YML.name);
        this.menuUtil = new MenuUtil(this);
        this.menuOpen = false;
        checkPlayerNameFile();
        checkPlayerListData();
        isFirstJoin();
        isGifting();
        setJoinTime();
        if(!config.isSet(Paths.PLAY_TIME.path)) { config.set(Paths.PLAY_TIME.path, "0.0:0.0:0.0:0.0"); }
        this.playTime = config.getStr(Paths.PLAY_TIME.path);
        if(utils().getConfig().getSleepingIgnored()) { this.spigotPlayer.setSleepingIgnored(true); }
        if(!this.config.isSet(Paths.IS_TELEPORT_WATCH.path)) { setTeleportWatch(false); }
        this.isTeleportWatch = this.config.getBoo(Paths.IS_TELEPORT_WATCH.path);
        isFlyingAllowed();
        loadInventories();
        vanishAdmins();
        loginMsg();
        save();
        //DEBUG
        if(debug.players()) { debug.message("player base loaded"); }
    }
    
    /**
     Constructor Functions
     */
    private void checkPlayerNameFile() {
        File file;
        file = new File(main.getDataFolder(),
                        File.separator + Paths.PLAYER_FOLDER.name +
                        File.separator + getUniqueId().toString() +
                        File.separator + "!." + player.getName() + ".yml");
        if(!file.exists())// check file doesn't already exist
        {
            File directory = new File(main.getDataFolder(),
                                      File.separator + Paths.PLAYER_FOLDER.name +
                                      File.separator + getUniqueId().toString());
            File[] files = directory.listFiles((FileFilter) new PrefixFileFilter("!.", IOCase.INSENSITIVE));
            if(files != null) {
                for(File oldName : files) {
                    if(oldName.delete()) {
                        main.getServer().getLogger().info(oldName.getName() + " File Deleted, " + file + " Added");
                    }
                }
            }
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch(IOException f) { f.printStackTrace(); }
        }
    }
    
    private void checkPlayerListData() {
        // add player to list if missing
        if(!players().playerListHasUUID(getUniqueId().toString())) {
            players().addPlayerToList(getUniqueId(), getName());
            // check if player name matches uuid
        } else if(players().playerListHasUUID(getUniqueId().toString()) &&
                  !players().getCustomPlayerList().contains(getName())) {
            players().removePlayerFromList(getUniqueId());
            players().addPlayerToList(getUniqueId(), getName());
        }
    }
    
    private void isFirstJoin() {
        if(!config.isSet(Paths.FIRST_JOINED_DATE.path)) {
            config.set(Paths.FIRST_JOINED_DATE.path, getCalender(Utils.Calender.DATE));
            config.set(Paths.FIRST_JOINED_TIME.path, getCalender(Utils.Calender.TIME));
            config.set(Paths.FIRST_JOINED_LOCATION.path, getLocale());
            config.set(Paths.GIFTING_PLAYER.path, players().rewards().isGifting());
            //first join implemented below here !!!!!
            ItemStack[] items = rewards().getFirstJoinItems(0, 8);
            if(items.length > 0) {
                getInventory().setItemInMainHand(items[0]);
                if(items.length > 1) {
                    for(int i = 1; i < items.length; i++) {
                        if(items[i] != null) { getInventory().addItem(items[i]); }
                    }
                    setTeleportWatch(true);
                }
            }
        }
    }
    
    private String getCountry(Player p) {//TODO find old country method
        String[] args = p.getLocale().split("_");
        return args[2];
    }
    
    private void isGifting() {
        if(debug.players()) { debug.message("is Gifting?"); }
        if(config.getBoo(Paths.GIFTING_PLAYER.path)) {
            players().rewards().getGiftItems(this);
            config.set(Paths.GIFTING_PLAYER.path, false);
            if(debug.players()) { debug.message("gifting true"); }
        }
        if(debug.players()) { debug.message("gifting false"); }
    }
    
    private void setJoinTime() {
        final String date = getCalender(Utils.Calender.DATE);
        final String time = getCalender(Utils.Calender.TIME);
        final String lastDate = config.getStr(Paths.LAST_JOINED_DATE.path);
        //
        //
        //TODO check date of last login and warn player if its been over 60 days the claims have been wiped from the system, but there points left behind,
        //TODO if its been more then 120 days the points and all other config data has been cleared.
        //
        //
        config.set(Paths.LAST_JOINED_DATE.path, date);
        config.set(Paths.LAST_JOINED_TIME.path, time);
        this.lastTimeSet = time;
    }
    
    public void isFlyingAllowed() {
        //DEBUG
        if(debug.players()) { debug.message("Setting Basic Flying"); }
        if(hasPermission(Utils.Perm._flyBypass.txt) ||
           hasPermission(Utils.Perm._permBypass.txt)) {
            if(!config.isSet(Paths.ALLOW_FLIGHT.path)) { config.set(Paths.ALLOW_FLIGHT.path, false); }
            this.allowFlight = config.getBoo(Paths.ALLOW_FLIGHT.path);
            if(!config.isSet(Paths.IS_FLYING.path)) { config.set(Paths.IS_FLYING.path, false); }
            this.isFlying = config.getBoo(Paths.IS_FLYING.path);
            
        } else {
            this.allowFlight = false;
            this.isFlying = false;
        }
    }
    
    private void loadInventories() {
        /*DEBUG*/if(debug.players()) { debug.message("loading inventories"); }
        if(!config().isSet(Paths.EDITED.path)) {
            config().set(Paths.EDITED.path, false);
        }
        if(!config().isSet(Paths.INV.path)) {
            /*DEBUG*/if(debug.players()) { debug.message("config not set"); }
            survivalInv = getInventory().getContents();
            config.set(Paths.INV.path, serializeItemArray(survivalInv));
            survivalEchest = getEnderChest().getContents();
            config.set(Paths.E_CHEST.path, serializeItemArray(survivalEchest));
        } else {
            /*DEBUG*/if(debug.players()) { debug.message("loading config data"); }
            survivalInv = deSerializeItemArray(config().getStr(Paths.INV.path));
            survivalEchest = deSerializeItemArray(config().getStr(Paths.E_CHEST.path));
        }
        if(config().getBoo(Paths.EDITED.path)) {
            if(!hasPermission(Utils.Perm._invBypass.txt)) {
                /*DEBUG*/if(debug.players()) { debug.message("inventory edited"); }
                getInventory().setContents(survivalInv);
                getEnderChest().setContents(survivalEchest);
            }
            config.set(Paths.EDITED.path, false);
        }
    }
    
    private void vanishAdmins() {
        if(!players().isAdmin(getName())) {
            List<String> vanishedAdmins = players().getVanishedAdmins();
            if(!vanishedAdmins.isEmpty()) {
                for(String vAdmin : vanishedAdmins) {
                    AdminPlayer admin = (AdminPlayer) main.players().getCustomPlayer(vAdmin);
                    hidePlayer(main, admin.getPlayer());
                }
            }
        }
    }
    
    public void loginMsg() {
        if(!players().isReloading()) {
            main.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + Utils.Chat._fakeLogIn.txt);
        }
    }
    /**
     * Constructor End
     */
    
    public MenuUtil getMenuUtil() {
        return menuUtil;
    }
    
    /**
     The Main Class
     
     @return the main class
     */
    public Main main() { return main; }
    
    /**
     Get The Plugin Utilities
     
     @return plugin utilities
     */
    public Utils utils() { return main.utils(); }
    
    /**
     Get All Player Storage
     
     @return online/offline player storage
     */
    public Players players() { return main.players(); }
    
    public Rewards rewards() { return main.players().rewards(); }
    
    /**
     Get The Players Config
     
     @return the players config
     */
    public Config config() { return config; }
    
    /**
     Get The Date Of Which The Player Joined
     
     @return date the player joined
     */
    public String country() { return config.getStr(Paths.FIRST_JOINED_LOCATION.path); }
    
    /**
     Send Player A Message
     
     @param message to be sent
     */
    public void message(String message) { sendMessage(message); }
    
    public void setMenuOpen(boolean b) {
        menuOpen = b;
    }
    
    public boolean isMenuOpen() {
        return menuOpen;
    }
    
    /**
     Get Target Block Player Is Looking At Within A Certain Range
     
     @param maxDistance
     @return
     */
    public Block targetBlock(int maxDistance) { return getTargetBlockExact(maxDistance); }
    
    /**
     Create Custom Inventory Menu
     
     @param menu  is a custom inventory holder
     @param slots to exist
     @param name  of menu
     @return new inventory
     */
    public Inventory createInv(Menu menu, int slots, String name) { return main.getServer().createInventory(menu, slots, name); }
    
    public boolean isTeleportWatch() {
        return isTeleportWatch;
    }
    
    public void setTeleportWatch(boolean b) { isTeleportWatch = b; config.set(Paths.IS_TELEPORT_WATCH.path, b); }
    
    /**
     Set Time Played On The Server, By The Player
     */
    private void setPlayTime() {
        final String[] args = getCalender(Utils.Calender.TIME).split(":");
        final String[] args0 = lastTimeSet.split(":");
        final String[] args1 = playTime.split(":");
        final double[] current = new double[3];
        final double[] last = new double[3];
        final double[] play = new double[4];
        for(int i = 0; i < 4; i++) {
            try {
                if(i < 3) {
                    current[i] = Double.parseDouble(args[i]);
                    last[i] = Double.parseDouble(args0[i]);
                }
                play[i] = Double.parseDouble(args1[i]);
            } catch(NumberFormatException e) { if(debug.players()) { e.printStackTrace(); } }
        }
        double d = play[0];
        double s = addTime(current[2], last[2], play[3], 60);
        double min = z;
        double m = addTime(current[1], last[1], play[2], 60);
        double hr = z;
        double h = addTime(current[0], last[0], play[1], 24);
        m += min;
        h += hr;
        d += z;
        final String format = d + ":" + h + ":" + m + ":" + s;
        //DEBUG
        if(debug.players()) { debug.message("TOTAL AFTER MATH = " + format); }
        this.playTime = format;
        this.config.set(Paths.PLAY_TIME.path, format);
        this.lastTimeSet = getCalender(Utils.Calender.TIME);
    }
    double z = 0;
    private double addTime(double current, double last, double play, int dividend) {
        z = 0;
        double temp = current - last;
        if(temp < 0) { z -= 1; temp = (current + dividend) - last; }
        double temp2 = temp + play;
        if(temp2 >= dividend) { for(int i = 0; temp2 >= dividend; i++) { temp2 -= dividend; z++; } }
        if(z < 0) { z = 0; }
        return temp2;
    }
    
    public void getSurvivalInventory() {
        if(this instanceof AdminPlayer) {
            ((AdminPlayer) this).saveAdminInventory(getInventory().getContents(), getEnderChest().getContents());
        }
        getInventory().setContents(this.survivalInv);
        getEnderChest().setContents(this.survivalEchest);
    }
    
    public void saveSurvivalInventory(ItemStack[] inv, ItemStack[] eChest) {
        this.survivalInv = inv;
        this.survivalEchest = eChest;
        config().set(Paths.INV.path, serializeItemArray(survivalInv));
        config().set(Paths.E_CHEST.path, serializeItemArray(survivalEchest));
    }
    
    /**
     Thread Safe Save
     */
    public void threadSave() {
        /*DEBUG*/
        if(debug.players()) { debug.message("thread safe saving"); }
        id.cancel();
        setPlayTime();
        boolean save = true;
        if(this instanceof AdminPlayer) {
            AdminPlayer p = (AdminPlayer) this;
            if(p.getState(AdminPlayer.State.ADMIN)) {
                save = false;
            }
        }
        if(save) { saveSurvivalInventory(player.getInventory().getContents(), player.getEnderChest().getContents()); }
        utils().save().savePlayer(getUniqueId(), config);
    }
    
    /**
     Que Player Data To Save Based On Config Setting
     */
    public void save() {
        //DEBUG
        if(debug.players()) { debug.message("sending player config"); }
        id = new BukkitRunnable() {
            @Override
            public void run() {
                setPlayTime();
                if(!(player instanceof AdminPlayer)) { saveSurvivalInventory(player.getInventory().getContents(), player.getEnderChest().getContents()); }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        utils().save().savePlayer(getUniqueId(), config);
                        //DEBUG
                        if(debug.players()) { debug.message("config sent"); }
                    }
                }.runTaskAsynchronously(main);
            }
        }.runTaskTimerAsynchronously(main, 0L, (main.utils().getConfig().getConfigSaveTime() - (long) (main.utils().getConfig().getConfigSaveTime() * .1)));
        
    }
    
    /**
     Clear Player Data From Memory When Logging Out
     */
    public void memoryDump() {
        new BukkitRunnable() {
            @Override
            public void run() {
                id.cancel();
                setPlayTime();
                saveSurvivalInventory(player.getInventory().getContents(), player.getEnderChest().getContents());
                utils().save().removePlayer(getUniqueId());
                config.save();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        players().customPlayerLogOut(getUniqueId());
                    }
                }.runTaskLaterAsynchronously(main, 15L);
            }
        }.runTaskAsynchronously(main);
    }
    
    
    /**
     Spigot/Bukkit Modified Methods
     */
    @Override
    public boolean getAllowFlight() {
        return this.allowFlight;
    }
    
    @Override
    public void setAllowFlight(boolean b) {
        this.spigotPlayer.setAllowFlight(b);
        this.allowFlight = b;
        this.config.set(Paths.ALLOW_FLIGHT.path, b);
    }
    
    @Override
    public boolean isFlying() {
        return this.isFlying;
    }
    
    @Override
    public void setFlying(boolean b) {
        this.spigotPlayer.setFlying(b);
        this.isFlying = b;
        this.config.set(Paths.IS_FLYING.path, b);
    }
    
    @Override
    public void hidePlayer(org.bukkit.plugin.Plugin plugin, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                spigotPlayer.hidePlayer(plugin, player);
            }
        }.runTask(main);
        //hide p from player
        //set player hidden from tab-list
    }
    
    @Override
    public void showPlayer(Plugin plugin, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                spigotPlayer.showPlayer(plugin, player);
            }
        }.runTask(main);
        //show p to player
        //set player un-hidden from tab-list
    }
    
    @Override
    public boolean canSee(Player player) {
        return this.spigotPlayer.canSee(player);
        //if above show/hide doesnt work, check canSee and show/hide player on tab-list quickly
        //return this.canSee
    }
    /**
     End Of Modified Methods
     */
    
    
    /**
     Modify Me
     */
    
    @Override
    public int getPortalCooldown() {
        return this.spigotPlayer.getPortalCooldown();
    }
    
    @Override
    public void setPortalCooldown(int i) {
        this.spigotPlayer.setPortalCooldown(i);
    }
    
    @Override
    public void setCustomNameVisible(boolean b) {
        this.spigotPlayer.setCustomNameVisible(b);
    }
    
    @Override
    public boolean isCustomNameVisible() {
        return this.spigotPlayer.isCustomNameVisible();
    }
    
    @Override
    public void setGlowing(boolean b) {
        this.spigotPlayer.setGlowing(b);
    }
    
    @Override
    public boolean isGlowing() {
        return this.spigotPlayer.isGlowing();
    }
    
    @Override
    public void setInvulnerable(boolean b) {
        this.spigotPlayer.setInvulnerable(b);
    }
    
    @Override
    public boolean isInvulnerable() {
        return this.spigotPlayer.isInvulnerable();
    }
    
    @Override
    public boolean isSilent() {
        return this.spigotPlayer.isSilent();
    }
    
    @Override
    public void setSilent(boolean b) {
        this.spigotPlayer.setSilent(b);
    }
    
    @Override
    public BlockFace getFacing() {
        return this.spigotPlayer.getFacing();
    }
    
    @Override
    public List<Entity> getPassengers() {
        return this.spigotPlayer.getPassengers();
    }
    
    @Override
    public boolean addPassenger(Entity entity) {
        return this.spigotPlayer.addPassenger(entity);
    }
    
    @Override
    public boolean removePassenger(Entity entity) {
        return this.spigotPlayer.removePassenger(entity);
    }
    
    @Override
    public boolean isEmpty() {
        return this.spigotPlayer.isEmpty();
    }
    
    @Override
    public boolean eject() {
        return this.spigotPlayer.eject();
    }
    
    @Override
    public boolean isValid() {
        return this.spigotPlayer.isValid();
    }
    
    @Override
    public Location getLocation() {
        return this.spigotPlayer.getLocation();
    }
    
    @Override
    public void setCollidable(boolean b) {
        this.spigotPlayer.setCollidable(b);
    }
    
    @Override
    public boolean isCollidable() {
        return this.spigotPlayer.isCollidable();
    }
    
    @Override
    public Entity getSpectatorTarget() {
        return this.spigotPlayer.getSpectatorTarget();
    }
    
    @Override
    public void setSpectatorTarget(Entity entity) {
        this.spigotPlayer.setSpectatorTarget(entity);
    }
    /**
     End Modify Me
     */
    
    
    /**
     Enums
     */
    public enum Type { ONLINE, OFFLINE, ADMIN, SPECIAL_ADMIN }
    
    public enum InvType { INVENTORY, ENDER_CHEST, SURVIVAL, ADMIN, EDIT_MODE, FIRST_JOIN, RANK_PRIZE }
    
    public enum InvSection {
        HELMET(35, 37),
        CHEST_PLATE(36, 38),
        LEGGINGS(37, 39),
        BOOTS(38, 40),
        LEFT_HAND(39, 41),
        RIGHT_HAND(0, 0),
        HOT_BAR(0, 9),
        CONTENTS(9, 35);
        
        public int start;
        public int finish;
        
        InvSection(int start, int finish) {
            this.start = start;
            this.finish = finish;
        }
    }
    
    public enum Paths {
        PLAYER_YML("Player_Config.yml", "player-config.player"),
        PLAYER_LIST_YML("Player_List.yml", "player-list"),
        PLAYER_LIST_NAME("Player_List_Name", ".name"),
        PLAYER_FOLDER("Player_Data", ""),
        EDITED("Edited", "player-config.player.inventories.edited"),
        INV("Survival_Inv", "player-config.player.inventories.inv"),
        E_CHEST("E_Chest", "player-config.player.inventories.e-chest"),
        LAST_JOINED("Last_Joined", "player-config.player.last-joined"),
        FIRST_JOINED_DATE("First_Joined", "player-config.player.first-joined.date"),
        FIRST_JOINED_TIME("First_Joined_Time", "player-config.player.first-joined.time"),
        FIRST_JOINED_LOCATION("First_Joined_Location", "player-config.player.first-joined.location"),
        FIRST_JOINED_LANG("First_Joined_Language", "player-config.player.first-joined.lang"),
        LAST_JOINED_DATE("Date", "player-config.player.last-joined.date"),
        LAST_JOINED_TIME("Time", "player-config.player.last-joined.time"),
        PLAY_TIME("Play_Time", "player-config.player.play-time"),
        NAME("Name", "player-config.player.name"),
        UUID("UUID", "player-config.player.uuid"),
        ALLOW_FLIGHT("Allow_Flight", "player-config.player.flight.allow-flight"),
        IS_FLYING("Is_Flying", "player-config.player.flight.is-flying"),
        IS_TELEPORT_WATCH("Is_Teleport_Watch", "player-config.player.flight.is-teleport-watch"),
        GIFTING_PLAYER("Gift_Season", "player-config.player.gifts");
        
        public String name;
        public String path;
        
        Paths(String name, String path) {
            this.name = name;
            this.path = path;
        }
    }
    /**
     End Enums
     */
    
    
    /**
     Spigot/Bukkit Methods
     */
    
    
    @Override
    public void setSleepingIgnored(boolean b) {
        this.spigotPlayer.setSleepingIgnored(b);
    }
    
    @Override
    public String getDisplayName() {
        return this.spigotPlayer.getDisplayName();
    }
    
    @Override
    public void setDisplayName(String s) {
        this.spigotPlayer.setDisplayName(s);
    }
    
    @Override
    public String getPlayerListName() {
        return this.spigotPlayer.getPlayerListName();
    }
    
    @Override
    public void setPlayerListName(String s) {
        this.spigotPlayer.setPlayerListName(s);
    }
    
    @Override
    public String getPlayerListHeader() {
        return this.spigotPlayer.getPlayerListHeader();
    }
    
    @Override
    public String getPlayerListFooter() {
        return this.spigotPlayer.getPlayerListFooter();
    }
    
    @Override
    public void setPlayerListHeader(String s) {
        this.spigotPlayer.setPlayerListHeader(s);
    }
    
    @Override
    public void setPlayerListFooter(String s) {
        this.spigotPlayer.setPlayerListFooter(s);
    }
    
    @Override
    public void setPlayerListHeaderFooter(String s, String s1) {
        this.spigotPlayer.setPlayerListHeaderFooter(s, s1);
    }
    
    @Override
    public void setCompassTarget(Location location) {
        this.spigotPlayer.setCompassTarget(location);
    }
    
    @Override
    public Location getCompassTarget() {
        return this.spigotPlayer.getCompassTarget();
    }
    
    @Override
    public InetSocketAddress getAddress() {
        return this.spigotPlayer.getAddress();
    }
    
    @Override
    public void sendRawMessage(String s) {
        this.spigotPlayer.sendRawMessage(s);
    }
    
    @Override
    public void kickPlayer(String s) {
        this.spigotPlayer.kickPlayer(s);
    }
    
    @Override
    public void chat(String s) {
        this.spigotPlayer.chat(s);
    }
    
    @Override
    public boolean performCommand(String s) {
        return this.spigotPlayer.performCommand(s);
    }
    
    @Override
    public boolean isOnGround() {
        return this.spigotPlayer.isOnGround();
    }
    
    @Override
    public boolean isSneaking() {
        return this.spigotPlayer.isSneaking();
    }
    
    @Override
    public void setSneaking(boolean b) {
        this.spigotPlayer.setSneaking(b);
    }
    
    @Override
    public boolean isSprinting() {
        return this.spigotPlayer.isSprinting();
    }
    
    @Override
    public void setSprinting(boolean b) {
        this.spigotPlayer.setSprinting(b);
    }
    
    @Override
    public void saveData() {
        this.spigotPlayer.saveData();
    }
    
    @Override
    public void loadData() {
        this.spigotPlayer.loadData();
    }
    
    @Override
    public boolean isSleepingIgnored() {
        return this.spigotPlayer.isSleepingIgnored();
    }
    
    @Override
    public Location getBedSpawnLocation() {
        return this.spigotPlayer.getBedSpawnLocation();
    }
    
    @Override
    public void setBedSpawnLocation(Location location) {
        this.spigotPlayer.setBedSpawnLocation(location);
    }
    
    @Override
    public void setBedSpawnLocation(Location location, boolean b) {
        this.spigotPlayer.setBedSpawnLocation(location, b);
    }
    
    @Override
    public void playNote(Location location, byte b, byte b1) {
        this.spigotPlayer.playNote(location, b, b1);
    }
    
    @Override
    public void playNote(Location location, Instrument instrument, Note note) {
        this.spigotPlayer.playNote(location, instrument, note);
    }
    
    @Override
    public void playSound(Location location, Sound sound, float v, float v1) {
        this.spigotPlayer.playSound(location, sound, v, v1);
    }
    
    @Override
    public void playSound(Location location, String s, float v, float v1) {
        this.spigotPlayer.playSound(location, s, v, v1);
    }
    
    @Override
    public void playSound(Location location, Sound sound, SoundCategory soundCategory, float v, float v1) {
        this.spigotPlayer.playSound(location, sound, soundCategory, v, v1);
    }
    
    @Override
    public void playSound(Location location, String s, SoundCategory soundCategory, float v, float v1) {
        this.spigotPlayer.playSound(location, s, soundCategory, v, v1);
    }
    
    @Override
    public void stopSound(Sound sound) {
        this.spigotPlayer.stopSound(sound);
    }
    
    @Override
    public void stopSound(String s) {
        this.spigotPlayer.stopSound(s);
    }
    
    @Override
    public void stopSound(Sound sound, SoundCategory soundCategory) {
        this.spigotPlayer.stopSound(sound, soundCategory);
    }
    
    @Override
    public void stopSound(String s, SoundCategory soundCategory) {
        this.spigotPlayer.stopSound(s, soundCategory);
    }
    
    @Override
    public void playEffect(Location location, Effect effect, int i) {
        this.spigotPlayer.playEffect(location, effect, i);
    }
    
    @Override
    public <T> void playEffect(Location location, Effect effect, T t) {
        this.spigotPlayer.playEffect(location, effect, t);
    }
    
    @Override
    public void sendBlockChange(Location location, Material material, byte b) {
        this.spigotPlayer.sendBlockChange(location, material, b);
    }
    
    @Override
    public void sendBlockChange(Location location, BlockData blockData) {
        this.spigotPlayer.sendBlockChange(location, blockData);
    }
    
    @Override
    public boolean sendChunkChange(Location location, int i, int i1, int i2, byte[] bytes) {
        return this.spigotPlayer.sendChunkChange(location, i, i1, i2, bytes);
    }
    
    @Override
    public void sendSignChange(Location location, String[] strings) throws IllegalArgumentException {
        this.spigotPlayer.sendSignChange(location, strings);
    }
    
    @Override
    public void sendSignChange(Location location, String[] strings, DyeColor dyeColor) throws IllegalArgumentException {
        this.spigotPlayer.sendSignChange(location, strings, dyeColor);
    }
    
    @Override
    public void sendMap(MapView mapView) {
        this.spigotPlayer.sendMap(mapView);
    }
    
    @Override
    public void updateInventory() {
        this.spigotPlayer.updateInventory();
    }
    
    @Override
    public void setPlayerTime(long l, boolean b) {
        this.spigotPlayer.setPlayerTime(l, b);
    }
    
    @Override
    public long getPlayerTime() {
        return this.spigotPlayer.getPlayerTime();
    }
    
    @Override
    public long getPlayerTimeOffset() {
        return this.spigotPlayer.getPlayerTimeOffset();
    }
    
    @Override
    public boolean isPlayerTimeRelative() {
        return this.spigotPlayer.isPlayerTimeRelative();
    }
    
    @Override
    public void resetPlayerTime() {
        this.spigotPlayer.resetPlayerTime();
    }
    
    @Override
    public void setPlayerWeather(WeatherType weatherType) {
        this.spigotPlayer.setPlayerWeather(weatherType);
    }
    
    @Override
    public WeatherType getPlayerWeather() {
        return this.spigotPlayer.getPlayerWeather();
    }
    
    @Override
    public void resetPlayerWeather() {
        this.spigotPlayer.resetPlayerWeather();
    }
    
    @Override
    public void giveExp(int i) {
        this.spigotPlayer.giveExp(i);
    }
    
    @Override
    public void giveExpLevels(int i) {
        this.spigotPlayer.giveExpLevels(i);
    }
    
    @Override
    public float getExp() {
        return this.spigotPlayer.getExp();
    }
    
    @Override
    public void setExp(float v) {
        this.spigotPlayer.setExp(v);
    }
    
    @Override
    public int getLevel() {
        return this.spigotPlayer.getLevel();
    }
    
    @Override
    public void setLevel(int i) {
        this.spigotPlayer.setLevel(i);
    }
    
    @Override
    public int getTotalExperience() {
        return this.spigotPlayer.getTotalExperience();
    }
    
    @Override
    public void setTotalExperience(int i) {
        this.spigotPlayer.setTotalExperience(i);
    }
    
    @Override
    public void sendExperienceChange(float v) {
        this.spigotPlayer.sendExperienceChange(v);
    }
    
    @Override
    public void sendExperienceChange(float v, int i) {
        this.spigotPlayer.sendExperienceChange(v, i);
    }
    
    @Override
    public float getExhaustion() {
        return this.spigotPlayer.getExhaustion();
    }
    
    @Override
    public void setExhaustion(float v) {
        this.spigotPlayer.setExhaustion(v);
    }
    
    @Override
    public float getSaturation() {
        return this.spigotPlayer.getSaturation();
    }
    
    @Override
    public void setSaturation(float v) {
        this.spigotPlayer.setSaturation(v);
    }
    
    @Override
    public int getFoodLevel() {
        return this.spigotPlayer.getFoodLevel();
    }
    
    @Override
    public void setFoodLevel(int i) {
        this.spigotPlayer.setFoodLevel(i);
    }
    
    @Override
    public void hidePlayer(Player player) {
        this.spigotPlayer.hidePlayer(spigotPlayer);
    }
    
    @Override
    public void showPlayer(Player player) {
        this.spigotPlayer.showPlayer(player);
    }
    
    @Override
    public void setFlySpeed(float v) throws IllegalArgumentException {
        this.spigotPlayer.setFlySpeed(v);
    }
    
    @Override
    public void setWalkSpeed(float v) throws IllegalArgumentException {
        this.spigotPlayer.setWalkSpeed(v);
    }
    
    @Override
    public float getFlySpeed() {
        return this.spigotPlayer.getFlySpeed();
    }
    
    @Override
    public float getWalkSpeed() {
        return this.spigotPlayer.getWalkSpeed();
    }
    
    @Override
    public void setTexturePack(String s) {
        this.spigotPlayer.setTexturePack(s);
    }
    
    @Override
    public void setResourcePack(String s) {
        this.spigotPlayer.setResourcePack(s);
    }
    
    @Override
    public void setResourcePack(String s, byte[] bytes) {
        this.spigotPlayer.setResourcePack(s, bytes);
    }
    
    @Override
    public Scoreboard getScoreboard() {
        return this.spigotPlayer.getScoreboard();
    }
    
    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        this.spigotPlayer.setScoreboard(scoreboard);
    }
    
    @Override
    public boolean isHealthScaled() {
        return this.spigotPlayer.isHealthScaled();
    }
    
    @Override
    public void setHealthScaled(boolean b) {
        this.spigotPlayer.setHealthScaled(b);
    }
    
    @Override
    public void setHealthScale(double v) throws IllegalArgumentException {
        this.spigotPlayer.setHealthScale(v);
    }
    
    @Override
    public double getHealthScale() {
        return this.spigotPlayer.getHealthScale();
    }
    
    @Override
    public void sendTitle(String s, String s1) {
        this.spigotPlayer.sendTitle(s, s1);
    }
    
    @Override
    public void sendTitle(String s, String s1, int i, int i1, int i2) {
        this.spigotPlayer.sendTitle(s, s1, i, i1, i2);
    }
    
    @Override
    public void resetTitle() {
        this.spigotPlayer.resetTitle();
    }
    
    @Override
    public void spawnParticle(Particle particle, Location location, int i) {
        this.spigotPlayer.spawnParticle(particle, location, i);
    }
    
    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i) {
        this.spigotPlayer.spawnParticle(particle, v, v1, v2, i);
    }
    
    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, T t) {
        this.spigotPlayer.spawnParticle(particle, location, i, t);
    }
    
    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, T t) {
        this.spigotPlayer.spawnParticle(particle, v, v1, v2, i, t);
    }
    
    @Override
    public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2) {
        this.spigotPlayer.spawnParticle(particle, location, i, v, v1, v2);
    }
    
    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5) {
        this.spigotPlayer.spawnParticle(particle, v, v1, v2, i, v3, v4, v5);
    }
    
    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, T t) {
        this.spigotPlayer.spawnParticle(particle, location, i, v, v1, v2, t);
    }
    
    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, T t) {
        this.spigotPlayer.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, t);
    }
    
    @Override
    public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3) {
        this.spigotPlayer.spawnParticle(particle, location, i, v, v1, v2, v3);
    }
    
    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6) {
        this.spigotPlayer.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, v6);
    }
    
    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3, T t) {
        this.spigotPlayer.spawnParticle(particle, location, i, v, v1, v2, v3, t);
    }
    
    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, T t) {
        this.spigotPlayer.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, v6, t);
    }
    
    @Override
    public AdvancementProgress getAdvancementProgress(Advancement advancement) {
        return this.spigotPlayer.getAdvancementProgress(advancement);
    }
    
    @Override
    public int getClientViewDistance() {
        return this.spigotPlayer.getClientViewDistance();
    }
    
    @Override
    public String getLocale() {
        return this.spigotPlayer.getLocale();
    }
    
    @Override
    public void updateCommands() {
        this.spigotPlayer.updateCommands();
    }
    
    @Override
    public void openBook(ItemStack itemStack) {
        this.spigotPlayer.openBook(itemStack);
    }
    
    @Override
    public Player.Spigot spigot() {
        return this.spigotPlayer.spigot();
    }
    
    @Override
    public boolean isOnline() {
        return this.spigotPlayer.isOnline();
    }
    
    @Override
    public boolean isBanned() {
        return this.spigotPlayer.isBanned();
    }
    
    @Override
    public boolean isWhitelisted() {
        return this.spigotPlayer.isWhitelisted();
    }
    
    @Override
    public void setWhitelisted(boolean b) {
        this.spigotPlayer.setWhitelisted(b);
    }
    
    @Override
    public Player getPlayer() {
        return this.spigotPlayer.getPlayer();
    }
    
    @Override
    public long getFirstPlayed() {
        return this.spigotPlayer.getFirstPlayed();
    }
    
    @Override
    public long getLastPlayed() {
        return this.spigotPlayer.getLastPlayed();
    }
    
    @Override
    public boolean hasPlayedBefore() {
        return this.spigotPlayer.hasPlayedBefore();
    }
    
    @Override
    public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {
        this.spigotPlayer.incrementStatistic(statistic);
    }
    
    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
        this.spigotPlayer.decrementStatistic(statistic);
    }
    
    @Override
    public void incrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        this.spigotPlayer.incrementStatistic(statistic, i);
    }
    
    @Override
    public void decrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        this.spigotPlayer.decrementStatistic(statistic, i);
    }
    
    @Override
    public void setStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        this.spigotPlayer.setStatistic(statistic, i);
    }
    
    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return this.spigotPlayer.getStatistic(statistic);
    }
    
    @Override
    public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        this.spigotPlayer.incrementStatistic(statistic, material);
    }
    
    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        this.spigotPlayer.decrementStatistic(statistic, material);
    }
    
    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return this.spigotPlayer.getStatistic(statistic, material);
    }
    
    @Override
    public void incrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        this.spigotPlayer.incrementStatistic(statistic, material, i);
    }
    
    @Override
    public void decrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        this.spigotPlayer.decrementStatistic(statistic, material, i);
    }
    
    @Override
    public void setStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        this.spigotPlayer.setStatistic(statistic, material, i);
    }
    
    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        this.spigotPlayer.incrementStatistic(statistic, entityType);
    }
    
    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        this.spigotPlayer.decrementStatistic(statistic, entityType);
    }
    
    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return this.spigotPlayer.getStatistic(statistic, entityType);
    }
    
    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int i) throws IllegalArgumentException {
        this.spigotPlayer.incrementStatistic(statistic, entityType, i);
    }
    
    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int i) {
        this.spigotPlayer.decrementStatistic(statistic, entityType, i);
    }
    
    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int i) {
        this.spigotPlayer.setStatistic(statistic, entityType, i);
    }
    
    @Override
    public Map<String, Object> serialize() {
        return spigotPlayer.serialize();
    }
    
    @Override
    public boolean isConversing() {
        return spigotPlayer.isConversing();
    }
    
    @Override
    public void acceptConversationInput(String s) {
        this.spigotPlayer.acceptConversationInput(s);
    }
    
    @Override
    public boolean beginConversation(Conversation conversation) {
        return this.spigotPlayer.beginConversation(conversation);
    }
    
    @Override
    public void abandonConversation(Conversation conversation) {
        this.spigotPlayer.abandonConversation(conversation);
    }
    
    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent) {
        this.spigotPlayer.abandonConversation(conversation, conversationAbandonedEvent);
    }
    
    @Override
    public void sendRawMessage(UUID uuid, String s) {
        this.spigotPlayer.sendRawMessage(uuid, s);
    }
    
    @Override
    public String getName() {
        return this.spigotPlayer.getName();
    }
    
    @Override
    public PlayerInventory getInventory() {
        return this.spigotPlayer.getInventory();
    }
    
    @Override
    public Inventory getEnderChest() {
        return this.spigotPlayer.getEnderChest();
    }
    
    @Override
    public MainHand getMainHand() {
        return this.spigotPlayer.getMainHand();
    }
    
    @Override
    public boolean setWindowProperty(InventoryView.Property property, int i) {
        return this.spigotPlayer.setWindowProperty(property, i);
    }
    
    @Override
    public InventoryView getOpenInventory() {
        return this.spigotPlayer.getOpenInventory();
    }
    
    @Override
    public InventoryView openInventory(Inventory inventory) {
        return this.spigotPlayer.openInventory(inventory);
    }
    
    @Override
    public InventoryView openWorkbench(Location location, boolean b) {
        return this.spigotPlayer.openWorkbench(location, b);
    }
    
    @Override
    public InventoryView openEnchanting(Location location, boolean b) {
        return this.spigotPlayer.openEnchanting(location, b);
    }
    
    @Override
    public void openInventory(InventoryView inventoryView) {
        this.spigotPlayer.openInventory(inventoryView);
    }
    
    @Override
    public InventoryView openMerchant(Villager villager, boolean b) {
        return this.spigotPlayer.openMerchant(villager, b);
    }
    
    @Override
    public InventoryView openMerchant(Merchant merchant, boolean b) {
        return this.spigotPlayer.openMerchant(merchant, b);
    }
    
    @Override
    public void closeInventory() {
        this.spigotPlayer.closeInventory();
    }
    
    @Override
    public ItemStack getItemInHand() {
        return this.spigotPlayer.getItemInHand();
    }
    
    @Override
    public void setItemInHand(ItemStack itemStack) {
        this.spigotPlayer.setItemInHand(itemStack);
    }
    
    @Override
    public ItemStack getItemOnCursor() {
        return this.spigotPlayer.getItemOnCursor();
    }
    
    @Override
    public void setItemOnCursor(ItemStack itemStack) {
        this.spigotPlayer.setItemOnCursor(itemStack);
    }
    
    @Override
    public boolean hasCooldown(Material material) {
        return this.spigotPlayer.hasCooldown(material);
    }
    
    @Override
    public int getCooldown(Material material) {
        return this.spigotPlayer.getCooldown(material);
    }
    
    @Override
    public void setCooldown(Material material, int i) {
        this.spigotPlayer.setCooldown(material, i);
    }
    
    @Override
    public int getSleepTicks() {
        return this.spigotPlayer.getSleepTicks();
    }
    
    @Override
    public boolean sleep(Location location, boolean b) {
        return this.spigotPlayer.sleep(location, b);
    }
    
    @Override
    public void wakeup(boolean b) {
        this.spigotPlayer.wakeup(b);
    }
    
    @Override
    public Location getBedLocation() {
        return this.spigotPlayer.getBedLocation();
    }
    
    @Override
    public GameMode getGameMode() {
        return this.spigotPlayer.getGameMode();
    }
    
    @Override
    public void setGameMode(GameMode gameMode) {
        this.spigotPlayer.setGameMode(gameMode);
    }
    
    @Override
    public boolean isBlocking() {
        return this.spigotPlayer.isBlocking();
    }
    
    @Override
    public boolean isHandRaised() {
        return this.spigotPlayer.isHandRaised();
    }
    
    @Override
    public int getExpToLevel() {
        return this.spigotPlayer.getExpToLevel();
    }
    
    @Override
    public float getAttackCooldown() {
        return this.spigotPlayer.getAttackCooldown();
    }
    
    @Override
    public boolean discoverRecipe(NamespacedKey namespacedKey) {
        return this.spigotPlayer.discoverRecipe(namespacedKey);
    }
    
    @Override
    public int discoverRecipes(Collection<NamespacedKey> collection) {
        return this.spigotPlayer.discoverRecipes(collection);
    }
    
    @Override
    public boolean undiscoverRecipe(NamespacedKey namespacedKey) {
        return this.spigotPlayer.undiscoverRecipe(namespacedKey);
    }
    
    @Override
    public int undiscoverRecipes(Collection<NamespacedKey> collection) {
        return this.spigotPlayer.undiscoverRecipes(collection);
    }
    
    @Override
    public boolean hasDiscoveredRecipe(NamespacedKey namespacedKey) {
        return this.spigotPlayer.hasDiscoveredRecipe(namespacedKey);
    }
    
    @Override
    public Set<NamespacedKey> getDiscoveredRecipes() {
        return this.spigotPlayer.getDiscoveredRecipes();
    }
    
    @Override
    public Entity getShoulderEntityLeft() {
        return this.spigotPlayer.getShoulderEntityLeft();
    }
    
    @Override
    public void setShoulderEntityLeft(Entity entity) {
        this.spigotPlayer.setShoulderEntityLeft(entity);
    }
    
    @Override
    public Entity getShoulderEntityRight() {
        return this.spigotPlayer.getShoulderEntityRight();
    }
    
    @Override
    public void setShoulderEntityRight(Entity entity) {
        this.spigotPlayer.setShoulderEntityRight(entity);
    }
    
    @Override
    public boolean dropItem(boolean b) {
        return this.spigotPlayer.dropItem(b);
    }
    
    @Override
    public double getEyeHeight() {
        return this.spigotPlayer.getEyeHeight();
    }
    
    @Override
    public double getEyeHeight(boolean b) {
        return this.spigotPlayer.getEyeHeight(b);
    }
    
    @Override
    public Location getEyeLocation() {
        return this.spigotPlayer.getEyeLocation();
    }
    
    @Override
    public List<Block> getLineOfSight(Set<Material> set, int i) {
        return this.spigotPlayer.getLineOfSight(set, i);
    }
    
    @Override
    public Block getTargetBlock(Set<Material> set, int i) {
        return this.spigotPlayer.getTargetBlock(set, i);
    }
    
    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> set, int i) {
        return this.spigotPlayer.getLastTwoTargetBlocks(set, i);
    }
    
    @Override
    public Block getTargetBlockExact(int i) {
        return this.spigotPlayer.getTargetBlockExact(i);
    }
    
    @Override
    public Block getTargetBlockExact(int i, FluidCollisionMode fluidCollisionMode) {
        return this.spigotPlayer.getTargetBlockExact(i, fluidCollisionMode);
    }
    
    @Override
    public RayTraceResult rayTraceBlocks(double v) {
        return this.spigotPlayer.rayTraceBlocks(v);
    }
    
    @Override
    public RayTraceResult rayTraceBlocks(double v, FluidCollisionMode fluidCollisionMode) {
        return this.spigotPlayer.rayTraceBlocks(v, fluidCollisionMode);
    }
    
    @Override
    public int getRemainingAir() {
        return this.spigotPlayer.getRemainingAir();
    }
    
    @Override
    public void setRemainingAir(int i) {
        this.spigotPlayer.setRemainingAir(i);
    }
    
    @Override
    public int getMaximumAir() {
        return this.spigotPlayer.getMaximumAir();
    }
    
    @Override
    public void setMaximumAir(int i) {
        this.spigotPlayer.setMaximumAir(i);
    }
    
    @Override
    public int getArrowCooldown() {
        return this.spigotPlayer.getArrowCooldown();
    }
    
    @Override
    public void setArrowCooldown(int i) {
        this.spigotPlayer.setArrowCooldown(i);
    }
    
    @Override
    public int getArrowsInBody() {
        return this.spigotPlayer.getArrowsInBody();
    }
    
    @Override
    public void setArrowsInBody(int i) {
        this.spigotPlayer.setArrowsInBody(i);
    }
    
    @Override
    public int getMaximumNoDamageTicks() {
        return this.spigotPlayer.getMaximumNoDamageTicks();
    }
    
    @Override
    public void setMaximumNoDamageTicks(int i) {
        this.spigotPlayer.setMaximumNoDamageTicks(i);
    }
    
    @Override
    public double getLastDamage() {
        return this.spigotPlayer.getLastDamage();
    }
    
    @Override
    public void setLastDamage(double v) {
        this.spigotPlayer.setLastDamage(v);
    }
    
    @Override
    public int getNoDamageTicks() {
        return this.spigotPlayer.getNoDamageTicks();
    }
    
    @Override
    public void setNoDamageTicks(int i) {
        this.spigotPlayer.setNoDamageTicks(i);
    }
    
    @Override
    public Player getKiller() {
        return this.spigotPlayer.getKiller();
    }
    
    @Override
    public boolean addPotionEffect(PotionEffect potionEffect) {
        return this.spigotPlayer.addPotionEffect(potionEffect);
    }
    
    @Override
    public boolean addPotionEffect(PotionEffect potionEffect, boolean b) {
        return this.spigotPlayer.addPotionEffect(potionEffect, b);
    }
    
    @Override
    public boolean addPotionEffects(Collection<PotionEffect> collection) {
        return this.spigotPlayer.addPotionEffects(collection);
    }
    
    @Override
    public boolean hasPotionEffect(PotionEffectType potionEffectType) {
        return this.spigotPlayer.hasPotionEffect(potionEffectType);
    }
    
    @Override
    public PotionEffect getPotionEffect(PotionEffectType potionEffectType) {
        return this.spigotPlayer.getPotionEffect(potionEffectType);
    }
    
    @Override
    public void removePotionEffect(PotionEffectType potionEffectType) {
        this.spigotPlayer.removePotionEffect(potionEffectType);
    }
    
    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return this.spigotPlayer.getActivePotionEffects();
    }
    
    @Override
    public boolean hasLineOfSight(Entity entity) {
        return this.spigotPlayer.hasLineOfSight(entity);
    }
    
    @Override
    public boolean getRemoveWhenFarAway() {
        return this.spigotPlayer.getRemoveWhenFarAway();
    }
    
    @Override
    public void setRemoveWhenFarAway(boolean b) {
        this.spigotPlayer.setRemoveWhenFarAway(b);
    }
    
    @Override
    public EntityEquipment getEquipment() {
        return this.spigotPlayer.getEquipment();
    }
    
    @Override
    public void setCanPickupItems(boolean b) {
        this.spigotPlayer.setCanPickupItems(b);
    }
    
    @Override
    public boolean getCanPickupItems() {
        return this.spigotPlayer.getCanPickupItems();
    }
    
    @Override
    public boolean isLeashed() {
        return this.spigotPlayer.isLeashed();
    }
    
    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        return this.spigotPlayer.getLeashHolder();
    }
    
    @Override
    public boolean setLeashHolder(Entity entity) {
        return this.spigotPlayer.setLeashHolder(entity);
    }
    
    @Override
    public boolean isGliding() {
        return this.spigotPlayer.isGliding();
    }
    
    @Override
    public void setGliding(boolean b) {
        this.spigotPlayer.setGliding(b);
    }
    
    @Override
    public boolean isSwimming() {
        return this.spigotPlayer.isSwimming();
    }
    
    @Override
    public void setSwimming(boolean b) {
        this.spigotPlayer.setSwimming(b);
    }
    
    @Override
    public boolean isRiptiding() {
        return this.spigotPlayer.isRiptiding();
    }
    
    @Override
    public boolean isSleeping() {
        return this.spigotPlayer.isSleeping();
    }
    
    @Override
    public void setAI(boolean b) {
        this.spigotPlayer.setAI(b);
    }
    
    @Override
    public boolean hasAI() {
        return this.spigotPlayer.hasAI();
    }
    
    @Override
    public void attack(Entity entity) {
        this.spigotPlayer.attack(entity);
    }
    
    @Override
    public void swingMainHand() {
        this.spigotPlayer.swingMainHand();
    }
    
    @Override
    public void swingOffHand() {
        this.spigotPlayer.swingOffHand();
    }
    
    @Override
    public Set<UUID> getCollidableExemptions() {
        return this.spigotPlayer.getCollidableExemptions();
    }
    
    @Override
    public <T> T getMemory(MemoryKey<T> memoryKey) {
        return this.spigotPlayer.getMemory(memoryKey);
    }
    
    @Override
    public <T> void setMemory(MemoryKey<T> memoryKey, T t) {
        this.spigotPlayer.setMemory(memoryKey, t);
    }
    
    @Override
    public EntityCategory getCategory() {
        return this.spigotPlayer.getCategory();
    }
    
    @Override
    public void setInvisible(boolean b) {
        this.spigotPlayer.setInvulnerable(b);
    }
    
    @Override
    public boolean isInvisible() {
        return this.spigotPlayer.isInvisible();
    }
    
    @Override
    public AttributeInstance getAttribute(Attribute attribute) {
        return this.spigotPlayer.getAttribute(attribute);
    }
    
    @Override
    public void damage(double v) {
        this.spigotPlayer.damage(v);
    }
    
    @Override
    public void damage(double v, Entity entity) {
        this.spigotPlayer.damage(v, entity);
    }
    
    @Override
    public double getHealth() {
        return this.spigotPlayer.getHealth();
    }
    
    @Override
    public void setHealth(double v) {
        this.spigotPlayer.setHealth(v);
    }
    
    @Override
    public double getAbsorptionAmount() {
        return this.spigotPlayer.getAbsorptionAmount();
    }
    
    @Override
    public void setAbsorptionAmount(double v) {
        this.spigotPlayer.setAbsorptionAmount(v);
    }
    
    @Override
    public double getMaxHealth() {
        return this.spigotPlayer.getMaxHealth();
    }
    
    @Override
    public void setMaxHealth(double v) {
        this.spigotPlayer.setMaxHealth(v);
    }
    
    @Override
    public void resetMaxHealth() {
        this.spigotPlayer.resetMaxHealth();
    }
    
    @Override
    public Location getLocation(Location location) {
        return this.spigotPlayer.getLocation(location);
    }
    
    @Override
    public void setVelocity(org.bukkit.util.Vector vector) {
        this.spigotPlayer.setVelocity(vector);
    }
    
    @Override
    public org.bukkit.util.Vector getVelocity() {
        return this.spigotPlayer.getVelocity();
    }
    
    @Override
    public double getHeight() {
        return this.spigotPlayer.getHeight();
    }
    
    @Override
    public double getWidth() {
        return this.spigotPlayer.getWidth();
    }
    
    @Override
    public BoundingBox getBoundingBox() {
        return this.spigotPlayer.getBoundingBox();
    }
    
    @Override
    public World getWorld() {
        return this.spigotPlayer.getWorld();
    }
    
    @Override
    public void setRotation(float v, float v1) {
        this.spigotPlayer.setRotation(v, v1);
    }
    
    @Override
    public boolean teleport(Location location) {
        return this.spigotPlayer.teleport(location);
    }
    
    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        return this.spigotPlayer.teleport(location, teleportCause);
    }
    
    @Override
    public boolean teleport(Entity entity) {
        return this.spigotPlayer.teleport(entity);
    }
    
    @Override
    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause) {
        return this.spigotPlayer.teleport(entity, teleportCause);
    }
    
    @Override
    public List<Entity> getNearbyEntities(double v, double v1, double v2) {
        return this.spigotPlayer.getNearbyEntities(v, v1, v2);
    }
    
    @Override
    public int getEntityId() {
        return this.spigotPlayer.getEntityId();
    }
    
    @Override
    public int getFireTicks() {
        return this.spigotPlayer.getFireTicks();
    }
    
    @Override
    public int getMaxFireTicks() {
        return this.spigotPlayer.getMaxFireTicks();
    }
    
    @Override
    public void setFireTicks(int i) {
        this.spigotPlayer.setFireTicks(i);
    }
    
    @Override
    public void remove() {
        this.spigotPlayer.remove();
    }
    
    @Override
    public boolean isDead() {
        return this.spigotPlayer.isDead();
    }
    
    @Override
    public Server getServer() {
        return this.spigotPlayer.getServer();
    }
    
    @Override
    public boolean isPersistent() {
        return this.spigotPlayer.isPersistent();
    }
    
    @Override
    public void setPersistent(boolean b) {
        this.spigotPlayer.setPersistent(b);
    }
    
    @Override
    public Entity getPassenger() {
        return this.spigotPlayer.getPassenger();
    }
    
    @Override
    public boolean setPassenger(Entity entity) {
        return this.spigotPlayer.setPassenger(entity);
    }
    
    @Override
    public float getFallDistance() {
        return this.spigotPlayer.getFallDistance();
    }
    
    @Override
    public void setFallDistance(float v) {
        this.spigotPlayer.setFallDistance(v);
    }
    
    @Override
    public void setLastDamageCause(EntityDamageEvent entityDamageEvent) {
        this.spigotPlayer.setLastDamageCause(entityDamageEvent);
    }
    
    @Override
    public EntityDamageEvent getLastDamageCause() {
        return this.spigotPlayer.getLastDamageCause();
    }
    
    @Override
    public UUID getUniqueId() {
        return this.spigotPlayer.getUniqueId();
    }
    
    @Override
    public int getTicksLived() {
        return this.spigotPlayer.getTicksLived();
    }
    
    @Override
    public void setTicksLived(int i) {
        this.spigotPlayer.setTicksLived(i);
    }
    
    @Override
    public void playEffect(EntityEffect entityEffect) {
        this.spigotPlayer.playEffect(entityEffect);
    }
    
    @Override
    public EntityType getType() {
        return this.spigotPlayer.getType();
    }
    
    @Override
    public boolean isInsideVehicle() {
        return this.spigotPlayer.isInsideVehicle();
    }
    
    @Override
    public boolean leaveVehicle() {
        return this.spigotPlayer.leaveVehicle();
    }
    
    @Override
    public Entity getVehicle() {
        return this.spigotPlayer.getVehicle();
    }
    
    @Override
    public boolean hasGravity() {
        return this.spigotPlayer.hasGravity();
    }
    
    @Override
    public void setGravity(boolean b) {
        this.spigotPlayer.setGravity(b);
    }
    
    @Override
    public Set<String> getScoreboardTags() {
        return this.spigotPlayer.getScoreboardTags();
    }
    
    @Override
    public boolean addScoreboardTag(String s) {
        return this.spigotPlayer.addScoreboardTag(s);
    }
    
    @Override
    public boolean removeScoreboardTag(String s) {
        return this.spigotPlayer.removeScoreboardTag(s);
    }
    
    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return this.spigotPlayer.getPistonMoveReaction();
    }
    
    @Override
    public Pose getPose() {
        return this.spigotPlayer.getPose();
    }
    
    @Override
    public String getCustomName() {
        return this.spigotPlayer.getCustomName();
    }
    
    @Override
    public void setCustomName(String s) {
        this.spigotPlayer.setCustomName(s);
    }
    
    @Override
    public void sendMessage(String s) {
        this.spigotPlayer.sendMessage(s);
    }
    
    @Override
    public void sendMessage(String[] strings) {
        this.spigotPlayer.sendMessage(strings);
    }
    
    @Override
    public void sendMessage(UUID uuid, String s) {
        this.spigotPlayer.sendMessage(uuid, s);
    }
    
    @Override
    public void sendMessage(UUID uuid, String[] strings) {
        this.spigotPlayer.sendMessage(uuid, strings);
    }
    
    @Override
    public void setMetadata(String s, MetadataValue metadataValue) {
        this.spigotPlayer.setMetadata(s, metadataValue);
    }
    
    @Override
    public List<MetadataValue> getMetadata(String s) {
        return this.spigotPlayer.getMetadata(s);
    }
    
    @Override
    public boolean hasMetadata(String s) {
        return this.spigotPlayer.hasMetadata(s);
    }
    
    @Override
    public void removeMetadata(String s, Plugin plugin) {
        this.spigotPlayer.removeMetadata(s, plugin);
    }
    
    @Override
    public boolean isPermissionSet(String s) {
        return this.spigotPlayer.isPermissionSet(s);
    }
    
    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.spigotPlayer.isPermissionSet(permission);
    }
    
    @Override
    public boolean hasPermission(String s) {
        return this.spigotPlayer.hasPermission(s);
    }
    
    @Override
    public boolean hasPermission(Permission permission) {
        return this.spigotPlayer.hasPermission(permission);
    }
    
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return this.spigotPlayer.addAttachment(plugin, s, b);
    }
    
    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.spigotPlayer.addAttachment(plugin);
    }
    
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return this.spigotPlayer.addAttachment(plugin, s, b, i);
    }
    
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return this.spigotPlayer.addAttachment(plugin, i);
    }
    
    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        this.spigotPlayer.removeAttachment(permissionAttachment);
    }
    
    @Override
    public void recalculatePermissions() {
        this.spigotPlayer.recalculatePermissions();
    }
    
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.spigotPlayer.getEffectivePermissions();
    }
    
    @Override
    public boolean isOp() {
        return this.spigotPlayer.isOp();
    }
    
    @Override
    public void setOp(boolean b) {
        this.spigotPlayer.setOp(b);
    }
    
    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return this.spigotPlayer.getPersistentDataContainer();
    }
    
    @Override
    public void sendPluginMessage(Plugin plugin, String s, byte[] bytes) {
        this.spigotPlayer.sendPluginMessage(plugin, s, bytes);
    }
    
    @Override
    public Set<String> getListeningPluginChannels() {
        return this.spigotPlayer.getListeningPluginChannels();
    }
    
    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass) {
        return this.spigotPlayer.launchProjectile(aClass);
    }
    
    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector) {
        return this.spigotPlayer.launchProjectile(aClass, vector);
    }
}