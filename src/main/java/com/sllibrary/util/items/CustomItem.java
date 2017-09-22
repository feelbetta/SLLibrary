package com.sllibrary.util.items;

import com.sllibrary.SLLibrary;
import com.sllibrary.util.items.actions.CustomItemAction;
import com.sllibrary.util.language.Lang;
import com.sllibrary.util.menus.rows.Row;
import com.sllibrary.util.objects.Optionals;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.stream.Collectors;

public class CustomItem extends ItemStack {

    private CustomItemAction customItemAction;

    public CustomItem(ItemStack itemStack) {
        super(itemStack);
    }

    public CustomItem(Material material) {
        super(material);
    }

    public CustomItem withAmount(int amount) {
        this.setAmount(amount);
        return this;
    }

    public CustomItem withName(String name) {
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(Lang.color(name));
        this.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem withLore(String... lore) {
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setLore(Arrays.stream(lore).map(Lang::color).collect(Collectors.toList()));
        this.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem withLore(List<String> lore) {
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setLore(lore.stream().map(Lang::color).collect(Collectors.toList()));
        this.setItemMeta(itemMeta);
        return this;
    }

    public boolean hasDisplayName() {
        return this.getItemMeta().hasDisplayName();
    }

    public boolean hasLore() {
        return this.getItemMeta().hasLore();
    }

    public CustomItem glowing() {
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem unique() {
        return this.setHiddenString(System.currentTimeMillis() + "");
    }

    private CustomItem setHiddenString(String name) {
        StringBuilder itemName = new StringBuilder(ChatColor.WHITE + Lang.toReadable(this.getType().name()));
        ItemMeta meta = this.getItemMeta();
        if (meta.hasDisplayName())
            itemName = new StringBuilder(meta.getDisplayName());
        for (int i = 0; i < name.length(); i++) {
            itemName.append(ChatColor.COLOR_CHAR).append(name.substring(i, i + 1));
        }
        meta.setDisplayName(itemName.toString());
        this.setItemMeta(meta);
        return this;
    }


    public CustomItem withData(int data) {
        this.setDurability((short) data);
        return this;
    }

    public CustomItem withArmorColor(Color color) {
        if (!this.getType().name().toLowerCase().contains("leather_")) {
            return this;
        }
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) this.getItemMeta();
        leatherArmorMeta.setColor(color);
        leatherArmorMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        this.setItemMeta(leatherArmorMeta);
        return this;
    }

    public CustomItem withPotionColor(Color color) {
        if (!this.getType().name().toLowerCase().contains("potion")) {
            return this;
        }
        PotionMeta potionMeta = (PotionMeta) this.getItemMeta();
        potionMeta.setColor(color);
        this.setItemMeta(potionMeta);
        return this;
    }

    public CustomItem withPotionEffect(PotionEffect... potionEffects) {
        if (!this.getType().name().toLowerCase().contains("potion")) {
            return this;
        }
        PotionMeta potionMeta = (PotionMeta) this.getItemMeta();
        Arrays.stream(potionEffects).forEach(potionEffectType -> potionMeta.addCustomEffect(potionEffectType, true));
        this.setItemMeta(potionMeta);
        return this;
    }

    public CustomItem withSkin(Player player) {
        return this.withSkin(player.getName());
    }

    public CustomItem withSkin(String name) {
        if (this.getType() != Material.SKULL_ITEM) {
            return this;
        }
        SkullMeta skullMeta = (SkullMeta) this.getItemMeta();
        skullMeta.setOwner(name);
        this.setItemMeta(skullMeta);
        return this.withData(3);
    }

    public CustomItem withCachedSkin(String textureValue) {
        if (this.getType() != Material.SKULL_ITEM) {
            return this;
        }
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(this);

        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null) {
            compound = new NBTTagCompound();
            nmsStack.setTag(compound);
            compound = nmsStack.getTag();
        }

        NBTTagCompound skullOwner = new NBTTagCompound();
        skullOwner.set("Id", new NBTTagString(UUID.randomUUID().toString()));
        NBTTagCompound properties = new NBTTagCompound();
        NBTTagList textures = new NBTTagList();
        NBTTagCompound value = new NBTTagCompound();
        value.set("Value", new NBTTagString(textureValue));
        textures.add(value);
        properties.set("textures", textures);
        skullOwner.set("Properties", properties);

        compound.set("SkullOwner", skullOwner);
        nmsStack.setTag(compound);

        return new CustomItem(CraftItemStack.asBukkitCopy(nmsStack)).withData(3);
    }

    public CustomItem spawn(Location location) {
        location.getWorld().dropItemNaturally(location.clone().add(0, 0.2, 0), this);
        return this;
    }

    public CustomItem giveTo(Player player) {
        player.getInventory().addItem(this);
        return this;
    }

    public CustomItem giveTo(Player player, Row row, int slot) {
        return this.giveTo(player, row, slot, false);
    }

    public CustomItem giveTo(Player player, Row row, int slot, boolean ignoreFull) {
        PlayerInventory playerInventory = player.getInventory();
        int position = row.getRelativeSlot(slot) - 1;
        if (!ignoreFull) {
            playerInventory.setItem(position + 1, this);
            return this;
        }
        while (playerInventory.getItem(position++) != null) {
            playerInventory.setItem(position, this);
        }
        return this;
    }

    public CustomItem withCustomItemAction(CustomItemAction customItemAction) {
        this.customItemAction = customItemAction;
        JavaPlugin.getPlugin(SLLibrary.class).getCustomItemActionTracker().track(this, this);
        return this;
    }

    public Optional<CustomItemAction> getCustomItemAction() {
        return Optionals.getOptionalOf(this.customItemAction);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ItemStack && this.isSimilar((ItemStack) obj);
    }

    @Override
    public CustomItem clone() {
        return new CustomItem(super.clone());
    }
}