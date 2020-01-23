package com.pvpraids.raid.listeners.fixes;

import com.google.common.collect.ImmutableSet;
import com.pvpraids.core.utils.message.CC;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DisabledPotionListener implements Listener {
	private static final Set<Integer> DISABLED_IDS = ImmutableSet.of(8235, 8267, 16427, 16459, 8206, 8270, 16398, 16462);

	@EventHandler
	public void onDisabledPotionUse(PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}

		Player player = event.getPlayer();
		ItemStack item = event.getItem();

		if (item.getType() == Material.POTION) {
			int id = item.getDurability();

			if (DISABLED_IDS.contains(id)) {
				event.setCancelled(true);
				player.updateInventory();
				player.sendMessage(CC.RED + "You're not allowed to use that potion.");
			}
		}
	}
}
