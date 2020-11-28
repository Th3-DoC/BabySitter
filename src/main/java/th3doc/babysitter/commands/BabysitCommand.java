package th3doc.babysitter.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.*;
import th3doc.babysitter.player.data.Chat;
import th3doc.babysitter.player.data.InvType;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.State;

import java.util.Arrays;
import java.util.List;

public class BabysitCommand implements CommandExecutor {

    //CONSTRUCTOR
    private Main main;
    public BabysitCommand(Main main) { this.main = main; }
    private List<String> inventoryTypes = Arrays.asList("inv", "echest");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //CHECK INSTANCE OF PLAYER
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().info(Chat._noConsole.txt);
            return false;
        }
        Player p = (Player) sender;

        //CHECK PLAYER PERMISSION
        if (p.hasPermission(Perm._babysitCommand.txt))
        {
            //CHECK ADMIN STATE
            if (!main.player().admin().config(p).getConfig().getBoolean(Config._adminState.txt))
            {
                //CHECK PLAYER TARGET OR BYPASS PERMISSIONS TO CONTINUE
                if ((!p.isOp()
                        && !p.hasPermission(Perm._opAdmin.txt)
                        && !p.hasPermission(Perm._forceSpectator.txt))
                        && (main.getConfig().getBoolean(Config._forceSpectate.txt)
                        && args.length < 1))
                {
                    p.sendMessage(Chat._targetInvalid.txt);
                    return false;
                }
                //SAVE SURVIVAL LOCATION?INVENTORY
                main.player().admin().config(p).getConfig().set(Config._survivalLocation.txt, p.getLocation());
                main.player().admin().saveInventory(p
                        , p.getInventory().getContents()
                        , p.getInventory().getArmorContents()
                        , p.getEnderChest().getContents(),
                        InvType.Survival);
                //LOAD PERMS
                main.player().admin().setPermissions(p, true);
                main.player().admin().toggleVanish(p);
                //CHECK PERMISSIONS
                if (main.getConfig().getBoolean(Config._forceSpectate.txt)
                        && (!p.isOp() && !p.hasPermission(Perm._opAdmin.txt)))
                {
                    //SPECTATE PLAYER
                    if (args.length >= 1)
                    {
                        if (!main.player().admin().activateSpectate(p, args)) { return false; }
                        else { main.player().admin().activateSpectate(p, args); }
                    }
                } else
                {
                    if ((p.isOp()
                            || p.hasPermission(Perm._forceSpectator.txt)
                            || p.hasPermission(Perm._opAdmin.txt)
                            || !main.getConfig().getBoolean(Config._forceSpectate.txt))
                            && args.length >= 1)
                    {
                        if (!main.player().admin().activateSpectate(p, args)) { return false; }
                        else { main.player().admin().activateSpectate(p, args); }
                    }
                }
                //LOAD BABYSIT INVENTORY
                main.player().admin().setInventory(p, InvType.Babysit);
                p.setInvulnerable(true);
                if (main.player().admin().config(p).getConfig().getBoolean(Config._flyState.txt))
                {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                }
                p.sendMessage(Chat._babySittingTime.txt);
                main.player().admin().setState(p, true, State.Admin);
            } else
            {
                //ADMIN STATE TRUE
                if (args.length >= 1)
                {
                    main.player().admin().activateSpectate(p, args);
                    return false;
                }
                //SAVE BABYSIT INVENTORY
                main.player().admin().saveInventory(p
                        , p.getInventory().getContents()
                        , p.getInventory().getArmorContents()
                        , p.getEnderChest().getContents(),
                        InvType.Babysit);
                //LOAD SURVIVAL INVENTORY
                main.player().admin().setInventory(p, InvType.Survival);
                //CHECK SURVIVAL LOCATION IS VALID
                if (main.player().admin().config(p).getConfig()
                        .getLocation(Config._survivalLocation.txt) instanceof Location)
                {
                    p.teleport(main.player().admin().config(p).getConfig().getLocation(Config._survivalLocation.txt));
                } else
                {
                    p.sendMessage(Chat._noSLoc.txt);
                }
                if (!p.isOp() && !p.hasPermission(Perm._opAdmin.txt)) {
                    p.setInvulnerable(false);
                    p.performCommand("effect clear");
                    main.player().admin().setPermissions(p, false);
                    if (main.player().admin().config(p).getConfig().getBoolean(Config._vanishState.txt))
                    {
                        main.player().admin().toggleVanish(p);
                    }
                    if (!main.getConfig().getBoolean(Config._adminFlight.txt))
                    {
                        p.setFlying(false);
                        p.setAllowFlight(false);
                    }
                    if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR))
                    {
                        p.setGameMode(GameMode.SURVIVAL);
                    }
                }
                p.sendMessage(Chat._babySittingDone.txt);
                main.player().admin().setState(p, false, State.Admin);
            }
        }
        return false;
    }
}
