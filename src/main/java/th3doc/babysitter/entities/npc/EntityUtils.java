package th3doc.babysitter.entities.npc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import th3doc.babysitter.Main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

public interface EntityUtils
{
    /**
     * Check If Valid Item Ingredients
     *
     * @param mat
     * @param amt
     * @return
     */
    default Map<Material, Integer> isItemMats(String mat, String amt)
    {
        Map<Material, Integer> itemData = new HashMap<>();
        try { Material material = Material.getMaterial(mat.toUpperCase()); int amount = Integer.parseInt(amt); itemData.put(material, amount); }
        catch(IllegalArgumentException ignored) { return null; }
        return itemData;
    }
    
    /**
     * Serialize Merchant Recipes
     *
     * @param recipes to be serialized
     * @return serialized recipes
     */
    default String[] serializeRecipes(List<MerchantRecipe> recipes)
    {
        //buy[0]:buyAmount[1]:sell1[2]:sell1Amount[3]:sell2[4]:sell2Amount[5]:maxUses[6]:priceMultiplier[7]
        String[] serializedList = new String[recipes.size()];
        if(!recipes.isEmpty())
        {
            for(MerchantRecipe recipe : recipes)
            {
                int index = recipes.indexOf(recipe);
                String buy = recipe.getResult().getType().name();
                int buyAmount = recipe.getResult().getAmount();
                String sell1 = recipe.getIngredients().get(0).getType().name();
                int sell1Amount = recipe.getIngredients().get(0).getAmount();
                String sell2 = "0";
                int sell2Amount = 0;
                if(recipe.getIngredients().size() > 1)
                {
                    sell2 = recipe.getIngredients().get(1).getType().name();
                    sell2Amount = recipe.getIngredients().get(1).getAmount();
                }
                int maxUses = recipe.getMaxUses();
                float priceMultiplier = recipe.getPriceMultiplier();
                
                serializedList[index] = buy + ":" + buyAmount + ":" + sell1 + ":" + sell1Amount + ":" +
                                        sell2 + ":" + sell2Amount + ":" + maxUses + ":" + priceMultiplier;
            }
        }
        return serializedList;
    }
    
    /**
     * De-Serialize Merchant Recipes
     *
     * @param serializedRecipes to be de-serialized
     * @return de-serialized recipes
     */
    default List<MerchantRecipe> deSerializeRecipes(String[] serializedRecipes)
    {
        List<MerchantRecipe> recipes = new ArrayList<>();
        for(String recipe : serializedRecipes)
        {
            String[] args = recipe.split(":");
            final int maxUses;
            final int buyAmount;
            final int sell1Amount;
            try { maxUses = Integer.parseInt(args[6]); buyAmount = Integer.parseInt(args[1]); sell1Amount = Integer.parseInt(args[3]); }
            catch(NumberFormatException ignored) { return null; }
            final Material buyMat;
            final Material sell1;
            try { buyMat = Material.valueOf(args[0].toUpperCase()); sell1 = Material.valueOf(args[2].toUpperCase()); }
            catch(IllegalArgumentException ignored) { return null; }
            
            final ItemStack buying = new ItemStack(buyMat, buyAmount);
            final ItemStack ing1 = new ItemStack(sell1, sell1Amount);
            MerchantRecipe add = new MerchantRecipe(buying, maxUses);
            add.addIngredient(ing1);
            int sell2Amount = 0;
            try { sell2Amount = Integer.parseInt(args[5]); }
            catch(NumberFormatException ignored) {}
            if(sell2Amount > 0)
            {
                try { Material sell2 = Material.valueOf(args[4].toUpperCase()); add.addIngredient(new ItemStack(sell2, sell2Amount)); }
                catch(IllegalArgumentException ignored) {}
            }
            final float priceX;
            try { priceX = Float.parseFloat(args[7]); }
            catch(NumberFormatException ignored) { return null; }
            add.setPriceMultiplier(priceX);
            recipes.add(add);
        }
        return recipes;
    }
    
    /**
     * Serialize Entity
     *
     * @param entity to be serialized
     * @return serialized entity string
     */
    default String serializeEntity(LivingEntity entity)
    {
        //type[0]:uuid[1]:locWorld[2]:locX[3]:locY[4]:locZ[5]
        String type = entity.getType().name();
        String uuid = entity.getUniqueId().toString();
        String world = "";
        if(entity.getLocation().getWorld() != null)
        {
            world = entity.getLocation().getWorld().getName();
        }
        else { return world; }
        String x = String.valueOf(entity.getLocation().getX());
        String y = String.valueOf(entity.getLocation().getY());
        String z = String.valueOf(entity.getLocation().getZ());
        return type + ":" + uuid + ":" + world + ":" + x + ":" + y + ":" + z;
    }
    
    /**
     * De-Serialize Entity
     *
     * @param serializedEntity string formed from serialization
     * @return entity
     */
    default LivingEntity deSerializeEntity(Main main, String serializedEntity)
    {
        String[] args = serializedEntity.split(":");
        EntityType type = EntityType.valueOf(args[0]);
        UUID uuid = UUID.fromString(args[1]);
        World world = main.getServer().getWorld(args[2]);
        Location loc = new Location(world, Float.parseFloat(args[3]),
                                    Float.parseFloat(args[4]),
                                    Float.parseFloat(args[5]));
        org.bukkit.entity.Entity[] entities = loc.getChunk().getEntities();
        for(Entity ent : entities)
        {
            if(ent.getUniqueId().equals(uuid))
            {
                if(type == EntityType.VILLAGER) { return (Villager) ent; }
                else if(type == EntityType.ZOMBIE) { return (Zombie) ent; }
                else if(type == EntityType.SKELETON) { return (Skeleton) ent; }
            }
        }
        return null;
    }
    
    /**
     * Serialize ItemStack[] to Base64
     *
     * @param items to serialize
     * @return base64 string
     */
    default String serializeItemArray(ItemStack[] items){
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            dataOutput.writeInt(items.length);
            
            for (ItemStack item : items) {
                if (item != null) {
                    dataOutput.writeObject(item.serialize());
                } else {
                    dataOutput.writeObject(null);
                }
            }
            
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception ignored) { return null; }
    }
    
    /**
     * De-Serialize ItemStack[] from Base64
     *
     * @param data base64 string to convert
     * @return itemstack[]
     */
    default ItemStack[] deSerializeItemArray(String data) {
        try
        {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];
            
            for (int Index = 0; Index < items.length; Index++) {
                Map<String, Object> stack = (Map<String, Object>) dataInput.readObject();
                
                if (stack != null) {
                    items[Index] = ItemStack.deserialize(stack);
                } else {
                    items[Index] = null;
                }
            }
            
            dataInput.close();
            return items;
        }
        catch (ClassNotFoundException | IOException ignored) { return null; }
    }
    
    /**
     * De-Serialize ItemStack[] from Base64
     *
     * @param data to de-serialize
     * @param start at item index
     * @param finish finish at item index
     * @return itemstack[] from start -> finish
     */
    default ItemStack[] deSerializeItemArray(String data, int start, int finish)
    {
        // This contains contents, armor and offhand (contents are indexes 0 - 35, armor 36 - 39, offhand - 40)
        
        final ItemStack[] savedItems = deSerializeItemArray(data);
        if(savedItems != null)
        {
            ItemStack[] items = new ItemStack[finish];
            if(finish - start >= 0)
                System.arraycopy(savedItems, start, items, start, finish - start);
            return items;
        }
        return null;
    }
}
