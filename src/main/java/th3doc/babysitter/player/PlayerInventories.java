package th3doc.babysitter.player;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;
import th3doc.babysitter.player.data.InvType;
import th3doc.babysitter.player.data.Perm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerInventories {
    
    //CONSTRUCTOR
    private Main main;
    public PlayerInventories(Main main) { this.main = main; }
    
    //SURVIVAL
    private HashMap<UUID, ItemStack[]> survivalInv = new HashMap<>();
    private HashMap<UUID, ItemStack[]> survivalArmour = new HashMap<>();
    private HashMap<UUID, ItemStack[]> survivalEChest = new HashMap<>();
    //BABYSIT
    private HashMap<UUID, ItemStack[]> babysitInv = new HashMap<>();
    private HashMap<UUID, ItemStack[]> babysitArmour = new HashMap<>();
    private HashMap<UUID, ItemStack[]> babysitEChest = new HashMap<>();
    
    //INITIALIZE
    public void initialize(Player p)
    {
        if(!p.hasPermission(Perm._invBypass.txt))
        {
            /**
             *
             * LOAD/CREATE CONFIG
             *
             */
            //LOAD INVENTORY CONFIG
            ConfigHandler config =
                    new ConfigHandler(main, Config._playerData.txt, p.getUniqueId().toString(), Config._invConfig.txt);
            
            //CHECK CONFIG VALUES, CREATE IF EMPTY
            //SURVIVAL INVENTORY
            if(!config.getConfig().isSet(Config._survivalInv.txt))
            {
                //CREATE SECTIONS
                config.getConfig().createSection(Config._survivalInv.txt)
                        .createSection(Config._inv.txt);
                ConfigurationSection survivalInv = config.getConfig().getConfigurationSection(Config._survivalInv.txt);
                survivalInv.createSection(Config._armour.txt);
                survivalInv.createSection(Config._eChest.txt);
                //SET SECTIONS
                survivalInv.set(Config._inv.txt, new ItemStack[0]);
                survivalInv.set(Config._armour.txt, new ItemStack[0]);
                survivalInv.set(Config._eChest.txt, new ItemStack[0]);
            }
            //BABYSIT INVENTORY
            if(!config.getConfig().isSet(Config._babysitInv.txt))
            {
                //CREATE SECTIONS
                config.getConfig().createSection(Config._babysitInv.txt)
                        .createSection(Config._inv.txt);
                ConfigurationSection babysitInv = config.getConfig().getConfigurationSection(Config._babysitInv.txt);
                babysitInv.createSection(Config._armour.txt);
                babysitInv.createSection(Config._eChest.txt);
                //SET SECTIONS
                babysitInv.set(Config._inv.txt, new ItemStack[0]);
                babysitInv.set(Config._armour.txt, new ItemStack[0]);
                babysitInv.set(Config._eChest.txt, new ItemStack[0]);
            }
            //SAVE CONFIG
            config.save();
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
                    config.getConfig().getConfigurationSection(Config._survivalInv.txt);
            ConfigurationSection babysitInv =
                    config.getConfig().getConfigurationSection(Config._babysitInv.txt);
            try
            {
                saveInventory(p,
                        ((List<ItemStack>) survivalInv.get(Config._inv.txt)).toArray(new ItemStack[0]),
                        ((List<ItemStack>) survivalInv.get(Config._armour.txt)).toArray(new ItemStack[0]),
                        ((List<ItemStack>) survivalInv.get(Config._eChest.txt)).toArray(new ItemStack[0]),
                        InvType.Survival);
                if(main.player().isAdmin(p.getName()))
                {
                    saveInventory(p,
                            ((List<ItemStack>) babysitInv.get(Config._inv.txt)).toArray(new ItemStack[0]),
                            ((List<ItemStack>) babysitInv.get(Config._armour.txt)).toArray(new ItemStack[0]),
                            ((List<ItemStack>) babysitInv.get(Config._eChest.txt)).toArray(new ItemStack[0]),
                            InvType.Babysit);
                }
            }
            catch(ClassCastException ignored) {}
            /**
             * END LOAD CONFIG DATA
             */
        }
    }
    
    //INVENTORY EDITING
    private HashMap<String, String> inventoryEdit = new HashMap<>();
    public void setEditingInv(Player p, String guiName) { inventoryEdit.put(p.getName(), guiName); }
    
    //INVENTORY CHECK FOR SAVE
    private HashMap<String, ItemStack[]> invToCheck = new HashMap<>();
    public ItemStack[] invToCheck(Player p) { return invToCheck.get(p.getName()); }
    public boolean isCheckingInv(Player p) { return invToCheck.containsKey(p.getName()); }
    
    //SAVE EDITED INVENTORY
    public void saveInvEdit(Player p, InventoryCloseEvent e)
    {
        if(!p.hasPermission(Perm._invBypass.txt))
        {
            //ARE WE EDITING AN INVENTORY
            if(inventoryEdit.containsKey(p.getName()))
            {
                String[] title = e.getView().getTitle().split(" ");
                if(main.getServer().getPlayer(title[0]) instanceof Player)
                {
                    Player player = main.getServer().getPlayer(title[0]);
                    assert player != null;
                    if(e.getView().getTitle().equals(inventoryEdit.get(p.getName())))
                    {
                        if(inventoryEdit.get(p.getName()).contains(InvType.Inventory.name()))
                        {
                            ItemStack[] event_inv = e.getInventory().getContents();
                            List<ItemStack> save_inv = new ArrayList<>();
                            for(ItemStack item : event_inv)
                            {
                                if(save_inv.size() < 41) { save_inv.add(item); }
                                else { break; }
                            }
                            ItemStack[] new_inv = save_inv.toArray(new ItemStack[0]);
                            player.getInventory().setContents(new_inv);
                        }
                        if(inventoryEdit.get(p.getName()).contains(InvType.EnderChest.name()))
                        {
                            player.getEnderChest().setContents(e.getInventory().getContents());
                        }
                        inventoryEdit.remove(p.getName());
                    }
                }
            }
        }
    }
    
    //SAVE/SET INVENTORIES
    public void saveInventory(Player p, ItemStack[] inv, ItemStack[] armour, ItemStack[] eChest, InvType type)
    {
        HashMap<UUID, ItemStack[]> invContents;
        HashMap<UUID, ItemStack[]> armourContents;
        HashMap<UUID, ItemStack[]> eChestContents;
        String configName = "";
        if(type == InvType.Survival)
        {
            invContents = survivalInv;
            armourContents = survivalArmour;
            eChestContents = survivalEChest;
            configName = Config._survivalInv.txt;
        }
        else if(type == InvType.Babysit)
        {
            invContents = babysitInv;
            armourContents = babysitArmour;
            eChestContents = babysitEChest;
            configName = Config._babysitInv.txt;
        }
        else { return; }
        invContents.put(p.getUniqueId(), inv);
        armourContents.put(p.getUniqueId(), armour);
        eChestContents.put(p.getUniqueId(), eChest);
        //SAVE CONFIG
        ConfigHandler config =
                new ConfigHandler(main, Config._playerData.txt, p.getUniqueId().toString(), Config._invConfig.txt);
        ConfigurationSection configSection =
                config.getConfig().getConfigurationSection(configName);
        configSection.set(Config._inv.txt, inv);
        configSection.set(Config._armour.txt, armour);
        configSection.set(Config._eChest.txt, eChest);
        config.save();
    }
    public void saveInventory(Player p, ItemStack[] inv) { invToCheck.put(p.getName(), inv); }
    public void getInventory(Player p, InvType type)
    {
        HashMap<UUID, ItemStack[]> invContents;
        HashMap<UUID, ItemStack[]> armourContents;
        HashMap<UUID, ItemStack[]> eChestContents;
        if(type == InvType.Survival)
        {
            invContents = survivalInv;
            armourContents = survivalArmour;
            eChestContents = survivalEChest;
        }
        else if(type == InvType.Babysit)
        {
            invContents = babysitInv;
            armourContents = babysitArmour;
            eChestContents = babysitEChest;
        }
        else { return; }
        p.getInventory().setContents(invContents.get(p.getUniqueId()));
        p.getInventory().setArmorContents(armourContents.get(p.getUniqueId()));
        p.getEnderChest().setContents(eChestContents.get(p.getUniqueId()));
    }
    
    //MEMORY DUMP
    public void memoryDump(Player p)
    {
        //SURVIVAL INV
        survivalInv.remove(p.getUniqueId());
        survivalArmour.remove(p.getUniqueId());
        survivalEChest.remove(p.getUniqueId());
        //BABYSIT INV
        babysitInv.remove(p.getUniqueId());
        babysitArmour.remove(p.getUniqueId());
        babysitEChest.remove(p.getUniqueId());
    }
}
