package com.pvpraids.raid.team.commands.impl;

import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.team.commands.TeamSubCommand;
import com.pvpraids.raid.util.PlayerUtil;
import java.util.UUID;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class InfoCommand extends TeamSubCommand {
	public InfoCommand() {
		super("info", false, false, "i");
	}

	@Override
	public void onCommand(Player player, String label, String[] args) {
		Team team = RaidPlugin.getInstance().getTeamManager().getTeam(player);

		if (args.length == 1) {
			Player targetPlayer = RaidPlugin.getInstance().getServer().getPlayer(args[0]);

			if (targetPlayer != null) {
				team = RaidPlugin.getInstance().getTeamManager().getTeam(targetPlayer);
			} else {
				team = RaidPlugin.getInstance().getTeamManager().getTeam(args[0]);
			}

			if (team == null) {
				player.sendMessage(CC.RED + "Team does not exist!");
				return;
			}
		}

		if (team == null) {
			player.sendMessage(ChatColor.RED + "You are not on a team.");
			return;
		}

		player.sendMessage(ChatColor.YELLOW + team.getName() + " (" + team.getMembers().size() + "/20)");
		if (team.isOnTeam(player)) {
			player.sendMessage(ChatColor.GRAY + "Password: " + (team.getPassword() != null ? team.getPassword() : "not set"));
			player.sendMessage(ChatColor.GRAY + "Headquarters: " + (team.getHq() != null ? "set" : "not set"));
			player.sendMessage(ChatColor.GRAY + "Rally: " + (team.getRally() != null ? "set" : "not set"));
			player.sendMessage(ChatColor.GRAY + "Friendly fire: " + (team.isFriendlyFire() ? "on" : "off"));
		}

		player.sendMessage(ChatColor.GRAY + "Members:");
		team.getMembers().forEach((uuid, manager) -> {
			double healthPercent = -1;
			if (PlayerUtil.isOnline(uuid)) {
				Player member = RaidPlugin.getInstance().getServer().getPlayer(uuid);
				healthPercent = member.getHealth() * 5;
			}

			// not sure if this is gonna work
			String name = PlayerUtil.isOnline(uuid) ?
					RaidPlugin.getInstance().getServer().getPlayer(uuid).getName() :
					RaidPlugin.getInstance().getServer().getOfflinePlayer(uuid).getName();

			String health = PlayerUtil.isOnline(uuid) ? healthPercent + "%" : "Offline";

			player.sendMessage((manager ? ChatColor.YELLOW + "*" : ChatColor.GRAY) + name + " - " + health);
		});

		final Team finalTeam = team;

		RaidPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(RaidPlugin.getInstance(), () -> {
			long sum = 0L;

			for (UUID uuid : finalTeam.getMembers().keySet()) {
				if (PlayerUtil.isOnline(uuid)) {
					RaidPlayer rp = RaidPlugin.getInstance().getPlayerManager().getPlayer(uuid);

					if (rp != null) {
						sum += rp.getBalance();
					}
				} else {
					Document doc = CorePlugin.getInstance().getMongoStorage().getDocument("team_players", uuid);

					if (doc != null) {
						sum += doc.get("balance", 0L);
					}
				}
			}

			player.sendMessage(CC.GRAY + "Gold: " + RaidPlugin.getInstance().getEconomy().toGoldString(sum));
		});
	}
}
