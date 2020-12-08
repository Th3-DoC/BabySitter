package th3doc.babysitter;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.commands.*;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.events.*;
import th3doc.babysitter.player.PlayerHandler;
import th3doc.babysitter.player.data.Chat;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable()
    {
        if(!setupPermissions())
        {
            Bukkit.getLogger().info("Error Loading Permission Handler, stopping plugin.");
            Bukkit.getPluginManager().getPlugin(this.getName());
        }
        defaultConfigStatus();
        registerCommands();
        registerEvents();
        player = new HashMap<>();
        groups = new ArrayList<>();
        setGroups();
        reloadCommand();
        this.getLogger().info(Chat._onEnable.txt);

    }

    //GET PLAYER
    private static HashMap<UUID, PlayerHandler> player;
    public void newPlayer(Player p)
    {
        Main main = this;
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                player.put(p.getUniqueId(), new PlayerHandler(main, p));
            }
        } .runTaskAsynchronously(this);
    }
    public PlayerHandler player(UUID uuid) { return player.get(uuid); }

    //REGISTER PERMISSIONS
    private Permission perms;
    public Permission getPerms() { return perms; }
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    //PERM GROUPS
    private List<String> groups;
    private void setGroups()
    {
        new BukkitRunnable() {
            @Override
            public void run()
            {
                groups.clear();
                groups.addAll(Arrays.asList(perms.getGroups()));
            }
        } .runTaskTimerAsynchronously(this, 0L, 6000L);
    }
    public List<String> getGroups() { return groups; }

    //REGISTER COMMANDS
    private void registerCommands()
    {
        this.getCommand("babysit").setExecutor(new BabysitCommand(this));
        this.getCommand("vanish").setExecutor(new VanishCommand(this));
        this.getCommand("god").setExecutor(new GodCommand());
        this.getCommand("fly").setExecutor(new FlyCommand(this));
        this.getCommand("see").setExecutor(new SeeInventoryCommand(this));
        this.getCommand("give").setExecutor(new GiveCommand(this));
        this.getCommand("back").setExecutor(new BackCommand(this));
    }

    //REGISTER EVENTS
    private void registerEvents()
    {
        PluginManager manger = this.getServer().getPluginManager();

        manger.registerEvents(new InventoryClose(this), this);
        manger.registerEvents(new PlayerCommandPreProcess(this), this);
        manger.registerEvents(new PlayerJoin(this), this);
        manger.registerEvents(new PlayerQuit(this), this);
        manger.registerEvents(new BlockPlace(this), this);
        manger.registerEvents(new PlayerDropItem(this), this);
        manger.registerEvents(new InventoryOpen(this), this);
    }

    //RELOAD COMMAND
    private void reloadCommand()
    {
        //INITIALIZE PLAYERS
        for (Player p : this.getServer().getOnlinePlayers())
        {
            newPlayer(p);
        }
    }

    //CONFIGS
    private void defaultConfigStatus()
    {
        //**********
        //config.yml
        //**********
        File file = new File(this.getDataFolder(), Config._config.txt);

        if (!this.getDataFolder().exists()) { this.getDataFolder().mkdirs(); }
        if (!file.exists())
        {
            this.getLogger().info(Chat._configMissing.txt);
            this.saveDefaultConfig();
        }
        this.getLogger().info(Chat._configFound.txt);
        try { this.getConfig().load(file); }
        catch (IOException | InvalidConfigurationException e) { e.printStackTrace(); }
    }
}
