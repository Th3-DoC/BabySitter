package th3doc.babysitter.player;

import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.configs.ConfigHandler;
import th3doc.babysitter.Main;
import th3doc.babysitter.enums.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AdminPlayer {

    //CONSTRUCTOR
    private Main main;
    public AdminPlayer(Main main) { this.main = main; }

    /**
     *
     * INVENTORIES
     *
     */
    //SURVIVAL
    private HashMap<UUID, ItemStack[]> survivalInv = new HashMap<>();
    private HashMap<UUID, ItemStack[]> survivalArmour = new HashMap<>();
    private HashMap<UUID, ItemStack[]> survivalEChest = new HashMap<>();
    //BABYSIT
    private HashMap<UUID, ItemStack[]> babysitInv = new HashMap<>();
    private HashMap<UUID, ItemStack[]> babysitArmour = new HashMap<>();
    private HashMap<UUID, ItemStack[]> babysitEChest = new HashMap<>();

    public void saveInventory(Player p, ItemStack[] inv, ItemStack[] armour, ItemStack[] eChest, InvType type)
    {
        HashMap<UUID, ItemStack[]> invContents;
        HashMap<UUID, ItemStack[]> armourContents;
        HashMap<UUID, ItemStack[]> eChestContents;
        String config = "";
        if (type == InvType.Survival)
        {
            invContents = survivalInv;
            armourContents = survivalArmour;
            eChestContents = survivalEChest;
            config = Config._survivalInv.txt;
        } else if (type == InvType.Babysit)
        {
            invContents = babysitInv;
            armourContents = babysitArmour;
            eChestContents = babysitEChest;
            config = Config._babysitInv.txt;
        } else { return; }
        invContents.put(p.getUniqueId(), inv);
        armourContents.put(p.getUniqueId(), armour);
        eChestContents.put(p.getUniqueId(), eChest);
        //SAVE CONFIG
        ConfigurationSection configSection =
                adminConfig.get(p.getUniqueId()).getConfig().getConfigurationSection(config);
        configSection.set(Config._inv.txt, inv);
        configSection.set(Config._armour.txt, armour);
        configSection.set(Config._eChest.txt, eChest);
        adminConfig.get(p.getUniqueId()).save();
    }
    public void setInventory(Player p, InvType type)
    {
        HashMap<UUID, ItemStack[]> invContents;
        HashMap<UUID, ItemStack[]> armourContents;
        HashMap<UUID, ItemStack[]> eChestContents;
        if (type == InvType.Survival)
        {
            invContents = survivalInv;
            armourContents = survivalArmour;
            eChestContents = survivalEChest;
        } else if (type == InvType.Babysit)
        {
            invContents = babysitInv;
            armourContents = babysitArmour;
            eChestContents = babysitEChest;
        } else { return; }
        p.getInventory().setContents(invContents.get(p.getUniqueId()));
        p.getInventory().setArmorContents(armourContents.get(p.getUniqueId()));
        p.getEnderChest().setContents(eChestContents.get(p.getUniqueId()));
    }

    //INVENTORY VIEWERS
    private HashMap<UUID, Boolean> inventoryEdit = new HashMap<>();
    public void setEditingInv(Player p, boolean boo) { inventoryEdit.put(p.getUniqueId(), boo); }
    private HashMap<UUID, String> guiName = new HashMap<>();
    public void setGuiName(Player p, String name) { guiName.put(p.getUniqueId(), name); }
    private HashMap<UUID, UUID> invViewee = new HashMap<>();
    public void setViewee(Player viewer, Player viewee) { invViewee.put(viewer.getUniqueId(), viewee.getUniqueId()); }

    //SAVE EDITED INVENTORY
    public void saveInvEdit(Player p, InventoryCloseEvent e) {
        //ARE WE EDITING AN INVENTORY
        if(inventoryEdit.get(p.getUniqueId()) != null
                && inventoryEdit.get(p.getUniqueId()))
        {
            if (e.getView().getTitle().equals(guiName.get(p.getUniqueId()))) {
                Player viewee = main.getServer().getPlayer(invViewee.get(p.getUniqueId()));
                assert viewee != null;
                if (guiName.get(p.getUniqueId()).contains(InvType.Inventory.name())) {
                    ItemStack[] event_inv = e.getInventory().getContents();
                    List<ItemStack> save_inv = new ArrayList<>();
                    for (ItemStack item : event_inv) {
                        if (save_inv.size() < 41) {
                            save_inv.add(item);
                        } else {
                            break;
                        }
                    }
                    ItemStack[] new_inv = save_inv.toArray(new ItemStack[0]);
                    viewee.getInventory().setContents(new_inv);
                }
                if (guiName.get(p.getUniqueId()).contains(InvType.EnderChest.name())) {
                    viewee.getEnderChest().setContents(e.getInventory().getContents());
                }
                inventoryEdit.remove(p.getUniqueId());
                guiName.remove(p.getUniqueId());
                invViewee.remove(p.getUniqueId());
            }
        }
    }
    /**
     * END INVENTORIES
     */

    /**
     *
     * PERMISSIONS
     *
     */
    //LUCK-PERM PERMISSIONS
    public void setPermissions(Player p, boolean boo)
    {
        setAdminPermissions(p, boo);
        setSpecialPermissions(p, boo);
    }
    //ADMIN PERMISSIONS
    private void setAdminPermissions(Player p, boolean boo)
    {
        //CHECK BYPASS?OP
        if(!p.hasPermission(Perm._opAdmin.txt) && !p.isOp())
        {
            ConfigurationSection adminPermissions =
                    main.getConfig().getConfigurationSection(Config._adminPermissionList.txt);
            //CHECK ADMIN PERMISSIONS ARE VALID
            if (adminPermissions != null)
            {
                //ITERATE PERMISSIONS & VALUES
                for (String key : adminPermissions.getKeys(false))
                {
                    String permission =
                            adminPermissions.getConfigurationSection(key).get(Config._permission.txt).toString();
                    boolean value =
                            adminPermissions.getConfigurationSection(key).getBoolean(Config._value.txt);
                    //CHECK PERMISSION IS VALID
                    if (permission != null)
                    {
                        boolean v = false;
                        boolean set = false;
                        if (boo && value) { if (!p.hasPermission(permission)) { v = true; set = true; }}
                        if (!boo && value) { if (p.hasPermission(permission)) { v = false; set = true; }}
                        if (set)
                        {
                            User user = main.getLuckPerms().getUserManager().getUser(p.getUniqueId());
                            Node node = Node.builder(permission).value(v).build();
                            user.data().add(node);
                            main.getLuckPerms().getUserManager().saveUser(user);
                        }
                    }
                }
                p.recalculatePermissions();
                p.updateCommands();
            }
        }
    }
    //SET SPECIAL PERMISSIONS
    private void setSpecialPermissions(Player p, boolean boo)
    {
        //CHECK BYPASS?OP
        if(!p.hasPermission(Perm._opAdmin.txt) && !p.isOp())
        {
            ConfigurationSection specialPermissions =
                    main.getConfig().getConfigurationSection(Config._specialPermissionList.txt);
            //CHECK SPECIAL PERMISSIONS ARE VALID
            if (specialPermissions != null
                    && main.getConfig().getBoolean(Config._specialPermissions.txt)
                    && main.getConfig().getStringList(Config._specialRanks.txt)
                    .contains(main.getLuckPerms().getUserManager().getUser(p.getUniqueId())
                            .getPrimaryGroup()))
            {
                //ITERATE PERMISSIONS & VALUES
                for (String key : specialPermissions.getKeys(false))
                {
                    String permission =
                            specialPermissions.getConfigurationSection(key).get(Config._permission.txt).toString();
                    boolean value =
                            specialPermissions.getConfigurationSection(key).getBoolean(Config._value.txt);
                    //CHECK PERMISSION IS VALID
                    if (permission != null)
                    {
                        boolean v = false;
                        boolean set = false;
                        if (boo && value) { if (!p.hasPermission(permission)) { v = true; set = true; }}
                        if (!boo && value) { if (p.hasPermission(permission)) { v = false; set = true; }}
                        if (set)
                        {
                            User user = main.getLuckPerms().getUserManager().getUser(p.getUniqueId());
                            Node node = Node.builder(permission).value(v).build();
                            user.data().add(node);
                            main.getLuckPerms().getUserManager().saveUser(user);
                        }
                    }
                }
                p.recalculatePermissions();
                p.updateCommands();
            }
        }
    }
    /**
     * END PERMISSIONS
     */

    /**
     *
     * COMMANDS
     *
     */
    //VANISH TOGGLE
    private HashSet<UUID> vanishedAdmins = new HashSet<>();
    public void toggleVanish(Player p)
    {

        //might require we save who we hide from so were not stuck hidden next time they log in?

        //CHECK IF PLAYER IS VANISHED
        if (!adminConfig.get(p.getUniqueId()).getConfig().getBoolean(Config._vanishState.txt))
        {
            //ITERATE AND HIDE ADMIN FROM PLAYERS
            for (Player player : main.getServer().getOnlinePlayers())
            {
                //CHECK IF PLAYER IS AN ADMIN
                if (!adminList.contains(player.getUniqueId()))
                {
                    player.hidePlayer(main, p);
                }
            }
            vanishedAdmins.add(p.getUniqueId());
            setState(p, true, State.Vanish);
            setPlayerTabInfo(p, false);
            main.getServer().broadcastMessage(ChatColor.YELLOW + p.getName() + Chat._fakeLogOut.txt);
        } else
        {
            //ITERATE AND UN-HIDE ADMIN FROM PLAYERS
            for (Player player : main.getServer().getOnlinePlayers())
            {
                player.showPlayer(main, p);
            }
            vanishedAdmins.remove(p.getUniqueId());
            setState(p, false, State.Vanish);
            setPlayerTabInfo(p, true);
            main.getServer().broadcastMessage(ChatColor.YELLOW + p.getName() + Chat._fakeLogIn.txt);
        }
    }
    public void vanishJoinedPlayer(Player p)
    {
        //CHECK FOR VANISHED ADMIN
        if (!vanishedAdmins.isEmpty())
        {
            //ITERATE ADMINS AND HIDE FROM PLAYER
            for (UUID uuid : vanishedAdmins)
            {
                Player admin = main.getServer().getPlayer(uuid);
                //CHECK ADMIN IS VALID
                if (admin != null)
                {
                    //CHECK PLAYER IS NOT AN ADMIN
                    if (!adminList.contains(p))
                    {
                        p.hidePlayer(main, admin);
                    }
                }
            }
        }
    }

    //ACTIVATE SPECTATE
    public boolean activateSpectate(Player p, String[] args)
    {
        //CHECK FOR VALID PLAYER

        //CHECK FOR VALID ARGUMENT AND USER INPUT
        if ((!(main.getServer().getPlayer(args[0]) instanceof Player)
                || main.getServer().getPlayer(args[0]) == p
                || args.length > 1))
        {
            p.sendMessage(Chat._targetInvalid.txt);
            return false;
        } else
        {
            //CHECK FOR PLAYER ARGUMENT
            if (main.getServer().getPlayer(args[0]) instanceof Player && args.length == 1)
            {
                p.setGameMode(GameMode.SPECTATOR);
                p.teleport(main.getServer().getPlayer(args[0]));
                p.setSpectatorTarget(main.getServer().getPlayer(args[0]));
                p.getSpectatorTarget().addPassenger(p);
                return true;
            }
        }
        return false;
    }

    /**
     * END COMMANDS
     */

    /**
     *
     * MEMORY HANDLERS
     *
     */
    //ADMIN LIST
    private HashSet<UUID> adminList = new HashSet<>();
    public HashSet<UUID> list() { return adminList; }
    //ADMIN CONFIG
    private HashMap<UUID, ConfigHandler> adminConfig = new HashMap<>();
    public ConfigHandler config(Player p) { return adminConfig.get(p.getUniqueId()); }
    //ADMIN STATE
    public void setState(Player p, Boolean boo, State state)
    {
        if (state == State.Admin)
        {
            adminConfig.get(p.getUniqueId()).getConfig().set(Config._adminState.txt, boo);
            adminConfig.get(p.getUniqueId()).save();
        } else if (state == State.Vanish)
        {
            adminConfig.get(p.getUniqueId()).getConfig().set(Config._vanishState.txt, boo);
            adminConfig.get(p.getUniqueId()).save();
        } else if (state == State.Fly)
        {
            adminConfig.get(p.getUniqueId()).getConfig().set(Config._flyState.txt, boo);
            adminConfig.get(p.getUniqueId()).save();
        }
    }

    private void setPlayerTabInfo(Player receiver, boolean boo) {
        PacketPlayOutPlayerInfo.EnumPlayerInfoAction info;
        if (!boo) { info = PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER; }
        else { info = PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER; }
        Player bukkitPlayer = Bukkit.getPlayer(receiver.getUniqueId());
        EntityPlayer[] playerNMS = new EntityPlayer[1];
        playerNMS[0] = ((CraftPlayer) bukkitPlayer).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(info, playerNMS);
        ((CraftPlayer) receiver).getHandle().playerConnection.sendPacket(packet);
    }
    //INITIALIZE PLAYER
    public void initializeAdmin(Player p)
    {
        String g = main.getLuckPerms().getUserManager().getUser(p.getUniqueId()).getPrimaryGroup();
        Group group = main.getLuckPerms().getGroupManager().getGroup(g);
        //CHECK GROUP IS VALID
        if (group != null)
        {
            //CHECK PLAYER IS ADMIN RANK, ELSE RETURN;
            if (main.getConfig().getStringList(Config._adminRank.txt)
                    .contains(g))
            {
                //ADD TO ADMIN LIST
                if (!adminList.contains(p.getUniqueId())) { adminList.add(p.getUniqueId()); }

                /**
                 *
                 * LOAD/CREATE CONFIG
                 *
                 */
                //LOAD ADMIN CONFIG
                adminConfig.put(p.getUniqueId(), new ConfigHandler(main
                        , Config._playerData.txt
                        , p.getUniqueId().toString()
                        , Config._invConfig.txt));

                //ADD PLAYER NAME TO CONFIG FOLDER IF NONE EXISTS
                File file;
                file = new File(main.getDataFolder(),
                        File.separator + Config._playerData.txt +
                                File.separator + p.getUniqueId().toString() +
                                File.separator + "!." + p.getName() + ".yml");
                if(!file.exists())
                {
                    try
                    {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    } catch(IOException f) { f.printStackTrace(); }
                }

                //CHECK ADMIN CONFIG VALUES, CREATE IF EMPTY
                //ADMIN STATE
                if (!adminConfig.get(p.getUniqueId()).getConfig().isSet(Config._adminState.txt))
                {
                    adminConfig.get(p.getUniqueId()).getConfig().createSection(Config._adminState.txt);
                    adminConfig.get(p.getUniqueId()).getConfig().set(Config._adminState.txt, "false");
                }
                //VANISH STATE
                if (!adminConfig.get(p.getUniqueId()).getConfig().isSet(Config._vanishState.txt))
                {
                    adminConfig.get(p.getUniqueId()).getConfig().createSection(Config._vanishState.txt);
                    adminConfig.get(p.getUniqueId()).getConfig().set(Config._vanishState.txt, "false");
                }
                //FLY STATE
                if (!adminConfig.get(p.getUniqueId()).getConfig().isSet(Config._flyState.txt))
                {
                    adminConfig.get(p.getUniqueId()).getConfig().createSection(Config._flyState.txt);
                    adminConfig.get(p.getUniqueId()).getConfig().set(Config._flyState.txt, "false");
                }
                //SURVIVAL LOCATION
                if (!adminConfig.get(p.getUniqueId()).getConfig().isSet(Config._survivalLocation.txt))
                {
                    adminConfig.get(p.getUniqueId()).getConfig().createSection(Config._survivalLocation.txt);
                    adminConfig.get(p.getUniqueId()).getConfig().set(Config._survivalLocation.txt, p.getLocation());
                }
                //SURVIVAL INV
                if (!adminConfig.get(p.getUniqueId()).getConfig().isSet(Config._survivalInv.txt))
                {
                    //CREATE SECTIONS
                    adminConfig.get(p.getUniqueId()).getConfig().createSection(Config._survivalInv.txt)
                            .createSection(Config._inv.txt);
                    ConfigurationSection survivalInv = adminConfig.get(p.getUniqueId()).getConfig().getConfigurationSection(Config._survivalInv.txt);
                    survivalInv.createSection(Config._armour.txt);
                    survivalInv.createSection(Config._eChest.txt);
                    //SET SECTIONS
                    survivalInv.set(Config._inv.txt, new ItemStack[0]);
                    survivalInv.set(Config._armour.txt, new ItemStack[0]);
                    survivalInv.set(Config._eChest.txt, new ItemStack[0]);
                }
                //BABYSIT INV
                if (!adminConfig.get(p.getUniqueId()).getConfig().isSet(Config._babysitInv.txt))
                {
                    //CREATE SECTIONS
                    adminConfig.get(p.getUniqueId()).getConfig().createSection(Config._babysitInv.txt)
                            .createSection(Config._inv.txt);
                    ConfigurationSection babysitInv = adminConfig.get(p.getUniqueId()).getConfig().getConfigurationSection(Config._babysitInv.txt);
                    babysitInv.createSection(Config._armour.txt);
                    babysitInv.createSection(Config._eChest.txt);
                    //SET SECTIONS
                    babysitInv.set(Config._inv.txt, new ItemStack[0]);
                    babysitInv.set(Config._armour.txt, new ItemStack[0]);
                    babysitInv.set(Config._eChest.txt, new ItemStack[0]);
                }
                //SAVE CONFIG
                adminConfig.get(p.getUniqueId()).save();
                /**
                 * END LOAD/CREATE CONFIG
                 */

                /**
                 *
                 * LOAD CONFIG DATA
                 *
                 */
                //SURVIVAL INV
                ConfigurationSection survivalInv =
                        adminConfig.get(p.getUniqueId()).getConfig().getConfigurationSection(Config._survivalInv.txt);
                ConfigurationSection babysitInv =
                        adminConfig.get(p.getUniqueId()).getConfig().getConfigurationSection(Config._babysitInv.txt);
                try
                {
                    saveInventory(p,
                            ((List<ItemStack>) survivalInv.get(Config._inv.txt)).toArray(new ItemStack[0]),
                            ((List<ItemStack>) survivalInv.get(Config._armour.txt)).toArray(new ItemStack[0]),
                            ((List<ItemStack>) survivalInv.get(Config._eChest.txt)).toArray(new ItemStack[0]),
                            InvType.Survival);
                    saveInventory(p,
                            ((List<ItemStack>) babysitInv.get(Config._inv.txt)).toArray(new ItemStack[0]),
                            ((List<ItemStack>) babysitInv.get(Config._armour.txt)).toArray(new ItemStack[0]),
                            ((List<ItemStack>) babysitInv.get(Config._eChest.txt)).toArray(new ItemStack[0]),
                            InvType.Babysit);
                } catch (ClassCastException ignored) {};
                /**
                 * END LOAD CONFIG DATA
                 */
                /**
                 *
                 * ACTIVATE STATES WITH TRUE VALUES
                 *
                 */
                //CHECK IF SPECIAL PERMISSIONS IS TRUE ALWAYS
                if (main.getConfig().getBoolean(Config._specialPermsAlways.txt)) { setSpecialPermissions(p, false); }
                //CHECK ADMIN STATE
                if (adminConfig.get(p.getUniqueId()).getConfig().getBoolean(Config._adminState.txt)
                        || p.hasPermission(Perm._opAdmin.txt) || p.isOp())
                {
                    //SET FLY STATE
                    if (main.player().admin().config(p).getConfig().getBoolean(Config._flyState.txt))
                    {
                        p.setAllowFlight(true);
                        p.setFlying(true);
                    }
                    //SET VANISH STATE
                    if (adminConfig.get(p.getUniqueId()).getConfig().getBoolean(Config._vanishState.txt))
                    {
                        //ITERATE ONLINE PLAYERS
                        for (Player player : main.getServer().getOnlinePlayers())
                        {
                            //HIDE IF NOT AN ADMIN
                            if (!adminList.contains(player.getUniqueId()))
                            {
                                player.hidePlayer(main, p);
                            }
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                setPlayerTabInfo(p, false);
                            }
                        }.runTaskLater(main, 1L);
                        vanishedAdmins.add(p.getUniqueId());
                        p.sendMessage(Chat._vanishOn.txt);
                    }
                }
            }
        }
    }
    //REMOVE FROM MEMORY
    public void removeFromMemory(Player p) {
        //ADMIN
        adminList.remove(p.getUniqueId());
        adminConfig.remove(p.getUniqueId());
        vanishedAdmins.remove(p.getUniqueId());
        //SURVIVAL INV
        survivalInv.remove(p.getUniqueId());
        survivalArmour.remove(p.getUniqueId());
        survivalEChest.remove(p.getUniqueId());
        //BABYSIT INV
        babysitInv.remove(p.getUniqueId());
        babysitArmour.remove(p.getUniqueId());
        babysitEChest.remove(p.getUniqueId());
    }
    /**
     * MEMORY END
     */
}
