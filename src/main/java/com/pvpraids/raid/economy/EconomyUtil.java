package com.pvpraids.raid.economy;

import com.pvpraids.raid.economy.material.MarketMaterial;
import com.pvpraids.raid.economy.material.Materials;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class EconomyUtil {
	private EconomyUtil() {
	}

	public static int count(Inventory inventory, MarketMaterial material) {
		int count = 0;
		for (ItemStack stack : inventory.getContents()) {
			MarketMaterial stackMaterial = Materials.get(stack);
			if (stackMaterial == null) {
				continue;
			}
			count += stackMaterial.equals(material) ? stack.getAmount() : 0;
		}
		return count;
	}

	public static boolean remove(Inventory inventory, MarketMaterial material, int amount) {
		for (int i = 0; i != inventory.getSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (material.matches(stack)) {
				// Calculate new amount of the current stack.
				int amountNew = Math.max(stack.getAmount() - amount, 0);
				// Reduce by the amount we're removing.
				amount -= stack.getAmount() - amountNew;

				if (amountNew <= 0) {
					inventory.setItem(i, null);
				} else {
					stack.setAmount(amountNew);
					inventory.setItem(i, stack);
				}
			}
		}
		return amount <= 0;
	}

	public static boolean give(Player player, MarketMaterial material, int amount) {
		Inventory inventory = player.getInventory();
		boolean floor = false;

		while (amount > 0) {
			ItemStack stack = material.create(1);
			for (ItemStack floored : inventory.addItem(stack).values()) {
				floor = true;
				player.getWorld().dropItem(player.getLocation(), floored);
			}
			amount--;
		}

		player.updateInventory();
		return floor;
	}
}