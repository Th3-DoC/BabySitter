package th3doc.babysitter.player.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import th3doc.babysitter.Main;
import th3doc.babysitter.config.Config;
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
            main.player().inventory().setEditingInv(viewer, guiName);
        }
        ItemStack[] inventory = new ItemStack[0];
        ItemStack[] eChest = new ItemStack[0];
        if(state.equals(PlayerType.Online.name()))
        {
            inventory = main.getServer().getPlayer(viewee).getInventory().getContents();
            eChest = main.getServer().getPlayer(viewee).getEnderChest().getContents();
        }
        if(state.equals(PlayerType.Offline.name()))
        {
            try
            {
                inventory = ((List<String>) main.player().inventory().config(viewee).getConfig()
                        .getConfigurationSection(Config._survivalInv.txt)
                        .get(Config._inv.txt)).toArray(new ItemStack[0]);
                
                eChest = ((List<String>) main.player().inventory().config(viewee).getConfig()
                        .getConfigurationSection(Config._survivalInv.txt)
                        .get(Config._eChest.txt)).toArray(new ItemStack[0]);
            }
            catch(ClassCastException ignored) {}
        }
        if (inv.equals(InvType.EnderChest.name())) {
            Inventory gui = Bukkit.createInventory(null, size, guiName);
            gui.setContents(eChest);
            viewer.openInventory(gui);
        }
        if (inv.equals(InvType.Inventory.name())) {
            Inventory gui = Bukkit.createInventory(null, size, guiName);
            gui.setContents(inventory);
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
