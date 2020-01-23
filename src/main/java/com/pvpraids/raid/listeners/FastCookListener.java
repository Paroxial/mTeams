package com.pvpraids.raid.listeners;

import com.pvpraids.raid.RaidPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class FastCookListener implements Listener {
	private final RaidPlugin plugin;

	@EventHandler
	public void onFurnaceQuicken(FurnaceBurnEvent event) {
		Furnace furnace = (Furnace) event.getBlock().getState();

		new BukkitRunnable() {
			@Override
			public void run() {
				if (furnace.getCookTime() > 0 || furnace.getBurnTime() > 0) {
					furnace.setCookTime((short) (furnace.getCookTime() + 4));
					furnace.update();
				} else {
					cancel();
				}
			}
		}.runTaskTimer(plugin, 2L, 2L);
	}
}
