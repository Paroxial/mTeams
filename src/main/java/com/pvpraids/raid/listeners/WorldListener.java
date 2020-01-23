package com.pvpraids.raid.listeners;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import com.pvpraids.raid.util.LocationUtil;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

@RequiredArgsConstructor
public class WorldListener implements Listener {
	private static final List<EntityType> blacklistedEntities = Arrays.asList(
			EntityType.GUARDIAN, EntityType.ENDERMITE, EntityType.RABBIT
	);

	private final RaidPlugin plugin;

	@EventHandler
	public void onLeaveDecay(LeavesDecayEvent e) {
		if (LocationUtil.isCloseToSpawn(e.getBlock().getLocation(), LocationUtil.SPAWN_POS_BOUND)) {
			e.setCancelled(true);
		}
	}

	// cancelling this apparently causes memory leaks, so just remove the entity to cancel it
	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent e) {
		if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
			return;
		}
		if (e.getEntityType() != EntityType.VILLAGER && LocationUtil.isCloseToSpawn(e.getEntity().getLocation())) {
			e.getEntity().remove();
		}
		if (blacklistedEntities.contains(e.getEntityType())) {
			e.getEntity().remove();
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		Block block = e.getBlock();

		if (LocationUtil.isCloseToSpawn(block.getLocation(), LocationUtil.BLOCKS_CLEAR)) {
			if (block.getType() == Material.LAVA_BUCKET || block.getType() == Material.WATER_BUCKET) {
				e.setCancelled(true);
				player.updateInventory();
				return;
			}
			plugin.getBlocksPlacedNearSpawn().put(block, System.currentTimeMillis());
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		Block block = e.getBlock();

		if (LocationUtil.isWithinSpawnAntiBreak(block.getLocation())) {
			e.setCancelled(true);
			player.updateInventory();
			return;
		}

		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);

		switch (block.getType()) {
			case DIAMOND_ORE:
				rp.getStats().setDiamondsMined(rp.getStats().getDiamondsMined() + 1);
				break;
			case OBSIDIAN:
				rp.getStats().setObsidianMined(rp.getStats().getObsidianMined() + 1);
				break;
		}
	}

	@EventHandler
	public void onCreeperExplode(EntityExplodeEvent event) {
		if (LocationUtil.isWithinSpawnAntiBreak(event.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onIgnite(BlockIgniteEvent event) {
		if (LocationUtil.isWithinSpawnAntiBreak(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBurn(BlockBurnEvent event) {
		if (LocationUtil.isWithinSpawnAntiBreak(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onRainStart(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent e) {
		Player player = e.getPlayer();
		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player.getUniqueId());

		if (rp.isSpawnProtected() && !player.isOp()) {
			e.setCancelled(true);
		}
	}
}
