package th3doc.babysitter;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import th3doc.babysitter.commands.*;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.player.PlayerHandler;

import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable()
    {
        registerLuckPerms();
        defaultConfigStatus();
        registerCommands();
        this.getServer().getPluginManager().registerEvents(new Events(this), this);
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
        this.getCommand("god").setExecutor(new GodCommand(this));
        this.getCommand("fly").setExecutor(new FlyCommand(this));
        this.getCommand("see").setExecutor(new SeeInventoryCommand(this));
        this.getCommand("give").setExecutor(new GiveCommand(this));
    }

    //RELOAD COMMAND
    private void reloadCommand()
    {
        for (Player p : this.getServer().getOnlinePlayers())
        {
            this.player().admin().initializeAdmin(p);
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
