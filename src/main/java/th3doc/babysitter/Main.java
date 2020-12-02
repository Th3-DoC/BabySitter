package th3doc.babysitter;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import th3doc.babysitter.commands.*;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;
import th3doc.babysitter.events.*;
import th3doc.babysitter.player.PlayerHandler;
import th3doc.babysitter.player.data.Chat;

import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable()
    {
        registerLuckPerms();
        defaultConfigStatus();
        registerCommands();
        registerEvents();
        player = new PlayerHandler(this);
        reloadCommand();
        this.getLogger().info(Chat._onEnable.txt);

    }

    //GET PLAYER
    private PlayerHandler player;
    public PlayerHandler player() { return player; }

    //LUCKPERMS ACCESS
    private LuckPerms api;
    public LuckPerms getLuckPerms() { return api; }

    //REGISTER LUCKPERMS
    private void registerLuckPerms()
    {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) { api = provider.getProvider(); }
    }

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
        //PLAYER LIST CONFIG
        ConfigHandler listConfig = new ConfigHandler(this
                , Config._playerData.txt
                , ""
                , Config._playerListConfig.txt);
    
        //INITIALIZE PLAYER BASE
        if(!listConfig.getConfig().isSet(Config._playerList.txt))
        {
            listConfig.getConfig().createSection(Config._playerList.txt);
        }
        
        //INITIALIZE PLAYERS
        for (Player p : this.getServer().getOnlinePlayers())
        {
            this.player.initialize(p);
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
