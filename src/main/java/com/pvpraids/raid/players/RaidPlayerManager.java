package com.pvpraids.raid.players;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class RaidPlayerManager {
	private final Map<UUID, RaidPlayer> players = new HashMap<>();

	public RaidPlayer addPlayer(UUID playerUUID) {
		players.remove(playerUUID);

		RaidPlayer rp = new RaidPlayer(playerUUID);

		players.put(playerUUID, rp);

		return rp;
	}

	public void removePlayer(Player player) {
		if (!players.containsKey(player.getUniqueId())) {
			return;
		}

		RaidPlayer rp = players.remove(player.getUniqueId());
		rp.save(true);
	}

	public void removePlayer(UUID id) {
		if (!players.containsKey(id)) {
			return;
		}

		RaidPlayer rp = players.remove(id);
		rp.save(true);
	}

	public RaidPlayer getPlayer(UUID uuid) {
		return players.get(uuid);
	}

	public RaidPlayer getPlayer(Player player) {
		return getPlayer(player.getUniqueId());
	}
}
