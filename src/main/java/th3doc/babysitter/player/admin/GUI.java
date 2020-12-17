package th3doc.babysitter.player.admin;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import th3doc.babysitter.config.Config;
import th3doc.babysitter.config.ConfigHandler;
import th3doc.babysitter.player.PlayerConfig;
import th3doc.babysitter.player.PlayerHandler;
import th3doc.babysitter.player.data.InvType;
import th3doc.babysitter.player.data.Perm;
import th3doc.babysitter.player.data.PlayerType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GUI {
    //VARIABLES
    final private PlayerHandler player;
    final private List<ItemStack> invToEdit;
    private String inventoryEdit = null;
    private boolean guiOpen;
    
    
    //CONSTUCTOR
    public GUI(PlayerHandler player)
    {
        this.player = player;
        this.invToEdit = new ArrayList<>(41);
        this.guiOpen = false;
    }
    
    
    //GETTERS
    public boolean isEditingInv() { return inventoryEdit != null; }
    public boolean isGuiOpen() { return guiOpen; }
    private ItemStack[] getViewedInv(String viewed, String state, String type)
    {
        ItemStack[] inventory = new ItemStack[0];
        if(state.equals(PlayerType.Online.name()))
        {
            Player p = player.getMain().getServer().getPlayer(viewed);
            if(p != null)
            {
                if(type.equals(InvType.Inventory.name())) { inventory = p.getInventory().getContents(); }
                else { inventory = p.getEnderChest().getContents(); }
            }
        }
        else if(state.equals(PlayerType.Offline.name()))
        {
            String offlineUUID = PlayerConfig.playerList.get(viewed);
            if(offlineUUID != null)
            {
                ConfigHandler config = new ConfigHandler(player.getMain(),
                                                         Config._playerData.txt,
                                                         offlineUUID,
                                                         Config._invConfig.txt);
                try
                {
                    if(type.equals(InvType.Inventory.name()))
                    {
                        inventory = ((List<String>) config.getConfig()
                                                          .getConfigurationSection(Config._survivalInv.txt)
                                                          .get(Config._inv.txt)).toArray(new ItemStack[0]);
                    }
                    else
                    {
                        inventory = ((List<String>) config.getConfig()
                                                       .getConfigurationSection(Config._survivalInv.txt)
                                                       .get(Config._eChest.txt)).toArray(new ItemStack[0]);
                    }
                }
                catch(ClassCastException ignored) {}
            }
        }
        return inventory;
    }
    
    
    //SETTERS
    public void saveInvToEdit(ItemStack item) { invToEdit.add(item); }
    public void setEditingInv(String guiName) { inventoryEdit = guiName; }
    public void closeGui() { this.guiOpen = false; }
    private Inventory createGUI(String title, String type)
    {
        int size = 0;
        if(type.equals(InvType.Inventory.name())) { size = 54; }
        else { size = 27; }
        return Bukkit.createInventory(null, size, title);
        
    }
    public void openInv(String viewed, String state, String type, boolean edit)
    {
        final ItemStack filler = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        final ItemStack[] viewedInv = getViewedInv(viewed, state, type);
        final String title = viewed + "'s " + type;
        if (edit)
        {
            inventoryEdit = title;
        }
        Inventory gui = createGUI(title, type);
        if (type.equals(InvType.EnderChest.name())) {
            gui.setContents(viewedInv);
        }
        else
        {
            //0-8
            gui.setItem(0, viewedInv[39]); gui.setItem(1, filler); gui.setItem(2, filler);
            gui.setItem(3, filler); gui.setItem(4, viewedInv[40]); gui.setItem(5, filler);
            gui.setItem(6, filler); gui.setItem(7, filler); gui.setItem(8, viewedInv[37]);
            //9-17
            gui.setItem(9, viewedInv[38]); gui.setItem(10, filler); gui.setItem(11, filler);
            gui.setItem(12, filler); gui.setItem(13, filler); gui.setItem(14, filler);
            gui.setItem(15, filler); gui.setItem(16, filler); gui.setItem(17, viewedInv[36]);
            for(int i=0;i<35;i++) { gui.setItem(i+18, viewedInv[i]); }
    
    
        }
        player.getPlayer().openInventory(gui);
        this.guiOpen = true;
    }
    
    
    //SAVE INVENTORY
    public void saveInvEdit(String invTitle)
    {
        //ARE WE EDITING AN INVENTORY
        if(inventoryEdit != null)
        {
            final String[] title = invTitle.split(" ");
            final String nameStart = StringUtils.chop(title[0]);
            final String name = StringUtils.chop(nameStart);
            player.message(Arrays.toString(title) + name);//REMOVE
            final ItemStack[] saveInv = invToEdit.toArray(new ItemStack[0]);
            player.message("PlayerInv : invEdit != null");//REMOVE
            if(invTitle.equals(inventoryEdit))
            {
                player.message("title == invEdit");//REMOVE
                final Player saveTo = player.getMain().getServer().getPlayer(name);
                if(saveTo != null && !saveTo.hasPermission(Perm._invBypass.txt))
                {
                    player.message("Not NUll Player");
                    if(title[1].equals(InvType.Inventory.name()))
                    {player.message("inv"); saveTo.getInventory().setContents(saveInv); }
                    else if(title[1].equals(InvType.EnderChest.name()))
                    {player.message("echest"); saveTo.getEnderChest().setContents(saveInv); }
                    invToEdit.clear();
                }
                else if(PlayerConfig.playerList.containsKey(name))
                {
                    player.message("offline player");
                    String offlineUUID = PlayerConfig.playerList.get(name);
                    player.message(offlineUUID);
                    new BukkitRunnable()
                    {
                    
                        @Override
                        public void run()
                        {
                            ConfigHandler config = new ConfigHandler(player.getMain(),
                                                                     Config._playerData.txt,
                                                                     offlineUUID,
                                                                     Config._invConfig.txt);
                            if(!config.getConfig().getBoolean(Config._invBypass.txt))
                            {
                                player.message("inv bypass false");
                                if(title[1].equals(InvType.Inventory.name()))
                                {
                                    player.message("inv");
                                    config.getConfig().getConfigurationSection(Config._survivalInv.txt)
                                          .set(Config._inv.txt, saveInv);
                                    config.getConfig().set(Config._edited.txt, true);
                                    config.save();
                                }
                                else if(title[1].equals(InvType.EnderChest.name()))
                                {
                                    player.message("echest");
                                    config.getConfig().getConfigurationSection(Config._survivalInv.txt)
                                          .set(Config._eChest.txt, saveInv);
                                    config.getConfig().set(Config._edited.txt, true);
                                    config.save();
                                }
                                invToEdit.clear();
                            }
                        }
                    
                    }.runTaskAsynchronously(player.getMain());
                }
            }
        }
        inventoryEdit = null;
        player.message("invEdit null");
    }
}
