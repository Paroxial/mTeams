package com.pvpraids.raid.salvaging;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.util.LocationUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SalvageManager {
	private final Set<ISalvager> salvagers = new HashSet<>();

	public SalvageManager(RaidPlugin plugin) {
		salvagers.add(new ItemSalvager(plugin, Material.IRON_CHESTPLATE, Material.IRON_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.IRON_LEGGINGS, Material.IRON_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.IRON_BOOTS, Material.IRON_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.IRON_HELMET, Material.IRON_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.IRON_SWORD, Material.IRON_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.IRON_AXE, Material.IRON_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.IRON_SPADE, Material.IRON_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.IRON_PICKAXE, Material.IRON_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.IRON_HOE, Material.IRON_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.BUCKET, Material.IRON_BLOCK));

		Map<Material, Integer> ironBardingIngredients = new HashMap<>();
		ironBardingIngredients.put(Material.IRON_INGOT, 3);
		salvagers.add(new ItemSalvager(Material.IRON_BARDING, Material.IRON_BLOCK, ironBardingIngredients));

		salvagers.add(new ItemSalvager(plugin, Material.GOLD_CHESTPLATE, Material.GOLD_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.GOLD_LEGGINGS, Material.GOLD_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.GOLD_BOOTS, Material.GOLD_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.GOLD_HELMET, Material.GOLD_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.GOLD_SWORD, Material.GOLD_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.GOLD_AXE, Material.GOLD_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.GOLD_SPADE, Material.GOLD_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.GOLD_PICKAXE, Material.GOLD_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.GOLD_HOE, Material.GOLD_BLOCK));

		Map<Material, Integer> goldBardingIngredients = new HashMap<>();
		goldBardingIngredients.put(Material.GOLD_INGOT, 3);
		salvagers.add(new ItemSalvager(Material.GOLD_BARDING, Material.GOLD_BLOCK, goldBardingIngredients));

		salvagers.add(new ItemSalvager(plugin, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.DIAMOND_BOOTS, Material.DIAMOND_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.DIAMOND_HELMET, Material.DIAMOND_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.DIAMOND_SWORD, Material.DIAMOND_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.DIAMOND_AXE, Material.DIAMOND_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.DIAMOND_SPADE, Material.DIAMOND_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.DIAMOND_PICKAXE, Material.DIAMOND_BLOCK));
		salvagers.add(new ItemSalvager(plugin, Material.DIAMOND_HOE, Material.DIAMOND_BLOCK));

		Map<Material, Integer> diamondBardingIngredients = new HashMap<>();
		diamondBardingIngredients.put(Material.DIAMOND, 3);

		salvagers.add(new ItemSalvager(Material.DIAMOND_BARDING, Material.DIAMOND_BLOCK, diamondBardingIngredients));
	}

	public List<ItemStack> salvageItem(Player player, ItemStack stack, Block withBlock) {
		List<ItemStack> salvagedItems = new ArrayList<>();
		if (LocationUtil.hasBlockTypeTouching(withBlock, Material.FURNACE) || LocationUtil.hasBlockTypeTouching(withBlock, Material.BURNING_FURNACE)) {
			for (ISalvager salvager : salvagers) {
				if (salvager.canSalvage(stack, withBlock)) {
					salvagedItems.addAll(salvager.salvageItem(stack));
					if (stack.getEnchantments().size() > 0 && LocationUtil.hasBlockTypeTouching(withBlock, Material.ENCHANTMENT_TABLE)) {
						double xpModifier = (double) (stack.getType().getMaxDurability() - stack.getDurability()) / (double) stack.getType().getMaxDurability();
						int xpCount = 0;
						for (Map.Entry<Enchantment, Integer> enchantmentEntry : stack.getEnchantments().entrySet()) {
							xpCount += enchantmentEntry.getValue() * 10;
						}
						player.giveExp((int) (xpCount * xpModifier));
					}
					break;
				}
			}
		}
		return salvagedItems;
	}
}
