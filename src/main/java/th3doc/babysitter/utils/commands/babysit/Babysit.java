//package th3doc.babysitter.utils.commands.babysit;
//
//import org.bukkit.Bukkit;
//import org.bukkit.GameMode;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import th3doc.babysitter.Main;
//import th3doc.babysitter.entities.player.AdminPlayer;
//import th3doc.babysitter.utils.Utils;
//
//public class Babysit implements CommandExecutor
//{
//
//    //CONSTRUCTOR
//    private final Main main;
//    public Babysit(Main main) { this.main = main; }
//
//    @Override
//    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//
//        //CHECK INSTANCE OF PLAYER
//        if (!(sender instanceof AdminPlayer)) {
//            Bukkit.getLogger().info(Utils.Chat._noConsole.txt);
//            return false;
//        }
//        AdminPlayer p = (AdminPlayer) sender;
//
//        //CHECK PLAYER PERMISSION
//        if (p.hasPermission(Utils.Perm._babysitCommand.txt))
//        {
//            //CHECK ADMIN STATE
//            if (!p.getState(AdminPlayer.State.ADMIN))
//            {
//                //CHECK PLAYER TARGET OR BYPASS PERMISSIONS TO CONTINUE
//                if (!p.hasPermission(Utils.Perm._forceSpectator.txt)
//                        && (main.utils().getConfig().isSpectateForced()
//                        && args.length < 1))
//                {
//                    p.sendMessage(Utils.Chat._targetInvalid.txt);
//                    return false;
//                }
//                //SAVE SURVIVAL LOCATION?INVENTORY
//                p.setSurvivalLastKnown(p.getLocation());
//                p.saveSurvivalInventory(p.getInventory().getContents(),
//                                   p.getEnderChest().getContents());
//                //LOAD PERMS
//                p.setPermissions(true);
//                p.setPermissions(true);
//                if(!p.hasPermission(Utils.Perm._vanishBypass.txt)) { p.toggleVanish(); }
//                //CHECK PERMISSIONS
//                if (main.utils().getConfig().isSpectateForced()
//                        && !p.hasPermission(Utils.Perm._forceSpectator.txt))
//                {
//                    //SPECTATE PLAYER
//                    if (!p.forceSpectate(args)) { return false; }
//                    else { p.forceSpectate(args); }
//                }
//                else if ((p.hasPermission(Utils.Perm._forceSpectator.txt)
//                          || !main.utils().getConfig().isSpectateForced())
//                         && args.length >= 1)
//                {
//                    if (!p.forceSpectate(args)) { return false; }
//                    else { p.forceSpectate(args); }
//                }
//                //LOAD BABYSIT INVENTORY
//                p.getAdminInventory();
//                p.setInvulnerable(true);
//                if (p.getState(AdminPlayer.State.FLY))
//                {
//                    p.setAllowFlight(true);
//                    p.setFlying(true);
//                }
//                p.sendMessage(Utils.Chat._babySittingTime.txt);
//                p.setState(true, AdminPlayer.State.ADMIN);
//            }
//            else
//            {
//                //ADMIN STATE TRUE
//                if (args.length >= 1)
//                {
//                    p.forceSpectate(args);
//                    return false;
//                }
//                //SAVE BABYSIT INVENTORY
//                p.saveAdminInventory(p.getInventory().getContents(),
//                                   p.getEnderChest().getContents());
//                //LOAD SURVIVAL INVENTORY
//                p.getSurvivalInventory();
//                //CHECK SURVIVAL LOCATION IS VALID
//                if (p.getSurvivalLastKnown() != null
//                        && !p.hasPermission(Utils.Perm._tpBypass.txt))
//                {
//                    p.teleport(p.getSurvivalLastKnown());
//                } else
//                {
//                    if(!p.hasPermission(Utils.Perm._tpBypass.txt)) { p.sendMessage(Utils.Chat._noSLoc.txt); }
//                }
//                if (!p.hasPermission(Utils.Perm._permBypass.txt)) {
//                    p.setInvulnerable(false);
//                    if (!p.getActivePotionEffects().isEmpty()) { p.performCommand("effect clear"); }
//                    p.setPermissions(false);
//                    p.setPermissions(false);
//                    if (p.getState(AdminPlayer.State.VANISH))
//                    {
//                        p.toggleVanish();
//                    }
//                    if (!main.utils().getConfig().canAdminFlySurvival() || !p.hasPermission(Utils.Perm._flyBypass.txt))
//                    {
//                        p.setFlying(false);
//                        p.setAllowFlight(false);
//                    }
//                    if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR))
//                    {
//                        p.setGameMode(GameMode.SURVIVAL);
//                    }
//                }
//                p.sendMessage(Utils.Chat._babySittingDone.txt);
//                p.setState(false, AdminPlayer.State.ADMIN);
//            }
//        }
//        return false;
//    }
//}
