package com.pvpraids.raid.task;

import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.player.CoreProfile;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class WarpTask extends BukkitRunnable {
	private static final int PLAYER_NEARBY_RADIUS = 24 * 24; // squared

	private Player player;
	private Location to;

	public WarpTask(Player player, Location to) {
		this.player = player;
		this.to = to;

		if (RaidPlugin.getInstance().getWarping().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are already warping!");
			return;
		}

		CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

		if (profile.isVanished()) {
			runTask(RaidPlugin.getInstance());
			return;
		}

		boolean playersNearby = false;
		for (Player other : player.getWorld().getPlayers()) {
			if (player == other) {
				continue;
			}
			if (!player.canSee(other)) {
				continue;
			}
			if (other.getGameMode() == GameMode.CREATIVE) {
				continue;
			}
			if (RaidPlugin.getInstance().getTeamManager().getTeam(player) != null && RaidPlugin.getInstance().getTeamManager().getTeam(player).isOnTeam(other)) {
				continue;
			}
			if (other.getLocation().distanceSquared(player.getLocation()) <= PLAYER_NEARBY_RADIUS) {
				playersNearby = true;
			}
		}

		if (playersNearby) {
			player.sendMessage(ChatColor.GRAY + "Someone is nearby! You must stand still for 10 seconds to warp!");

			runTaskLater(RaidPlugin.getInstance(), 20 * 10);
			RaidPlugin.getInstance().getWarping().put(player.getUniqueId(), this);
		} else {
			runTask(RaidPlugin.getInstance());
		}
	}

	@Override
	public void run() {
		RaidPlugin.getInstance().getWarping().remove(player.getUniqueId());

		if (player.isInsideVehicle()) {
			player.sendMessage(ChatColor.RED + "You cannot warp while inside a vehicle.");
			return;
		}

		player.teleport(to);

		RaidPlayer rp = RaidPlugin.getInstance().getPlayerManager().getPlayer(player);

		if (to.equals(RaidPlugin.getInstance().getSpawn()) && !rp.isSpawnProtected()) {
			rp.gainSpawnProtection();
		}

		rp.setCannotAttackUntil(System.currentTimeMillis() + 10_000);
		player.sendMessage(CC.GRAY + "You cannot attack for 10 seconds.");
	}
}
