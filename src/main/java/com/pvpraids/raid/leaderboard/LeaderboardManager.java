package com.pvpraids.raid.leaderboard;

import com.pvpraids.raid.RaidPlugin;
import java.util.EnumMap;

public class LeaderboardManager {
	private final EnumMap<LeaderboardStat, Leaderboard> leaderboards = new EnumMap<>(LeaderboardStat.class);

	public LeaderboardManager(RaidPlugin plugin) {
		for (LeaderboardStat stat : LeaderboardStat.values()) {
			leaderboards.put(stat, new Leaderboard(plugin, stat));
		}
	}

	public void updateAll() {
		for (Leaderboard leaderboard : leaderboards.values()) {
			leaderboard.update();
		}
	}

	public Leaderboard getLeaderboardByStat(LeaderboardStat stat) {
		return leaderboards.get(stat);
	}
}
