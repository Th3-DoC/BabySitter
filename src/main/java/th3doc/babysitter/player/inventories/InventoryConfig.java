package th3doc.babysitter.player.inventories;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;
import th3doc.babysitter.player.PlayerHandler;
import th3doc.babysitter.player.data.InvType;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.States;

import java.util.Arrays;
import java.util.List;

public class InventoryConfig {
    
    //VARIABLES
    private final PlayerHandler player;
    private final boolean isInvBypass;
    private ItemStack[] survivalInv;
    private ItemStack[] survivalArmour;
    private ItemStack[] survivalEChest;
    private ItemStack[] babysitInv;
    private ItemStack[] babysitArmour;
    private ItemStack[] babysitEChest;
    
    
    //CONSTRUCTOR
    public InventoryConfig(PlayerHandler player)
    {
        this.player = player;
        //LOAD INVENTORY CONFIG
        ConfigHandler config = new ConfigHandler(player.getMain()
                , Config._playerData.txt
                , player.getUUID().toString()
                , Config._invConfig.txt);
        //CHECK CONFIG VALUES, CREATE IF EMPTY
        boolean configSave = false;
        //inv bypass
        if(!config.getConfig().isSet(Config._invBypass.txt))
        {
            config.getConfig().createSection(Config._invBypass.txt);
            config.getConfig().set(Config._invBypass.txt, false);
            configSave = true;
        }
        //edited
        if(!config.getConfig().isSet(Config._edited.txt))
        {
            config.getConfig().createSection(Config._edited.txt);
            config.getConfig().set(Config._edited.txt, false);
            configSave = true;
        }
        //survival inv
        if(!config.getConfig().isSet(Config._survivalInv.txt))
        {
            //create sections
            config.getConfig().createSection(Config._survivalInv.txt)
                  .createSection(Config._inv.txt);
            ConfigurationSection survivalInv = config.getConfig().getConfigurationSection(Config._survivalInv.txt);
            survivalInv.createSection(Config._armour.txt);
            survivalInv.createSection(Config._eChest.txt);
            //set sections
            survivalInv.set(Config._inv.txt, player.getPlayer().getInventory().getContents());
            survivalInv.set(Config._armour.txt, player.getPlayer().getInventory().getArmorContents());
            survivalInv.set(Config._eChest.txt, player.getPlayer().getEnderChest().getContents());
            configSave = true;
        }
        //babysit inv
        if(!config.getConfig().isSet(Config._babysitInv.txt))
        {
            //create sections
            config.getConfig().createSection(Config._babysitInv.txt)
                  .createSection(Config._inv.txt);
            ConfigurationSection babysitInv = config.getConfig().getConfigurationSection(Config._babysitInv.txt);
            babysitInv.createSection(Config._armour.txt);
            babysitInv.createSection(Config._eChest.txt);
            //SET SECTIONS
            babysitInv.set(Config._inv.txt, new ItemStack[0]);
            babysitInv.set(Config._armour.txt, new ItemStack[0]);
            babysitInv.set(Config._eChest.txt, new ItemStack[0]);
            configSave = true;
        }
        //SAVE CONFIG
        if(configSave) { config.save(); }
        this.isInvBypass = config.getConfig().getBoolean(Config._invBypass.txt);
        //LOAD CONFIG VALUES TO MEM
        //SURVIVAL INV
        ConfigurationSection survivalInvConfig =
                config.getConfig().getConfigurationSection(Config._survivalInv.txt);
        ConfigurationSection babysitInvConfig =
                config.getConfig().getConfigurationSection(Config._babysitInv.txt);
        try
        {
            saveInventory(((List<ItemStack>) survivalInvConfig.get(Config._inv.txt)).toArray(new ItemStack[0]),
                          ((List<ItemStack>) survivalInvConfig.get(Config._armour.txt)).toArray(new ItemStack[0]),
                          ((List<ItemStack>) survivalInvConfig.get(Config._eChest.txt)).toArray(new ItemStack[0]),
                          InvType.Survival);
            if(player.isAdmin())
            {
                saveInventory(((List<ItemStack>) babysitInvConfig.get(Config._inv.txt)).toArray(new ItemStack[0]),
                              ((List<ItemStack>) babysitInvConfig.get(Config._armour.txt)).toArray(new ItemStack[0]),
                              ((List<ItemStack>) babysitInvConfig.get(Config._eChest.txt)).toArray(new ItemStack[0]),
                              InvType.Babysit);
            }
        }
        catch(ClassCastException ignored) {}
    }
    
    
    //GETTERS
    public boolean isInvBypass() { return isInvBypass; }
    public ConfigHandler getOfflineConfig(String uuid)
    {
        final ConfigHandler[] config = new ConfigHandler[1];
        new BukkitRunnable()
        {
    
            @Override
            public void run()
            {
                config[0] = new ConfigHandler(JavaPlugin.getPlugin(Main.class),
                                              Config._playerData.txt,
                                              uuid,
                                              Config._invConfig.txt);
            }
        }.runTaskAsynchronously(player.getMain());
        return config[0];
    }
    public void getInventory(InvType type)
    {
        ItemStack[] invContents;
        ItemStack[] armourContents;
        ItemStack[] eChestContents;
        if(type == InvType.Survival)
        {
            invContents = this.survivalInv;
            armourContents = this.survivalArmour;
            eChestContents = this.survivalEChest;
        }
        else if(type == InvType.Babysit)
        {
            invContents = this.babysitInv;
            armourContents = this.babysitArmour;
            eChestContents = this.babysitEChest;
        }
        else { return; }
        if(invContents != null && Arrays.stream(invContents).count() > 0)
        {
            player.getPlayer().getInventory().setContents(invContents);
        } else { player.getPlayer().getInventory().setContents(new ItemStack[0]); }
        if(armourContents != null && Arrays.stream(armourContents).count() > 0)
        {
            player.getPlayer().getInventory().setArmorContents(armourContents);
        } else { player.getPlayer().getInventory().setArmorContents(new ItemStack[0]); }
        if(eChestContents != null && Arrays.stream(eChestContents).count() > 0)
        {
            player.getPlayer().getEnderChest().setContents(eChestContents);
        } else { player.getPlayer().getEnderChest().setContents(new ItemStack[0]); }
    }
    
    
    //SETTERS
    public void saveInventory(ItemStack[] inv, ItemStack[] armour, ItemStack[] eChest, InvType type)
    {
        if(type == InvType.Survival)
        {
            this.survivalInv = inv;
            this.survivalArmour = armour;
            this.survivalEChest = eChest;
        }
        else if(type == InvType.Babysit)
        {
            this.babysitInv = inv;
            this.babysitArmour = armour;
            this.babysitEChest = eChest;
        }
    }
    
    
    //SAVE
    public void save()
    {
        ConfigHandler config = new ConfigHandler(player.getMain(),
                                                 Config._playerData.txt,
                                                 player.getUUID().toString(),
                                                 Config._invConfig.txt);
        boolean configSave = false;
        if(player.isAdmin())
        {
            ConfigurationSection babysitInvConfig =
                    config.getConfig().getConfigurationSection(Config._babysitInv.txt);
            babysitInvConfig.set(Config._inv.txt, babysitInv);
            babysitInvConfig.set(Config._armour.txt, babysitArmour);
            babysitInvConfig.set(Config._eChest.txt, babysitEChest);
            configSave = true;
        }
        if((player.isAdmin() && !player.admin().getConfig().getState(States.ADMIN)) ||
           !player.isAdmin())
        {
            ConfigurationSection survivalInvConfig =
                    config.getConfig().getConfigurationSection(Config._survivalInv.txt);
            survivalInvConfig.set(Config._inv.txt, survivalInv);
            survivalInvConfig.set(Config._armour.txt, survivalArmour);
            survivalInvConfig.set(Config._eChest.txt, survivalEChest);
            configSave = true;
        }
        if(player.getPlayer().hasPermission(Perm._invBypass.txt) && !isInvBypass)
        {
            config.getConfig().set(Config._invBypass.txt, true);
            configSave = true;
        }
        else if(!player.getPlayer().hasPermission(Perm._invBypass.txt) && isInvBypass)
        {
            config.getConfig().set(Config._invBypass.txt, false);
            configSave = true;
        }
        if(configSave) { config.save(); }
    }
}
