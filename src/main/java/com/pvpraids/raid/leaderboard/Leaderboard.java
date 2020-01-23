package com.pvpraids.raid.leaderboard;

import com.mongodb.client.MongoCursor;
import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.player.CoreProfile;
import com.pvpraids.core.player.rank.Rank;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

@RequiredArgsConstructor
public class Leaderboard {
	private final RaidPlugin plugin;
	private final LeaderboardStat trackedStat;
	private final List<LeaderboardEntry> entries = new ArrayList<>();

	synchronized void update() {
		List<LeaderboardEntry> updatedEntries = new ArrayList<>();
		List<UUID> addedUUIDs = new ArrayList<>();

		plugin.getPlayerManager().getPlayers().forEach(((uuid, raidPlayer) -> {
			CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(uuid);

			if (coreProfile == null) {
				return;
			}

			String displayName = coreProfile.getRank().getFormat() + coreProfile.getName();

			addedUUIDs.add(uuid);
			updatedEntries.add(new LeaderboardEntry(coreProfile.getName(), displayName, uuid, getAmountFromStat(raidPlayer)));
		}));

		MongoCursor<Document> allDocuments = CorePlugin.getInstance().getMongoStorage().getAllDocuments("team_players");

		while (allDocuments.hasNext()) {
			Document doc = allDocuments.next();
			UUID uuid = doc.get("_id", UUID.class);

			if (addedUUIDs.contains(uuid)) {
				continue;
			}

			Document document = CorePlugin.getInstance().getMongoStorage().getDocument("players", uuid);

			if (document == null) {
				continue;
			}

			String name = document.getString("name");

			if (name == null) {
				continue;
			}

			String rankName = document.getString("rank_name");
			Rank rank = Rank.getByName(rankName);

			if (rank == null) {
				continue;
			}

			String displayName = rank.getFormat() + name;

			updatedEntries.add(new LeaderboardEntry(name, displayName, uuid, getAmountFromStat(doc)));
		}

		updatedEntries.sort(EntryComparator.getInstance());

		entries.clear();
		entries.addAll(updatedEntries);
	}

	private long getAmountFromStat(RaidPlayer player) {
		switch (trackedStat) {
			case GOLD:
				return player.getBalance();
			case DIAMONDS_MINED:
				return player.getStats().getDiamondsMined();
			case OBSIDIAN_MINED:
				return player.getStats().getObsidianMined();
			case MOOSHROOMS_CAPPED:
				return player.getStats().getMooshroomsCapped();
			case PLAYTIME:
				return player.getCurrentPlaytime();
		}
		return 0L;
	}

	private long getAmountFromStat(Document document) {
		Document stats = (Document) document.get("stats");

		switch (trackedStat) {
			case GOLD:
				return document.get("balance", 0L);
			case DIAMONDS_MINED:
				return stats == null ? 0 : stats.getInteger("diamonds_mined");
			case OBSIDIAN_MINED:
				return stats == null ? 0 : stats.getInteger("obsidian_mined");
			case MOOSHROOMS_CAPPED:
				return stats == null ? 0 : stats.getInteger("mooshrooms_capped");
			case PLAYTIME:
				return document.get("playtime", 0L);
		}
		return 0L;
	}

	public List<LeaderboardEntry> getEntries() {
		synchronized (entries) {
			return entries;
		}
	}

	public List<LeaderboardEntry> getEntries(int index) {
		synchronized (entries) {
			if (entries.isEmpty()) {
				return Collections.emptyList();
			}

			if (entries.size() < index) {
				return entries;
			}

			return entries.subList(index, Math.min(index + 20, entries.size()));
		}
	}
}
