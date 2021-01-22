//package th3doc.babysitter.utils.menu;
//
//public class GUI {
//    //VARIABLES
//    final private Players player;
//    final private List<ItemStack> invToEdit;
//    private String inventoryEdit = null;
//    private boolean guiOpen;
//
//
//    //CONSTRUCTOR
//    public GUI(Players player)
//    {
//        this.player = player;
//        this.invToEdit = new ArrayList<>(41);
//        this.guiOpen = false;
//    }
//
//
//    //GETTERS
//    public boolean isEditingInv() { return inventoryEdit != null; }
//    public boolean isGuiOpen() { return guiOpen; }
//    private ItemStack[] getViewedInv(String viewed, String state, String type)
//    {
//        ItemStack[] inventory = new ItemStack[0];
//        if(state.equals(Players.Type.ONLINE.name()))
//        {
//            Player p = player.getMain().getServer().getPlayer(viewed);
//            if(p != null)
//            {
//                if(type.equals(Players.InvType.INVENTORY.name())) { inventory = p.getInventory().getContents(); }
//                else { inventory = p.getEnderChest().getContents(); }
//            }
//        }
//        else if(state.equals(Players.Type.OFFLINE.name()))
//        {
//            String offlineUUID = PlayerConfig.playerList.get(viewed);
//            if(offlineUUID != null)
//            {
//                ConfigHandler config = new ConfigHandler(player.getMain(),
//                                                         Config.PLAYER_FOLDER.txt,
//                                                         offlineUUID,
//                                                         Config.INV_YML.txt);
//                try
//                {
//                    if(type.equals(Players.InvType.INVENTORY.name()))
//                    {
//                        inventory = ((List<String>) config.getConfig()
//                                                          .getConfigurationSection(Config.SURVIVAL_INV.txt)
//                                                          .get(Config.INV.txt)).toArray(new ItemStack[0]);
//                    }
//                    else
//                    {
//                        inventory = ((List<String>) config.getConfig()
//                                                       .getConfigurationSection(Config.SURVIVAL_INV.txt)
//                                                       .get(Config.E_CHEST.txt)).toArray(new ItemStack[0]);
//                    }
//                }
//                catch(ClassCastException ignored) {}
//            }
//        }
//        return inventory;
//    }
//
//
//    //SETTERS
//    public void saveInvToEdit(ItemStack item) { invToEdit.add(item); }
//    public void setEditingInv(String guiName) { inventoryEdit = guiName; }
//    public void closeGui() { this.guiOpen = false; }
//    private Inventory createGUI(String title, String type)
//    {
//        int size = 0;
//        if(type.equals(Players.InvType.INVENTORY.name())) { size = 54; }
//        else { size = 27; }
//        return Bukkit.createInventory(null, size, title);
//
//    }
//    public void openInv(String viewed, String state, String type, boolean edit)
//    {
//        final ItemStack filler = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
//        final ItemStack[] viewedInv = getViewedInv(viewed, state, type);
//        final String title = viewed + "'s " + type;
//        if (edit)
//        {
//            inventoryEdit = title;
//        }
//        Inventory gui = createGUI(title, type);
//        if (type.equals(Players.InvType.SURVIVAL_ENDER_CHEST.name())) {
//            gui.setContents(viewedInv);
//        }
//        else
//        {
//            //0-8
//            gui.setItem(0, viewedInv[39]); gui.setItem(1, filler); gui.setItem(2, filler);
//            gui.setItem(3, filler); gui.setItem(4, viewedInv[40]); gui.setItem(5, filler);
//            gui.setItem(6, filler); gui.setItem(7, filler); gui.setItem(8, viewedInv[37]);
//            //9-17
//            gui.setItem(9, viewedInv[38]); gui.setItem(10, filler); gui.setItem(11, filler);
//            gui.setItem(12, filler); gui.setItem(13, filler); gui.setItem(14, filler);
//            gui.setItem(15, filler); gui.setItem(16, filler); gui.setItem(17, viewedInv[36]);
//            for(int i=0;i<35;i++) { gui.setItem(i+18, viewedInv[i]); }
//
//
//        }
//        player.getPlayer().openInventory(gui);
//        this.guiOpen = true;
//    }
//
//
//    //SAVE INVENTORY
//    public void saveInvEdit(String invTitle)
//    {
//        //ARE WE EDITING AN INVENTORY
//        if(inventoryEdit != null)
//        {
//            final String[] title = invTitle.split(" ");
//            final String nameStart = StringUtils.chop(title[0]);
//            final String name = StringUtils.chop(nameStart);
//            player.message(Arrays.toString(title) + name);//REMOVE
//            final ItemStack[] saveInv = invToEdit.toArray(new ItemStack[0]);
//            player.message("PlayerInv : invEdit != null");//REMOVE
//            if(invTitle.equals(inventoryEdit))
//            {
//                player.message("title == invEdit");//REMOVE
//                final Player saveTo = player.getMain().getServer().getPlayer(name);
//                if(saveTo != null && !saveTo.hasPermission(Perm._invBypass.txt))
//                {
//                    player.message("Not NUll Player");
//                    if(title[1].equals(Players.InvType.INVENTORY.name()))
//                    {player.message("inv"); saveTo.getInventory().setContents(saveInv); }
//                    else if(title[1].equals(Players.InvType.SURVIVAL_ENDER_CHEST.name()))
//                    {player.message("echest"); saveTo.getEnderChest().setContents(saveInv); }
//                    invToEdit.clear();
//                }
//                else if(PlayerConfig.playerList.containsKey(name))
//                {
//                    player.message("offline player");
//                    String offlineUUID = PlayerConfig.playerList.get(name);
//                    player.message(offlineUUID);
//                    new BukkitRunnable()
//                    {
//
//                        @Override
//                        public void run()
//                        {
//                            ConfigHandler config = new ConfigHandler(player.getMain(),
//                                                                     Config.PLAYER_FOLDER.txt,
//                                                                     offlineUUID,
//                                                                     Config.INV_YML.txt);
//                            if(!config.getConfig().getBoolean(Config.INV_BYPASS.txt))
//                            {
//                                player.message("inv bypass false");
//                                if(title[1].equals(Players.InvType.INVENTORY.name()))
//                                {
//                                    player.message("inv");
//                                    config.getConfig().getConfigurationSection(Config.SURVIVAL_INV.txt)
//                                          .set(Config.INV.txt, saveInv);
//                                    config.getConfig().set(Config.EDITED.txt, true);
//                                    config.save();
//                                }
//                                else if(title[1].equals(Players.InvType.SURVIVAL_ENDER_CHEST.name()))
//                                {
//                                    player.message("echest");
//                                    config.getConfig().getConfigurationSection(Config.SURVIVAL_INV.txt)
//                                          .set(Config.E_CHEST.txt, saveInv);
//                                    config.getConfig().set(Config.EDITED.txt, true);
//                                    config.save();
//                                }
//                                invToEdit.clear();
//                            }
//                        }
//
//                    }.runTaskAsynchronously(player.getMain());
//                }
//            }
//        }
//        inventoryEdit = null;
//        player.message("invEdit null");
//    }
//}
