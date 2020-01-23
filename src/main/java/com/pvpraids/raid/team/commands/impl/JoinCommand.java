package com.pvpraids.raid.team.commands.impl;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.team.commands.TeamSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class JoinCommand extends TeamSubCommand {
	public JoinCommand() {
		super("join", false, false, "j");
	}

	@Override
	public void onCommand(Player player, String label, String[] args) {
		if (RaidPlugin.getInstance().getTeamManager().getTeam(player) != null) {
			player.sendMessage(ChatColor.RED + "You are already on a team!");
			return;
		}

		String name;
		String password = null;
		if (args.length == 0 || args.length > 2) {
			player.sendMessage(ChatColor.RED + "You must specify a team name.");
			player.sendMessage(ChatColor.GRAY + "Usage: /" + label + " join (name) [password]");
			player.sendMessage(ChatColor.GRAY + "Usage: /" + label + " join (name)");
			return;
		} else if (args.length == 1) {
			name = args[0];
		} else {
			name = args[0];
			password = args[1];
		}

		if (!RaidPlugin.getInstance().getTeamManager().doesTeamExist(name)) {
			player.sendMessage(ChatColor.RED + "That team doesn't exist!");
			return;
		}

		Team team = RaidPlugin.getInstance().getTeamManager().getTeam(name);
		if (team.getMembers().size() > Team.MAX_PLAYERS) {
			player.sendMessage(ChatColor.RED + team.getName() + " is full.");
			return;
		}

		if (team.getPassword() != null) {
			if (!team.getPassword().equalsIgnoreCase(password)) {
				player.sendMessage(ChatColor.RED + "Incorrect password.");
				return;
			}
		}
		team.addMember(player);
		team.sendAnonymousMessage(ChatColor.YELLOW + player.getName() + " joined the team.");
		player.sendMessage(ChatColor.YELLOW + "Welcome to the team.");
	}
}
