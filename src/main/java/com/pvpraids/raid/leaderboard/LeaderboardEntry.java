package com.pvpraids.raid.leaderboard;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LeaderboardEntry {
	private final String name;
	private final String displayName;
	private final UUID id;
	private final long amount;
}
