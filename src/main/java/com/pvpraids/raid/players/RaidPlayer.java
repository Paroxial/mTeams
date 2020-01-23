package com.pvpraids.raid.players;

import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.storage.database.MongoRequest;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.core.utils.time.TimeUtil;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.economy.Economy;
import com.pvpraids.raid.util.LocationUtil;
import com.pvpraids.raid.util.PlayerUtil;
import com.pvpraids.raid.util.timer.PearlTimer;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

@Getter
@Setter
@RequiredArgsConstructor
public class RaidPlayer {
	private final UUID uniqueId;
	private final PlayerStats stats = new PlayerStats();
	private final PearlTimer pearlTimer = new PearlTimer();
	private String lastSavedInventory;
	private Location home;
	private long balance;
	private long lastDeathBan;
	private long loginTime = System.currentTimeMillis();
	private long lastCombatTag;
	private long cannotAttackUntil;
	private boolean teamChat;
	private boolean spawnProtected = true;
	private boolean scoreboardVisible = true;
	private boolean firstLogin = true;

	public Player getPlayer() {
		return RaidPlugin.getInstance().getServer().getPlayer(uniqueId);
	}

	public String getName() {
		return getPlayer().getName();
	}

	public long getCurrentPlaytime() {
		Player p = getPlayer();
		return p == null ? 0 : p.getStatistic(Statistic.PLAY_ONE_TICK) / 20;
	}

	public String getFormattedPlaytime() {
		return TimeUtil.formatTimeSeconds(getCurrentPlaytime());
	}

	public void addBalance(long amount) {
		balance += amount;
	}

	public boolean subtractBalance(long amount) {
		long newBalance = balance - amount;
		if (newBalance < 0) {
			return false;
		}

		balance = newBalance;
		return true;
	}

	public boolean subtractGold(int gold) {
		long value = gold * Economy.UNIT_GOLD;
		if (value > balance) {
			return false;
		}

		subtractBalance(value);
		return true;
	}

	public boolean hasCombatTag() {
		return !spawnProtected && System.currentTimeMillis() - lastCombatTag <= 30_000;
	}

	public void loseSpawnProtection() {
		spawnProtected = false;
		getPlayer().sendMessage(CC.GRAY + "You no longer have spawn protection.");
	}

	public void gainSpawnProtection() {
		spawnProtected = true;
		getPlayer().sendMessage(CC.GRAY + "You have gained have spawn protection.");
	}

	public void save(boolean async) {
		if (getPlayer() != null)
			setLastSavedInventory(PlayerUtil.toBase64(getPlayer().getInventory()));

		MongoRequest request = MongoRequest.newRequest("team_players", uniqueId)
				.put("balance", balance)
				.put("last_death_ban", lastDeathBan)
				.put("scoreboard_visible", scoreboardVisible)
				.put("home_location", LocationUtil.locationToString(home))
				.put("last_saved_inventory", lastSavedInventory)
				.put("stats", stats.serialize())
				.put("first_login", false)
				.put("playtime", getCurrentPlaytime());

		if (async) {
			RaidPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(RaidPlugin.getInstance(), request::run);
		} else {
			request.run();
		}
	}

	public void save() {
		save(false);
	}

	public void load() {
		CorePlugin.getInstance().getMongoStorage().getOrCreateDocument("team_players", uniqueId, (document, exists) -> {
			if (exists) {
				this.balance = document.get("balance", 0L);
				this.lastDeathBan = document.get("last_death_ban", 0L);
				this.scoreboardVisible = document.get("scoreboard_visible", true);
				this.home = LocationUtil.stringToLocation(document.get("home_location", "null"));
				this.lastSavedInventory = document.getString("last_saved_inventory");
				this.firstLogin = document.getBoolean("first_login", true);

				Document docStats = document.get("stats", Document.class);

				if (docStats != null)
					stats.deserialize(docStats);
			}
		});
	}
}
