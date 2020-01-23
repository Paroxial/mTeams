package com.pvpraids.raid.listeners;

import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.player.CoreProfile;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.util.LocationUtil;
import com.pvpraids.raid.util.NumberUtil;
import com.pvpraids.raid.util.timer.PearlTimer;
import com.pvpraids.scoreboardapi.PlayerScoreboardUpdateEvent;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class ScoreboardListener implements Listener {
	private final RaidPlugin plugin;

	@EventHandler
	public void onScoreboardUpdate(PlayerScoreboardUpdateEvent event) {
		Player player = event.getPlayer();
		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);

		if (rp == null || !rp.isScoreboardVisible()) {
			return;
		}

		event.setTitle(CC.GOLD + "PvPRaids.com");

		String separator = CC.GRAY + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 25);
		event.writeLine(separator);

		Team team = plugin.getTeamManager().getTeam(player);

		event.writeLine(CC.PRIMARY + "Team" + CC.GRAY + ": " + CC.WHITE + (team == null ? "None" : team.getName()));
		event.writeLine(CC.PRIMARY + "Gold" + CC.GRAY + ": " + CC.GOLD + plugin.getEconomy().toGold(rp.getBalance()));

		if (LocationUtil.isWithinSpawn(player.getLocation())) {
			event.writeLine(CC.PRIMARY + "Spawn Protected" + CC.GRAY + ": " + (rp.isSpawnProtected() ? CC.GREEN + "Yes" : CC.RED + "No"));
		} else {
			if (!player.isDead() && rp.hasCombatTag()) {
				event.writeLine(CC.PRIMARY + "Combat Tag" + CC.GRAY + ": " + CC.SECONDARY +
						NumberUtil.roundToTenthsPlace((30_000 - (System.currentTimeMillis() - rp.getLastCombatTag())) / 1000.0) + "s");
			}
		}

		PearlTimer timer = rp.getPearlTimer();

		if (timer.isActive(false)) {
			event.writeLine(CC.PRIMARY + "Pearl Cooldown" + CC.GRAY + ": " + CC.SECONDARY + timer.scoreboardFormattedExpiration());
		}

		CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

		if (profile != null) {
			List<String> staffLines = new ArrayList<>();

			if (profile.isInStaffChat()) {
				staffLines.add(CC.GREEN + CC.I + "Staff Chat Enabled");
			}

			if (profile.isVanished()) {
				staffLines.add(CC.GRAY + CC.I + "Vanish Enabled");
			}

			if (!staffLines.isEmpty()) {
				event.writeLine(CC.YELLOW + "Staff Info" + CC.GRAY + ":");
				staffLines.forEach(event::writeLine);
			}
		}

		event.writeLine(separator);
	}
}
