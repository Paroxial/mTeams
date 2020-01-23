package com.pvpraids.raid.listeners.fixes;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PortalFixListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPortal(EntityPortalEvent e) {
		if (e.getEntityType() == EntityType.MINECART_CHEST || e.getEntityType() == EntityType.MINECART_FURNACE || e.getEntityType() == EntityType.MINECART_HOPPER) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPortalTrap(PlayerTeleportEvent event) {

	}
}
