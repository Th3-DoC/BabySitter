package th3doc.babysitter.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.player.data.*;

public class BabysitCommand implements CommandExecutor {

    //CONSTRUCTOR
    private Main main;
    public BabysitCommand(Main main) { this.main = main; }

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
            if (!main.player().admin().getState(p.getName(), States.Babysit))
            {
                //CHECK PLAYER TARGET OR BYPASS PERMISSIONS TO CONTINUE
                if (!p.hasPermission(Perm._forceSpectator.txt)
                        && (main.getConfig().getBoolean(Config._forceSpectate.txt)
                        && args.length < 1))
                {
                    p.sendMessage(Chat._targetInvalid.txt);
                    return false;
                }
                //SAVE SURVIVAL LOCATION?INVENTORY
                main.player().location().setSurvivalLastKnown(p, p.getLocation());
                main.player().inventory().saveInventory(p
                        , p.getInventory().getContents()
                        , p.getInventory().getArmorContents()
                        , p.getEnderChest().getContents(),
                        InvType.Survival);
                //LOAD PERMS
                main.player().admin().setPermissions(p, true, PlayerType.Admin);
                main.player().admin().setPermissions(p, true, PlayerType.Special);
                if(!p.hasPermission(Perm._vanishBypass.txt)) { main.player().admin().toggleVanish(p); }
                //CHECK PERMISSIONS
                if (main.getConfig().getBoolean(Config._forceSpectate.txt)
                        && !p.hasPermission(Perm._forceSpectator.txt))
                {
                    //SPECTATE PLAYER
                    if (!main.player().admin().forceSpectate(p, args)) { return false; }
                    else { main.player().admin().forceSpectate(p, args); }
                }
                else
                {
                    if ((p.hasPermission(Perm._forceSpectator.txt)
                            || !main.getConfig().getBoolean(Config._forceSpectate.txt))
                        && args.length >= 1)
                    {
                        if (!main.player().admin().forceSpectate(p, args)) { return false; }
                        else { main.player().admin().forceSpectate(p, args); }
                    }
                }
                //LOAD BABYSIT INVENTORY
                main.player().inventory().getInventory(p, InvType.Babysit);
                p.setInvulnerable(true);
                if (main.player().admin().getState(p.getName(), States.Fly))
                {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                }
                p.sendMessage(Chat._babySittingTime.txt);
                main.player().admin().setState(p, true, States.Babysit);
            }
            else
            {
                //ADMIN STATE TRUE
                if (args.length >= 1)
                {
                    main.player().admin().forceSpectate(p, args);
                    return false;
                }
                //SAVE BABYSIT INVENTORY
                main.player().inventory().saveInventory(p
                        , p.getInventory().getContents()
                        , p.getInventory().getArmorContents()
                        , p.getEnderChest().getContents(),
                        InvType.Babysit);
                //LOAD SURVIVAL INVENTORY
                main.player().inventory().getInventory(p, InvType.Survival);
                //CHECK SURVIVAL LOCATION IS VALID
                if (main.player().location().getSurvivalLastKnown(p) != null
                        && !p.hasPermission(Perm._tpBypass.txt))
                {
                    p.teleport(main.player().location().getSurvivalLastKnown(p));
                } else
                {
                    if(!p.hasPermission(Perm._tpBypass.txt)) { p.sendMessage(Chat._noSLoc.txt); }
                }
                if (!p.hasPermission(Perm._permBypass.txt)) {
                    p.setInvulnerable(false);
                    if (!p.getActivePotionEffects().isEmpty()) { p.performCommand("effect clear"); }
                    main.player().admin().setPermissions(p, false, PlayerType.Admin);
                    main.player().admin().setPermissions(p, false, PlayerType.Special);
                    if (main.player().admin().getState(p.getName(), States.Vanish))
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
                main.player().admin().setState(p, false, States.Babysit);
            }
        }
        return false;
    }
}
