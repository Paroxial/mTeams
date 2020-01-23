package com.pvpraids.raid.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SoupListener implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		Material itemInHandType = event.getPlayer().getItemInHand().getType();
		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& itemInHandType == Material.MUSHROOM_SOUP && player.getItemInHand().getAmount() == 1) {
			if (player.getHealth() <= 19) {
				event.setCancelled(true);
				player.setHealth(Math.min(player.getHealth() + 7, player.getMaxHealth()));
				player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.BOWL));
				player.updateInventory();
			} else if (player.getFoodLevel() != 20) {
				event.setCancelled(true);

				player.setFoodLevel(Math.min(player.getFoodLevel() + 7, 20));
				player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.BOWL));
				player.updateInventory();
			}
		}
	}
}
