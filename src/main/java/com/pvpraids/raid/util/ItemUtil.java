package com.pvpraids.raid.util;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@UtilityClass
public class ItemUtil {
	public ItemStack setName(ItemStack stack, String name) {
		return setRawName(stack, ChatColor.RESET + name);
	}

	public ItemStack setRawName(ItemStack stack, String name) {
		if (name == null) {
			return stack;
		}
		ItemMeta meta = stack.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(name);
			stack.setItemMeta(meta);
		}
		return stack;
	}

	public String getName(ItemStack stack) {
		String displayed = getRawName(stack);
		displayed = displayed.length() > 2 ? displayed.substring(2) : displayed;
		return displayed;
	}

	public String getRawName(ItemStack stack) {
		if (stack == null) {
			return "";
		}
		if (stack.getItemMeta() == null) {
			return "";
		}
		String displayed = stack.getItemMeta().getDisplayName();
		if (displayed == null) {
			return "";
		}
		return displayed;
	}

	public boolean hasName(ItemStack stack, String name) {
		try {
			return getName(stack).equals(name);
		} catch (Exception e) {
			return false;
		}
	}

	public List<String> getLore(ItemStack stack) {
		if (stack == null) {
			return null;
		}
		if (stack.getItemMeta() == null) {
			return null;
		}
		return stack.getItemMeta().getLore();
	}

	public void setLore(ItemStack stack, List<String> lore) {
		if (stack == null) {
			return;
		}

		if (stack.getItemMeta() == null) {
			return;
		}

		ItemMeta meta = stack.getItemMeta();
		meta.setLore(new ArrayList<>());
		stack.setItemMeta(meta);

		for (String s : lore) {
			addDescription(stack, s);
		}
	}

	public Integer getId(Item item) {
		if (item == null) {
			return null;
		}
		return getId(item.getItemStack());
	}

	public Integer getId(ItemStack stack) {
		if (stack == null) {
			return null;
		}
		if (stack.getItemMeta() == null) {
			return null;
		}
		if (stack.getItemMeta().getLore() == null) {
			return null;
		}
		for (String line : stack.getItemMeta().getLore()) {
			if (line.startsWith("id:")) {
				try {
					return Integer.parseInt(line.substring(3));
				} catch (NumberFormatException e) {
					return null;
				}
			}
		}
		return null;
	}

	public ItemStack setId(ItemStack stack, int id) {
		ItemMeta meta = stack.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		if (meta.getLore() != null) {
			lore.addAll(meta.getLore());
		}
		boolean set = false;
		for (int i = 0; i < lore.size() && !set; i++) {
			if (lore.get(i).startsWith("id:")) {
				lore.set(i, "id:" + id);
				set = true;
			}
		}
		if (!set) {
			lore.add("id:" + id);
		}
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}

	public ItemStack addDescription(ItemStack stack, String description) {
		ItemMeta meta = stack.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		if (meta.getLore() != null) {
			lore.addAll(meta.getLore());
		}
		lore.add(ChatColor.RESET + description);
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}

	public ItemStack addDescription(ItemStack stack, String... description) {
		ItemMeta meta = stack.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		if (meta.getLore() != null) {
			lore.addAll(meta.getLore());
		}
		for (String added : description) {
			lore.add(ChatColor.RESET + added);
		}
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}

	public boolean hasEnchantment(ItemStack stack) {
		if (stack == null) {
			return false;
		}

		for (Integer v : stack.getEnchantments().values()) {
			if (v > 0) {
				return true;
			}
		}

		return false;
	}

	// Reduces the item of the amount by 1
	public void decrementStack(Inventory inv, ItemStack item) {
		if (item.getAmount() > 1) {
			item.setAmount(item.getAmount() - 1);
		} else {
			removeItemFromInventory(item, inv);
		}
	}

	public void removeItemFromInventory(ItemStack item, Inventory inventory) {
		item.setType(Material.MONSTER_EGG);

		item.setDurability((short) 123456789);
		item.setAmount(120);

		inventory.remove(item);
	}
}
