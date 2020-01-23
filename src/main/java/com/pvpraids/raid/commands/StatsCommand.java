package com.pvpraids.raid.commands;

import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.core.utils.time.TimeUtil;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.PlayerStats;
import com.pvpraids.raid.players.RaidPlayer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class StatsCommand extends PlayerCommand {
	private final RaidPlugin plugin;

	public StatsCommand(RaidPlugin plugin) {
		super("stats");
		this.plugin = plugin;
		setAliases("statistics");
		setUsage(CC.RED + "/stats [player]");
	}

	@Override
	public void execute(Player player, String[] args) {
		Player target = args.length < 1 ? player : Bukkit.getPlayer(args[0]);

		if (target == player || target != null) {
			RaidPlayer rp = plugin.getPlayerManager().getPlayer(target);
			PlayerStats stats = rp.getStats();

			player.sendMessage(CC.PRIMARY + "Stats of " + target.getDisplayName());
			player.sendMessage(CC.PRIMARY + "Gold: " + CC.GOLD + plugin.getEconomy().toGoldString(rp.getBalance()));
			player.sendMessage(CC.PRIMARY + "Mooshrooms Capped: " + CC.SECONDARY + stats.getMooshroomsCapped());
			player.sendMessage(CC.PRIMARY + "Diamonds Mined: " + CC.SECONDARY + stats.getDiamondsMined());
			player.sendMessage(CC.PRIMARY + "Obsidian Mined: " + CC.SECONDARY + stats.getObsidianMined());
			player.sendMessage(CC.PRIMARY + "Playtime: " + CC.SECONDARY + rp.getFormattedPlaytime());
		} else {
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
				OfflinePlayer offlineTarget = plugin.getServer().getOfflinePlayer(args[0]);

				if (offlineTarget == null || !offlineTarget.hasPlayedBefore()) {
					player.sendMessage(CC.RED + "Player doesn't exist.");
					return;
				}

				Document storedPlayerData = CorePlugin.getInstance().getMongoStorage().getDocument("team_players", offlineTarget.getUniqueId());

				if (storedPlayerData == null) {
					player.sendMessage(CC.RED + "That player doesn't have any data.");
					return;
				}

				Document statsDoc = storedPlayerData.get("stats", Document.class);

				if (statsDoc == null) {
					player.sendMessage(CC.RED + "That player doesn't have any data.");
					return;
				}

				player.sendMessage(CC.PRIMARY + "Stats of " + CC.SECONDARY + offlineTarget.getName());
				player.sendMessage(CC.PRIMARY + "Gold: " + CC.GOLD + plugin.getEconomy().toGoldString(storedPlayerData.getLong("balance")));
				player.sendMessage(CC.PRIMARY + "Mooshrooms Capped: " + CC.SECONDARY + statsDoc.getInteger("mooshrooms_capped"));
				player.sendMessage(CC.PRIMARY + "Diamonds Mined: " + CC.SECONDARY + statsDoc.getInteger("diamonds_mined"));
				player.sendMessage(CC.PRIMARY + "Obsidian Mined: " + CC.SECONDARY + statsDoc.getInteger("obsidian_mined"));
				player.sendMessage(CC.PRIMARY + "Playtime: " + CC.SECONDARY + TimeUtil.formatTimeSeconds(storedPlayerData.getLong("playtime")));
			});
		}
	}
}
