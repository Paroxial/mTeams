package com.pvpraids.raid.listeners.fixes;

import com.pvpraids.core.utils.message.CC;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class NetherLimitListener implements Listener {

	@EventHandler
	public void onNetherEscapeAttempt(PlayerMoveEvent event) {
		Location to = event.getTo();

		if (!to.getWorld().getName().endsWith("_nether")) {
			return;
		}

		Location from = event.getFrom();

		if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
			return;
		}

		if (to.getBlockY() >= 126) {
			event.setTo(event.getFrom());
			event.getPlayer().sendMessage(CC.RED + "You can't go above the nether!");
		}
	}

	@EventHandler
	public void onBlockPlaceAboveNether(BlockPlaceEvent event) {
		Location block = event.getBlock().getLocation();
		if (!block.getWorld().getName().endsWith("_nether")) {
			return;
		}

		if (block.getBlockY() >= 126) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(CC.RED + "You can't place blocks above the nether!");
		}
	}

	@EventHandler
	public void onBlockBreakAboveNether(BlockBreakEvent event) {
		Location block = event.getBlock().getLocation();
		if (!block.getWorld().getName().endsWith("_nether")) {
			return;
		}

		if (block.getBlockY() >= 126) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(CC.RED + "You can't break blocks above the nether!");
		}
	}

	@EventHandler
	public void onTeleportAboveNether(PlayerTeleportEvent event) {
		Location to = event.getTo();
		if (!to.getWorld().getName().endsWith("_nether")) {
			return;
		}

		if (to.getBlockY() >= 126) {
			event.setTo(event.getFrom());
			event.setCancelled(true);
			event.getPlayer().sendMessage(CC.RED + "You can't teleport above the nether!");
		}
	}
}
