package th3doc.babysitter;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import th3doc.babysitter.entities.npc.Entities;
import th3doc.babysitter.entities.player.Players;
import th3doc.babysitter.utils.Utils;
import th3doc.babysitter.utils.debug.Debug;

import java.util.Collection;

//TODO Add Jail System // jail on loggin /w message
//TODO PVP Toggle
//TODO Claim System
//TODO command for reloading configs
//TODO Custom Coords-Hud command {p.getfaciing for direction} turn off on death if activa and back on if it was active after respawn
//TODO PROTOCOL LIB SETUP VANISH TAB?ENITITY WATCH? or create a while loop? checking location vs location? or p.canSee???
//TODO Add auto rank up system, time based, require itemstack size/material in hand to upgrade?

public final class Main extends JavaPlugin {
    private boolean isReloading = true;
    private Debug debug;
    private Utils utils;
    private Entities entities;
    private Players players;
    
    @Override
    public void onEnable() {
        this.debug = new Debug(this);
        //DEBUG
        if(debug.utils()) { debug.message("Loading Main"); }
        this.utils = new Utils(this);
        this.entities = new Entities(this);
        this.players = new Players(this);
        this.utils.save().run();
        if(!getOnlineBukkitPlayers().isEmpty()) {
            if(debug.players()) { debug.message("reloading players"); } players.reloadCustomPlayers();
        }
        isReloading = false;
        this.getLogger().info(Utils.Chat._onEnable.txt);
    }
    
    @Override
    public void onDisable() {
        /*DEBUG*/if(debug.utils()) { debug.message("on-disable called"); }
        while(isReloading) {
            players.unLoad();
            /*DEBUG*/if(debug.utils()) { debug.message("players unloaded"); }
            utils.save().cancel();
            /*DEBUG*/if(debug.utils()) { debug.message("files saved"); }
            isReloading = false;
        }
        /*DEBUG*/if(debug.utils()) { debug.message("disable finished"); }
    }
    
    public boolean isReloading() {
        return isReloading;
    }
    
    public void setReloading(boolean b) {
        isReloading = b;
    }
  
    
    public Entities entities() {
        return entities;
    }
    
    public Debug debug() {
        return debug;
    }
    
    /**
     Plugin Utils
     
     @return plugin
     */
    public Utils utils() {
        return utils;
    }
    
    /**
     Player Utils
     
     @return players
     */
    public Players players() {
        return players;
    }
    
    /**
     Get All Online Bukkit Players
     
     @return list of online bukkit players
     */
    public Collection<? extends Player> getOnlineBukkitPlayers() {
        return this.getServer().getOnlinePlayers();
    }
    
    /**
     Broadcast To All Players Online
     
     @param message to be sent
     */
    public void massMessage(String message) {
        for(Player p : getOnlineBukkitPlayers()) { p.sendMessage(message); }
    }
}
