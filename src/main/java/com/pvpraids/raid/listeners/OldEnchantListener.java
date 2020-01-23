package com.pvpraids.raid.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

public class OldEnchantListener implements Listener {
	@EventHandler
	public void onEnchant(EnchantItemEvent e) {
		e.getEnchanter().setLevel(e.getEnchanter().getLevel() - e.getExpLevelCost() + (e.whichButton() + 1));
		e.setExpLevelCost(0);
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		if (e.getInventory().getType().equals(InventoryType.ENCHANTING)) {
			EnchantingInventory en = (EnchantingInventory) e.getInventory();
			en.setSecondary(new ItemStack(Material.INK_SACK, 64, (short) 4));
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (e.getInventory().getType().equals(InventoryType.ENCHANTING)) {
			e.getInventory().setItem(1, null);
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getInventory().getType().equals(InventoryType.ENCHANTING)) {
			if (e.getRawSlot() == 1) {
				e.setCancelled(true);
			}
		}
	}
}
