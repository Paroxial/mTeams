package com.pvpraids.raid.listeners;

import com.pvpraids.core.utils.message.CC;
import com.pvpraids.core.utils.timer.Timer;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class PearlCooldownListener implements Listener {
	private final RaidPlugin plugin;

	@EventHandler
	public void onClickPearl(PlayerInteractEvent event) {
		if (!event.hasItem() || event.getItem().getType() != Material.ENDER_PEARL
				|| event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			return;
		}

		Player player = event.getPlayer();

		if (player.getGameMode() != GameMode.SURVIVAL) {
			return;
		}

		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);
		Timer timer = rp.getPearlTimer();

		if (timer.isActive(false)) {
			event.setCancelled(true);
			player.updateInventory();
			player.sendMessage(CC.PRIMARY + "You can't throw pearls for another " + CC.SECONDARY + timer.formattedExpiration() + CC.PRIMARY + ".");
		}
	}

	@EventHandler
	public void onShootPearl(ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity().getShooter();

		if (player.getGameMode() != GameMode.SURVIVAL) {
			return;
		}

		if (event.getEntity() instanceof EnderPearl) {
			RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);
			Timer timer = rp.getPearlTimer();
			timer.isActive(); // check if active, or else start cooldown
		}
	}
}
