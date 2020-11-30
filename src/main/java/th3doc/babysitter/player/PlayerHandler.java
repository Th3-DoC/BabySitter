package th3doc.babysitter.player;

import org.bukkit.entity.Player;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PlayerHandler {

    //CONSTRUCTOR
    private Main main;
    public PlayerHandler(Main main)
    {
        this.main = main;
        inventories = new PlayerInventories(main);
        locations = new PlayerLocation(main);
        adminPlayer = new PlayerAdmin(main);
    }

    //PLAYER DATA

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
    public boolean isAdmin(String pName) { return this.admin().list().contains(pName); }

    //INITIALIZE
    public void initialize(Player p)
    {
        //ADD PLAYER NAME TO CONFIG FOLDER IF NONE EXISTS
        File file;
        file = new File(main.getDataFolder(),
                File.separator + Config._playerData.txt +
                        File.separator + p.getUniqueId().toString() +
                        File.separator + "!." + p.getName() + ".yml");
        if(!file.exists())
        {
            //ADD CHECK FOR OLD FILE!!!!!!
            try { file.getParentFile().mkdirs(); file.createNewFile(); }
            catch(IOException f) { f.printStackTrace(); }
        }

        //INITIALIZE PLAYER ACCESS
        inventories.initialize(p);
        locations.initialize(p);
        adminPlayer.initialize(p);

    }

    //GET PLAYER PERMISSION HANDLER GROUP
    public String getPermGroup(Player p) {
        String group = "";
        if (main.getLuckPerms().getUserManager().getUser(p.getUniqueId()) != null)
        {
            group = main.getLuckPerms().getUserManager().getUser(p.getUniqueId())
                    .getPrimaryGroup();
        }
        return group;
    }

    //VANISH ADMINS FROM PLAYER
    public void vanishAdmin(Player p)
    {
        List<String> vanishedAdmins = adminPlayer.getVanishedAdmins();
        //CHECK FOR VANISHED ADMIN
        if (!vanishedAdmins.isEmpty())
        {
            //ITERATE ADMINS AND HIDE FROM PLAYER
            for (String name : vanishedAdmins)
            {
                Player admin = main.getServer().getPlayer(name);
                //CHECK ADMIN IS VALID
                if (admin != null)
                {
                    //CHECK PLAYER IS NOT AN ADMIN
                    if (!isAdmin(p.getName()))
                    {
                        p.hidePlayer(main, admin);
                    }
                }
            }
        }
    }

    //PLAYER MEMORY DUMP
    public void memoryDump(Player p)
    {
        inventories.memoryDump(p);
        locations.memoryDump(p);
        adminPlayer.memoryDump(p);
    }
}
