package th3doc.babysitter.entities.player;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import th3doc.babysitter.Main;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.debug.Debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminPlayer extends BasicPlayer {
    public enum State { ADMIN, VANISH, FLY }
    
    private enum Paths {
        ADMIN_YML("Player_Config.yml", "player-config.admin"),
        ADMIN_STATE("Admin_State", "player-config.admin.states.admin-state"),
        FLY_STATE("Fly_state", "player-config.admin.states.fly-state"),
        VANISH_STATE("Vanish_State", "player-config.admin.states.vanish-state"),
        ADMIN_INV("Admin_Inventory", "player-config.admin.inventories.inventory"),
        ADMIN_ECHEST("Admin_Echest", "player-config.admin.inventories.e-chest"),
        SURVIVAL_LOC("Survival_Location", "player-config.admin.locations.survival-location");
        
        public String name;
        public String path;
        
        Paths(String name, String path) {
            this.name = name;
            this.path = path;
        }
    }
    
    //VARIABLES
    final private Debug debug;
    final private List< ItemStack > invToCheck;
    private boolean adminState;
    private boolean flyState;
    private boolean vanishState;
    private ItemStack[] adminInv;
    private ItemStack[] adminEchest;
    private Location survivalLastKnown;
    private boolean isSpecTarget;
    private boolean isChestEdit;
    private BukkitTask id;
    
    
    //CONSTRUCTOR
    
    public AdminPlayer(Main main, Player player) {
        super(main, player);
        this.debug = main.debug();
        //DEBUG
        if(debug.players()) { debug.message("loading admin player"); }
        this.invToCheck = new ArrayList<>();
        this.isSpecTarget = false;
        
        
        boolean save = false;
        
        //babysit inv
        if(!config().isSet(Paths.ADMIN_INV.path)) {
            config().set(Paths.ADMIN_INV.path, serializeItemArray(new ItemStack[41]));
            save = true;
        }
        this.adminInv = deSerializeItemArray(config().getStr(Paths.ADMIN_INV.path));
        if(!config().isSet(Paths.ADMIN_ECHEST.path)) {
            config().set(Paths.ADMIN_ECHEST.path, serializeItemArray(new ItemStack[27]));
            save = true;
        }
        this.adminEchest = deSerializeItemArray(config().getStr(Paths.ADMIN_ECHEST.path));
        if(utils().getConfig().isSpecialPermsAlwaysActive() && utils().checkPermGroup(getPlayer(), Type.SPECIAL_ADMIN)) { setPermissions(true); }
        if(!config().isSet(Paths.ADMIN_STATE.path)) { config().set(Paths.ADMIN_STATE.path, "false"); save = true; }
        if(!config().isSet(Paths.VANISH_STATE.path)) { config().set(Paths.VANISH_STATE.path, "false"); save = true; }
        if(!config().isSet(Paths.FLY_STATE.path)) { config().set(Paths.FLY_STATE.path, "false"); save = true; }
        this.adminState = config().getBoo(Paths.ADMIN_STATE.path);
        this.vanishState = config().getBoo(Paths.VANISH_STATE.path);
        this.flyState = config().getBoo(Paths.FLY_STATE.path);
        if(getState(State.ADMIN)
           || this.hasPermission(Utils.Perm._vanishBypass.txt)) {
            if(getState(State.VANISH)) {
                toggleVanish();
            }
        }
        
        //SURVIVAL LOCATION
        if(!config().isSet(Paths.SURVIVAL_LOC.path)) {
            config().set(Paths.SURVIVAL_LOC.path, getLocation());
            save = true;
        }
        this.survivalLastKnown = config().getLoc(Paths.SURVIVAL_LOC.path);
        if(save) { config().save(); }
        //DEBUG
        if(debug.players()) { debug.message("admin player loaded"); }
    }
    
    @Override
    public void isFlyingAllowed() {
        //DEBUG
        if(main().debug().players()) { main().debug().message("Setting Admin Flying"); }
        if(config().getBoo(Paths.ADMIN_STATE.path) ||
           hasPermission(Utils.Perm._flyBypass.txt) ||
           hasPermission(Utils.Perm._permBypass.txt)) {
            if(!config().isSet(BasicPlayer.Paths.ALLOW_FLIGHT.path)) {
                config().set(BasicPlayer.Paths.ALLOW_FLIGHT.path, false);
            }
            this.setAllowFlight(config().getBoo(BasicPlayer.Paths.ALLOW_FLIGHT.path));
            if(!config().isSet(BasicPlayer.Paths.IS_FLYING.path)) {
                config().set(BasicPlayer.Paths.IS_FLYING.path, false);
            }
            this.setFlying(config().getBoo(BasicPlayer.Paths.IS_FLYING.path));
            
        } else {
            setAllowFlight(false);
            setFlying(false);
        }
    }
    
    @Override
    public void loginMsg() {
        if(!config().getBoo(Paths.VANISH_STATE.path) && !players().isReloading()) {
            main().getServer().broadcastMessage(ChatColor.YELLOW + this.getName() + Utils.Chat._fakeLogIn.txt);
        }
    }
    
    public ItemStack[] invToCheck() { return invToCheck.toArray(new ItemStack[0]); }
    
    public boolean isCheckingInv() { return !invToCheck.isEmpty(); }
    
    public void stopCheckingInv() { invToCheck.clear(); }
    
    public void saveInventoryToCheck(ItemStack item) { invToCheck.add(item); }
    
    public boolean getState(State state) {
        if(state == State.ADMIN) { return adminState; } else if(state == State.FLY) {
            return flyState;
        } else if(state == State.VANISH) { return vanishState; }
        return false;
    }
    
    public boolean setState(boolean boo, State state) {
        switch(state) {
            case ADMIN:
                adminState = boo;
                config().set(Paths.ADMIN_STATE.path, this.adminState);
                return true;
            case FLY:
                flyState = boo;
                config().set(Paths.FLY_STATE.path, this.flyState);
                return true;
            case VANISH:
                vanishState = boo;
                config().set(Paths.VANISH_STATE.path, this.vanishState);
                return true;
            default:
                return false;
        }
    }
    
    public void getAdminInventory() {
        saveSurvivalInventory(getInventory().getContents(), getEnderChest().getContents());
        getInventory().setContents(this.adminInv);
        getEnderChest().setContents(this.adminEchest);
        
    }
    
    public void saveAdminInventory(ItemStack[] inv, ItemStack[] eChest) {
        this.adminInv = inv;
        this.adminEchest = eChest;
        config().set(Paths.ADMIN_INV.path, serializeItemArray(adminInv));
        config().set(Paths.ADMIN_ECHEST.path, serializeItemArray(adminEchest));
    }
    
    public void setSurvivalLastKnown(Location loc) {
        this.survivalLastKnown = loc;
        config().set(Paths.SURVIVAL_LOC.path, this.survivalLastKnown);
    }
    
    public Location getSurvivalLastKnown() { return survivalLastKnown; }
    
    //PERMISSIONS
    public void setPermissions(boolean boo) {
        if(!hasPermission(Utils.Perm._permBypass.txt)) {
            if(utils().checkPermGroup(this, Type.ADMIN)) {
                runPerms(utils().getConfig().getAdminPermissionsList(), boo);
            }
            if(utils().getConfig().isSpecialPermissionsActive()
               && utils().checkPermGroup(this, BasicPlayer.Type.SPECIAL_ADMIN)) {
                runPerms(utils().getConfig().getSpecialPermissionsList(), boo);
            }
        }
    }
    
    private void runPerms(HashMap< String, Boolean > permissionList, boolean boo) {
        for(String permission : permissionList.keySet()) {
            boolean value =
                    permissionList.get(permission);
            //CHECK PERMISSION IS VALID
            if(permission != null) {
                OfflinePlayer offlinePlayer = this;
                if(!value && hasPermission(permission)) { continue; }
                if(boo && value) {
                    if(!hasPermission(permission)) { utils().addPlayerPermission(offlinePlayer, permission); }
                }
                if(!boo && value) {
                    if(hasPermission(permission)) { utils().removePlayerPermission(offlinePlayer, permission); }
                }
            }
        }
    }
    
    public boolean isChestEdit() {
        return isChestEdit;
    }
    
    public void setChestEdit(boolean b) {
        isChestEdit = b;
    }
    
    //PLAYER TAB INFO TRUE?FALSE
    private void playerTabInfo(boolean boo) {
        PacketPlayOutPlayerInfo.EnumPlayerInfoAction info;
        if(!boo) { info = PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER; } else {
            info = PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER;
        }
        Player bukkitPlayer = Bukkit.getPlayer(getUniqueId());
        EntityPlayer[] playerNMS = new EntityPlayer[1];
        playerNMS[0] = ((CraftPlayer) bukkitPlayer).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(info, playerNMS);
        for(String player : players().getCustomOnlinePlayers()) {
            if(!players().getAdmins().contains(player)) {
                BasicPlayer p = players().getCustomPlayer(player);
                ((CraftPlayer) p.getPlayer()).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }
    
    //VANISH TOGGLE
    public void toggleVanish() {
        //CHECK IF PLAYER IS VANISHED
        if(!players().getVanishedAdmins().contains(getName())) {
            //ITERATE AND HIDE ADMIN FROM PLAYERS
            for(Player p : main().getOnlineBukkitPlayers()) {
                //CHECK IF PLAYER IS AN ADMIN
                if(!players().getAdmins().contains(getName())) {
                    p.hidePlayer(main(), this);
                }
            }
            setCollidable(false);
            setSilent(true);
            players().addToVanishList(getName());
            new BukkitRunnable() {
                @Override
                public void run() {
                    playerTabInfo(false);
                }
            }.runTaskLater(main(), 1L);
            if(getState(State.VANISH)) { if(!players().isReloading()) { message(Utils.Chat._vanishOn.txt); } return; }
            setState(true, State.VANISH);
            main().massMessage(ChatColor.YELLOW + getName() + Utils.Chat._fakeLogOut.txt);
        } else {//TODO ensure players become visible again
            playerTabInfo(true);
            //ITERATE AND UN-HIDE ADMIN FROM PLAYERS
            for(Player p : main().getOnlineBukkitPlayers()) {
                p.showPlayer(main(), getPlayer());
            }
            setCollidable(true);
            setSilent(false);
            players().removeVanishedAdminFromList(getName());
            setState(false, State.VANISH);
            main().massMessage(ChatColor.YELLOW + getName() + Utils.Chat._fakeLogIn.txt);
        }
    }
    
    public boolean spectatePlayer(String[] args) {
        if(players().isCustomPlayerOnline(args[1])) {
            BasicPlayer player = players().getCustomPlayer(args[1]);
            if((this.getUniqueId() == player.getUniqueId() || args.length != 2)) {
                message(Utils.Chat.INVALID_TARGET.txt);
                return false;
            } else {
                BasicPlayer p = this;
                isSpecTarget = true;
                teleport(player.getPlayer());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setGameMode(GameMode.SPECTATOR);
                        setSpectatorTarget(player.getPlayer());
                        getSpectatorTarget().addPassenger(p.getPlayer());
                        id = new BukkitRunnable() {
                            @Override
                            public void run() {
                                removeSpecPassenger();
                            }
                        }.runTaskLater(main(), 6000);
                    }
                }.runTaskLater(main(), 10L);
                return true;
            }
        }
        return false;
    }
    
    public void removeSpecPassenger() {
        if(isSpecTarget) {
            id.cancel();
            getSpectatorTarget().eject();
            setSpectatorTarget(null);
            isSpecTarget = false;
        } else { message(org.bukkit.ChatColor.RED + "You Have To Target Someone First!"); }
    }
    
    public boolean bsMode() {
        if(!hasPermission(Utils.Perm.FORCE_SPEC_BYPASS.txt) && utils().getConfig().isSpectateForced()) {
            message(Utils.Chat.INVALID_CMD.txt + "\n /bs spectate <player>");
            return false;
        }
        bsProperties();
        return true;
    }
    
    private void bsProperties() {
        boolean b = false;
        if(!getState(State.ADMIN)) {
            survivalLastKnown = getLocation();
            config().set(Paths.SURVIVAL_LOC.path, survivalLastKnown);
            getAdminInventory();
            if(!hasPermission(Utils.Perm._permBypass.txt)) {
                setInvulnerable(true);
                if (!getState(State.VANISH) && !hasPermission(Utils.Perm._vanishBypass.txt)) { toggleVanish(); }
            }
            message(Utils.Chat._babySittingTime.txt);
            b = true;
        } else {
            getSurvivalInventory();
            if(!hasPermission(Utils.Perm._permBypass.txt)) {
                if(survivalLastKnown != null) { teleport(survivalLastKnown); }
                else { message(Utils.Chat._noSLoc.txt); }
                setInvulnerable(false);
                if (!getActivePotionEffects().isEmpty()) { performCommand("effect clear"); }
                if (getState(State.VANISH) && !hasPermission(Utils.Perm._vanishBypass.txt)) { toggleVanish(); }
                if (getGameMode().equals(GameMode.CREATIVE) || getGameMode().equals(GameMode.SPECTATOR) ||
                    getGameMode().equals(GameMode.ADVENTURE)) {
                    setGameMode(GameMode.SURVIVAL);
                }
            }
            message(Utils.Chat._babySittingDone.txt);
        }
        setPermissions(b);
        setState(b, State.ADMIN);
        if((!hasPermission(Utils.Perm._flyBypass.txt) && !utils().getConfig().canAdminFlySurvival()) ||
           (hasPermission(Utils.Perm._flyBypass.txt) && getState(State.FLY))) {
            if ((getState(State.ADMIN) || hasPermission(Utils.Perm._flyBypass.txt)) &&
                getState(State.FLY))
            {
                setAllowFlight(true);
                setFlying(true);
            } else { setAllowFlight(false); setFlying(false); }
        }
    }
}
