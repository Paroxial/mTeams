package com.pvpraids.raid.listeners;

import com.pvpraids.raid.RaidPlugin;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class SalvageListener implements Listener {
	private final RaidPlugin plugin;

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack item = e.getItem();
		Block clickedBlock = e.getClickedBlock();
		if (item != null && clickedBlock != null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (clickedBlock.getType() == Material.IRON_BLOCK || clickedBlock.getType() == Material.DIAMOND_BLOCK || clickedBlock.getType() == Material.GOLD_BLOCK) {
				List<ItemStack> salvagedItems = plugin.getSalvageManager().salvageItem(player, item, clickedBlock);
				if (salvagedItems.size() > 0) {
					e.setUseItemInHand(Event.Result.DENY);
					player.setItemInHand(null);
				}
				salvagedItems.forEach(itemStack -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
			}
		}
	}
}
