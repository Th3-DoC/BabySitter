package th3doc.babysitter.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.data.*;

public class BabysitCommand implements CommandExecutor {

    //CONSTRUCTOR
    private final Main main;
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
            if (!main.getPlayer(p.getUniqueId()).admin().getConfig().getState(States.ADMIN))
            {
                //CHECK PLAYER TARGET OR BYPASS PERMISSIONS TO CONTINUE
                if (!p.hasPermission(Perm._forceSpectator.txt)
                        && (main.defaultConfig().isSpectateForced()
                        && args.length < 1))
                {
                    p.sendMessage(Chat._targetInvalid.txt);
                    return false;
                }
                //SAVE SURVIVAL LOCATION?INVENTORY
                main.getPlayer(p.getUniqueId()).location().setSurvivalLastKnown(p.getLocation());
                main.getPlayer(p.getUniqueId()).inventory().getConfig()
                    .saveInventory(p.getInventory().getContents(),
                                   p.getInventory().getArmorContents(),
                                   p.getEnderChest().getContents(),
                                   InvType.Survival);
                //LOAD PERMS
                main.getPlayer(p.getUniqueId()).admin().setPermissions(true, PlayerType.Admin);
                main.getPlayer(p.getUniqueId()).admin().setPermissions(true, PlayerType.Special);
                if(!p.hasPermission(Perm._vanishBypass.txt)) { main.getPlayer(p.getUniqueId()).admin().toggleVanish(); }
                //CHECK PERMISSIONS
                if (main.defaultConfig().isSpectateForced()
                        && !p.hasPermission(Perm._forceSpectator.txt))
                {
                    //SPECTATE PLAYER
                    if (!main.getPlayer(p.getUniqueId()).admin().forceSpectate(p, args)) { return false; }
                    else { main.getPlayer(p.getUniqueId()).admin().forceSpectate(p, args); }
                }
                else if ((p.hasPermission(Perm._forceSpectator.txt)
                          || !main.defaultConfig().isSpectateForced())
                         && args.length >= 1)
                {
                    if (!main.getPlayer(p.getUniqueId()).admin().forceSpectate(p, args)) { return false; }
                    else { main.getPlayer(p.getUniqueId()).admin().forceSpectate(p, args); }
                }
                //LOAD BABYSIT INVENTORY
                main.getPlayer(p.getUniqueId()).inventory().getConfig().getInventory(InvType.Babysit);
                p.setInvulnerable(true);
                if (main.getPlayer(p.getUniqueId()).admin().getConfig().getState(States.FLY))
                {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                }
                p.sendMessage(Chat._babySittingTime.txt);
                main.getPlayer(p.getUniqueId()).admin().getConfig().setState(true, States.ADMIN);
            }
            else
            {
                //ADMIN STATE TRUE
                if (args.length >= 1)
                {
                    main.getPlayer(p.getUniqueId()).admin().forceSpectate(p, args);
                    return false;
                }
                //SAVE BABYSIT INVENTORY
                main.getPlayer(p.getUniqueId()).inventory().getConfig()
                    .saveInventory(p.getInventory().getContents(),
                                   p.getInventory().getArmorContents(),
                                   p.getEnderChest().getContents(),
                        InvType.Babysit);
                //LOAD SURVIVAL INVENTORY
                main.getPlayer(p.getUniqueId()).inventory().getConfig().getInventory(InvType.Survival);
                //CHECK SURVIVAL LOCATION IS VALID
                if (main.getPlayer(p.getUniqueId()).location().getSurvivalLastKnown() != null
                        && !p.hasPermission(Perm._tpBypass.txt))
                {
                    p.teleport(main.getPlayer(p.getUniqueId()).location().getSurvivalLastKnown());
                } else
                {
                    if(!p.hasPermission(Perm._tpBypass.txt)) { p.sendMessage(Chat._noSLoc.txt); }
                }
                if (!p.hasPermission(Perm._permBypass.txt)) {
                    p.setInvulnerable(false);
                    if (!p.getActivePotionEffects().isEmpty()) { p.performCommand("effect clear"); }
                    main.getPlayer(p.getUniqueId()).admin().setPermissions(false, PlayerType.Admin);
                    main.getPlayer(p.getUniqueId()).admin().setPermissions(false, PlayerType.Special);
                    if (main.getPlayer(p.getUniqueId()).admin().getConfig().getState(States.VANISH))
                    {
                        main.getPlayer(p.getUniqueId()).admin().toggleVanish();
                    }
                    if (!main.defaultConfig().canAdminFlySurvival() || !p.hasPermission(Perm._flyBypass.txt))
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
                main.getPlayer(p.getUniqueId()).admin().getConfig().setState(false, States.ADMIN);
            }
            main.getPlayer(p.getUniqueId()).save();
        }
        return false;
    }
}
