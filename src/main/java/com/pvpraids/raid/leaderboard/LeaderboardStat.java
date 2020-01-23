package com.pvpraids.raid.leaderboard;

public enum LeaderboardStat {
	GOLD,
	MOOSHROOMS_CAPPED,
	OBSIDIAN_MINED,
	DIAMONDS_MINED,
	PLAYTIME;

	public static LeaderboardStat getByName(String statName) {
		for (LeaderboardStat stat : values()) {
			if (stat.name().equalsIgnoreCase(statName)) {
				return stat;
			}
		}

		return null;
	}
}
