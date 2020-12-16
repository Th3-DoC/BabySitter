package th3doc.babysitter.player.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;
import th3doc.babysitter.player.PlayerConfig;
import th3doc.babysitter.player.data.InvType;
import th3doc.babysitter.player.data.PlayerType;

import java.util.ArrayList;
import java.util.List;

public class InvGUI {

    public InvGUI(Main main, Player viewer, String state, String inv, String viewee, boolean edit) {
        int size = 0;
        String guiName = "";
        if (inv.equals(InvType.EnderChest.name())) {
            guiName = viewee + "'s " + InvType.EnderChest.name();
            size = 27;
        } else if (inv.equals(InvType.Inventory.name())) {
            guiName = viewee + "'s " + InvType.Inventory.name();
            size = 45;
        } else {
            return;
        }
        if (edit)
        {
            viewer.sendMessage("inv gui edit true");
            main.getPlayer(viewer.getUniqueId()).inventory().setEditingInv(guiName);
        }
        final ItemStack[][] inventory = new ItemStack[1][1];
        final ItemStack[][] eChest = new ItemStack[1][1];
        if(state.equals(PlayerType.Online.name()))
        {
            inventory[0] = main.getServer().getPlayer(viewee).getInventory().getContents();
            eChest[0] = main.getServer().getPlayer(viewee).getEnderChest().getContents();
        }
        else if(state.equals(PlayerType.Offline.name()))
        {
            String offlineUUID = PlayerConfig.playerList.get(viewee);
            new BukkitRunnable()
            {
    
                @Override
                public void run()
                {
                    try
                    {
                        ConfigHandler config = new ConfigHandler(main,
                                                                 Config._playerData.txt,
                                                                 offlineUUID,
                                                                 Config._invConfig.txt);
                        inventory[0] = ((List<String>) config.getConfig()
                                                             .getConfigurationSection(Config._survivalInv.txt)
                                                             .get(Config._inv.txt)).toArray(new ItemStack[0]);
    
                        eChest[0] = ((List<String>) config.getConfig()
                                                          .getConfigurationSection(Config._survivalInv.txt)
                                                          .get(Config._eChest.txt)).toArray(new ItemStack[0]);
                    }
                    catch(ClassCastException ignored) {}
                }
            
            }.runTaskAsynchronously(main);
        }
        else { return; }
        if (inv.equals(InvType.EnderChest.name())) {
            Inventory gui = Bukkit.createInventory(null, size, guiName);
            gui.setContents(eChest[0]);
            viewer.openInventory(gui);
        }
        else if (inv.equals(InvType.Inventory.name())) {
            Inventory gui = Bukkit.createInventory(null, size, guiName);
            gui.setContents(inventory[0]);
            ItemStack info = new ItemStack(Material.ENDER_EYE, 1);
            ItemMeta info_meta = info.getItemMeta();
            List<String> infolore = new ArrayList<>();
            infolore.add("HotBar");
            infolore.add("Inventory");
            infolore.add("Armour");
            info_meta.setLore(infolore);
            info_meta.setDisplayName("Slot Order");
            info.setItemMeta(info_meta);
            gui.setItem(44, info);
            viewer.openInventory(gui);
        }
    }
}
