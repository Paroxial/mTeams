package com.pvpraids.raid.listeners;

import com.pvpraids.core.utils.message.CC;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// TODO: WTF? idk why this wont work
public class PickaxeStatsListener implements Listener {
	private static int getLineByType(Material material) {
		switch (material) {
			case OBSIDIAN:
				return 1;
			case DIAMOND_ORE:
				return 2;
		}

		return 1;
	}

	private static String getName(Material type) {
		switch (type) {
			case DIAMOND_ORE:
				return "Diamonds";
		}

		return StringUtils.capitalize(type.name().toLowerCase());
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();

		if (player.getGameMode() != GameMode.SURVIVAL || item == null || !item.getType().name().contains("PICKAXE")) {
			return;
		}

		Material materialType = event.getBlock().getType();

		if (materialType != Material.DIAMOND_ORE && materialType != Material.OBSIDIAN) {
			return;
		}

		ItemMeta meta = item.getItemMeta();

		if (!item.hasItemMeta() || meta.getLore() == null) {
			meta.setLore(Collections.singletonList(""));
			item.setItemMeta(meta);
		}

		List<String> lore = meta.getLore();

		if (lore.size() < 4) {
			String line = CC.GRAY + CC.S + StringUtils.repeat('-', 20);
			meta.setLore(Arrays.asList(
					line,
					CC.PRIMARY + "Obsidian Mined: " + CC.SECONDARY + (materialType == Material.OBSIDIAN ? "1" : "0"),
					CC.PRIMARY + "Diamonds Mined: " + CC.SECONDARY + (materialType == Material.DIAMOND_ORE ? "1" : "0"),
					line
			));
			item.setItemMeta(meta);
			return;
		}

		int lineIndex = getLineByType(materialType);
		String typeMinedString = lore.get(lineIndex);
		String typeMined = typeMinedString.substring(typeMinedString.length() - 1);
		int typeMinedNum = Integer.parseInt(typeMined);

		List<String> newLore = meta.getLore();
		newLore.set(lineIndex, CC.PRIMARY + getName(materialType) + " Mined: " + CC.SECONDARY + (typeMinedNum + 1));

		meta.setLore(newLore);
		item.setItemMeta(meta);
	}
}
