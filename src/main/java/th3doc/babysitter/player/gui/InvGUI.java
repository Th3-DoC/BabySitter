package th3doc.babysitter.player.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import th3doc.babysitter.Main;
import th3doc.babysitter.player.data.InvType;

import java.util.ArrayList;
import java.util.List;

public class InvGUI {

    public InvGUI(Main main, Player viewer, String inv, Player viewee, boolean edit) {
        int size = 0;
        String guiName = "";
        if (inv.equals(InvType.EnderChest.name())) {
            guiName = viewee.getName() + "'s " + InvType.EnderChest.name();
            size = 27;
        } else if (inv.equals(InvType.Inventory.name())) {
            guiName = viewee.getName() + "'s " + InvType.Inventory.name();
            size = 45;
        } else {
            return;
        }
        if (edit)
        {
            main.player().inventory().setEditingInv(viewer, guiName);
        }
        if (inv.equals(InvType.EnderChest.name())) {
            Inventory gui = Bukkit.createInventory(null, size, guiName);
            gui.setContents(viewee.getEnderChest().getContents());
            viewer.openInventory(gui);
        }
        if (inv.equals(InvType.Inventory.name())) {
            Inventory gui = Bukkit.createInventory(null, size, guiName);
            gui.setContents(viewee.getInventory().getContents());
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
