package org.dawnoftimevillage.village;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

public class VillageInventory {
    public static int MAX_SIZE = 1000;
    private HashMap<Item, Integer> inventory = new HashMap<>();

    public VillageInventory() {
    }

    public VillageInventory(CompoundTag tag) {
        read(tag);
    }

    public void saveNBT(CompoundTag tag) {
        ListTag inventoryTag = new ListTag();
        this.inventory.forEach((key, value) -> {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("item", ForgeRegistries.ITEMS.getKey(key).toString());
            entryTag.putInt("amount", value);
            inventoryTag.add(entryTag);
        });
        tag.put("VillageInventory", inventoryTag);
    }

    public void read(CompoundTag tag) {
        ListTag inventoryTag = tag.getList("VillageInventory", 10);
        for(int i = 0; i < inventoryTag.size(); ++i) {
            CompoundTag entryTag = inventoryTag.getCompound(i);
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entryTag.getString("item")));
            this.inventory.put(item, entryTag.getInt("amount"));
        }
    }

    public <T extends Item> boolean hasAny(T item) {
        return this.inventory.containsKey(item);
    }

    public <T extends Item> boolean hasEnough(T item, int amount) {
        return !((amount <= 0) || !hasAny(item) || (this.inventory.get(item) < amount));
    }

    public <T extends Item> int get(T item) {
        return hasAny(item) ? this.inventory.get(item) : 0;
    }

    public <T extends Item> boolean add(T item) {
        return add(item, 1);
    }

    public <T extends Item> boolean add(T item, int amount) {
        if (amount <= 0 || amount > MAX_SIZE) {
            return false;
        } else {
            if (hasAny(item)) {
                this.inventory.put(item, this.inventory.get(item) + amount);
            } else {
                this.inventory.put(item, amount);
            }
            return true;
        }
    }

    public <T extends Item> boolean remove(T item, int amount) {
        if (amount <= 0 || amount > MAX_SIZE) {
            return false;
        } else {
            if (hasAny(item)) {
                this.inventory.put(item, this.inventory.get(item) - amount);
                if (this.inventory.get(item) <= 0) {
                    removeAll(item);
                }
            }
            return true;
        }
    }

    public <T extends Item> void removeAll(T item) {
        this.inventory.remove(item);
    }
}
