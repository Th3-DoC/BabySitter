package th3doc.babysitter.entities.player;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.Main;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.config.Config;
import th3doc.babysitter.utils.debug.Debug;

import java.util.*;

public class Players {
    final private HashMap<UUID, BasicPlayer> players;
    final private HashMap<UUID, String> playerList;
    final private List<String> vanishedAdmins;
    final private List<String> adminList;
    private boolean isReloading;
    final private Rewards rewards;
    final private Config config;
    final private Debug debug;
    final private Main main;
    
    public Players(Main main) {
        this.players = new HashMap<>();
        this.playerList = new HashMap<>();
        this.vanishedAdmins = new ArrayList<>();
        this.adminList = new ArrayList<>();
        this.rewards = new Rewards(main);
        this.config = new Config(main,
                                 BasicPlayer.Paths.PLAYER_FOLDER.name,
                                 "",
                                 BasicPlayer.Paths.PLAYER_LIST_YML.name);
        this.debug = main.debug();
        this.main = main;
        loadPlayerList();
    }
    
    /**
     Load Custom Player List
     */
    private void loadPlayerList() {
        if(!this.config.isSet(BasicPlayer.Paths.PLAYER_LIST_YML.path)) {
            this.config.set(BasicPlayer.Paths.PLAYER_LIST_YML.path, null);
            this.config.save();
        } else {
            for(String key : this.config.getConfigSection(BasicPlayer.Paths.PLAYER_LIST_YML.path).getKeys(false)) {
                this.playerList.put(UUID.fromString(key), this.config.getStr(BasicPlayer.Paths.PLAYER_LIST_YML.path + "." + key + BasicPlayer.Paths.PLAYER_LIST_NAME.path));
            }
    }
    }
    
    public boolean isReloading() {
        return isReloading;
    }
    
    public Rewards rewards() { return rewards; }
    
    public boolean isAdmin(String name) { return getAdmins().contains(name); }
    
    public List<String> getAdmins() { return this.adminList; }
    
    public void addToAdminList(String name) { this.adminList.add(name); }
    
    public void removeAdminFromList(String name) { this.adminList.remove(name); }
    
    public List<String> getVanishedAdmins() { return this.vanishedAdmins; }
    
    public void addToVanishList(String name) { this.vanishedAdmins.add(name); }
    
    public void removeVanishedAdminFromList(String name) { this.vanishedAdmins.remove(name); }
    
    public void removePlayerFromList(UUID uuid) { this.players.remove(uuid); }
    
    public List<String> getCustomOfflinePlayerList() {
        List<String> list = new ArrayList<>(Arrays.asList(this.playerList.values().toArray(new String[0])));
        list.removeAll(main.players().getCustomOnlinePlayers());
        debug.message(Arrays.toString(list.toArray()));
        return list;
    }
    
    public UUID getCustomPlayerUUID(String name) {
        for(UUID uuid : this.playerList.keySet()) {
            if(this.playerList.get(uuid).equalsIgnoreCase(name)) {
                if(debug.players()) { debug.message("UUID = " + uuid); }
                return uuid;
            }
        }
        return null;
    }
    
    public String getCustomPlayerName(UUID uuid) { return this.playerList.getOrDefault(uuid, null); }
    
    public Config getOfflinePlayerConfig(String uuid) // run async as needed
    {
        return new Config(main,
                          BasicPlayer.Paths.PLAYER_FOLDER.name,
                          uuid,
                          BasicPlayer.Paths.PLAYER_YML.name);
    }
    
    /**
     See If A Player Is Online
     
     @param name of player
     @return player is online true?false
     */
    public boolean isCustomPlayerOnline(String name) { return getCustomOnlinePlayers().contains(name); }
    
    /**
     Get All Online Players.
     
     @return babysitter players
     */
    public List<String> getCustomOnlinePlayers() {
        List<String> names = new ArrayList<>();
        for(BasicPlayer player : this.players.values()) { names.add(player.getName()); }
        return names;
    }
    
    public BasicPlayer getCustomPlayer(String name) {
        for(BasicPlayer player : this.players.values()) {
            if(player.getName().equalsIgnoreCase(name)) { return player; }
        }
        return null;
    }
    
    /**
     Get Online Player
     
     @param uuid of player
     @return babysitter player or null if absent
     */
    public BasicPlayer getCustomPlayer(UUID uuid) { return this.players.getOrDefault(uuid, null); }
    
    /**
     Activate A New BabySitter Player
     
     @param player bukkit?spigot
     */
    public void newCustomPlayer(Player player) {
        if(main.utils().checkPermGroup(player, BasicPlayer.Type.ADMIN) || player.hasPermission(Utils.Perm._permBypass.txt)) {
            addToAdminList(player.getName());
            players.put(player.getUniqueId(), new AdminPlayer(main, player));
        } else { players.put(player.getUniqueId(), new BasicPlayer(main, player)); }
    }
    
    /**
     Unload Player From Memory
     
     @param uuid of player
     */
    public void customPlayerLogOut(UUID uuid) {
        BasicPlayer player = players.getOrDefault(uuid, null);
        if(player != null) {
            /*DEBUG*/
            if(debug.players()) { debug.message("player not null"); }
            if(player instanceof AdminPlayer) {
                this.vanishedAdmins.remove(player.getName());
                this.adminList.remove(player.getName());
            }
            this.players.remove(uuid);
        }
        /*DEBUG*/
        if(debug.players()) { debug.message("player null, or logged out if we seen otherwise"); }
    }
    
    public List<String> getCustomPlayerList() { return Arrays.asList(this.playerList.values().toArray(new String[0])); }
    
    public boolean playerListHasUUID(String uuid) { return this.playerList.containsKey(UUID.fromString(uuid)); }
    
    public void addPlayerToList(UUID uuid, String name) {
        this.playerList.put(uuid, name);
        this.config.create(BasicPlayer.Paths.PLAYER_LIST_YML.path + "." + uuid);
        this.config.set(BasicPlayer.Paths.PLAYER_LIST_YML.path + "." + uuid + BasicPlayer.Paths.PLAYER_LIST_NAME.path, name);
        main.utils().save().savePlayerList(this.config);
    }
    
    public void reloadCustomPlayers() {
        new BukkitRunnable() {
            @Override
            public void run() {
                isReloading = true;
                for(Player p : main.getOnlineBukkitPlayers()) { main.players().newCustomPlayer(p); }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        isReloading = false;
                    }
                }.runTaskLaterAsynchronously(main, 40L);
            }
        }.runTaskLaterAsynchronously(main, 40L);
    }
    
    public void unLoad() {
        /*DEBUG*/if(debug.utils()) { debug.message("un-loading"); }
        for(Player p : this.main.getOnlineBukkitPlayers()) {
            if(isCustomPlayerOnline(p.getName())) {
                BasicPlayer player = main.players().getCustomPlayer(p.getUniqueId());
                player.threadSave();
                //save() fast & thread safe
                main.players().customPlayerLogOut(p.getUniqueId());
            }
        }
        /*DEBUG*/
        if(debug.utils()) { debug.message("un-loaded"); }
    }
}
