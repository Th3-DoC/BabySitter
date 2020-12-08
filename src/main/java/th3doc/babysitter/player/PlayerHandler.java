package th3doc.babysitter.player;

import org.bukkit.entity.Player;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.admin.PlayerAdmin;
import th3doc.babysitter.player.inventories.PlayerInventories;
import th3doc.babysitter.player.location.PlayerLocation;
import th3doc.babysitter.player.rewards.Rewards;

import java.util.List;
import java.util.UUID;

public class PlayerHandler {

    //CONSTRUCTOR
    private final Main main;
    public Main getMain() { return this.main; }
    private final Player p;
    public Player getPlayer() { return this.p; }
    private final String name;
    public String getName() { return this.name; }
    private final UUID uuid;
    public UUID getUUID() { return this.uuid; }
    private final PlayerConfig config;
    public PlayerConfig getConfig() { return this.config; }

    public PlayerHandler(Main main, Player p)
    {
        //main
        this.main = main;
        this.p = p;
        this.name = p.getName();
        this.uuid = p.getUniqueId();
        //object classes
        this.config = new PlayerConfig(this);
        this.rewards = new Rewards(main);//pass handler
        this.inventories = new PlayerInventories(main);//pass handler
        this.locations = new PlayerLocation(main);//pass handler
        this.adminPlayer = new PlayerAdmin(this);//pass handler
        //player list
        config.addPlayerList();
        //INITIALIZE PLAYER ACCESS
        rewards.initialize();
        adminPlayer.initialize(p);
        inventories.initialize(p);
        locations.initialize(p);
    }

    //PLAYER DATA
    
    //REWARDS
    private Rewards rewards;
    public Rewards rewards() { return rewards; }

    //INVENTORIES
    private PlayerInventories inventories;
    public PlayerInventories inventory() { return inventories; }

    //LOCATIONS
    private PlayerLocation locations;
    public PlayerLocation location() { return locations; }

    //ADMIN PLAYER
    private PlayerAdmin adminPlayer;
    public PlayerAdmin admin() { return adminPlayer; }
    
    //CHECK ADMIN LIST FOR PLAYER
    public boolean isAdmin() { return adminPlayer.list().contains(name); }

    //GET PLAYER PERMISSION HANDLER GROUP
    public String getPermGroup() {
        String group = "";
        if (main.getGroups().contains(main.getPerms().getPrimaryGroup(p)))
        {
            group = main.getPerms().getPrimaryGroup(p);
        }
        return group;
    }

    //VANISH ADMINS FROM PLAYER
    public void vanishAdmin()
    {
        List<String> vanishedAdmins = adminPlayer.getVanishedAdmins();
        //CHECK FOR VANISHED ADMIN
        if (!vanishedAdmins.isEmpty())
        {
            //ITERATE ADMINS AND HIDE FROM PLAYER
            for (String vAdmin : vanishedAdmins)
            {
                Player admin = main.getServer().getPlayer(vAdmin);
                //CHECK ADMIN IS VALID
                if (admin != null)
                {
                    //CHECK PLAYER IS NOT AN ADMIN
                    if (!isAdmin())
                    {
                        p.hidePlayer(main, admin);
                    }
                }
            }
        }
    }

    //PLAYER MEMORY DUMP
    public void memoryDump()
    {
        inventories.memoryDump(p);
        locations.memoryDump(p);
        adminPlayer.memoryDump(p);
    }
}
