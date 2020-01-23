package com.pvpraids.raid.team.commands.impl;

import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.team.commands.TeamSubCommand;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CreateCommand extends TeamSubCommand {
	public CreateCommand() {
		super("create", false, false);
	}

	@Override
	public void onCommand(Player player, String label, String[] args) {
		if (RaidPlugin.getInstance().getTeamManager().getTeam(player) != null) {
			player.sendMessage(ChatColor.RED + "You are already on a team!");
			return;
		}

		String name;
		String password = null;

		if (args.length == 0 || args.length > 2 || args[0] == null || args[0].trim().equalsIgnoreCase("")) {
			player.sendMessage(ChatColor.RED + "Incorrect syntax.");
			player.sendMessage(ChatColor.GRAY + "Usage: /" + label + " create (name) [password]");
			player.sendMessage(ChatColor.GRAY + "Usage: /" + label + " create (name)");
			return;
		} else if (args.length == 1) {
			name = args[0];
		} else {
			name = args[0];
			password = args[1];
		}

		if (name.isEmpty() || !StringUtils.isAlphanumeric(name) || name.length() < 3 || name.length() > 20) {
			player.sendMessage(ChatColor.RED + "\"" + name + "\" is an invalid name!");
			player.sendMessage(CC.RED + "You must enter an alphanumeric name from 3-20 characters long.");
			return;
		}

		if (RaidPlugin.getInstance().getTeamManager().doesTeamExist(name)) {
			player.sendMessage(ChatColor.RED + "That team name is already in use!");
			return;
		}

		Team team = new Team(name, password);
		team.addMember(player);
		team.setManager(player, true);

		RaidPlugin.getInstance().getTeamManager().addTeam(team);
		RaidPlugin.getInstance().getTeamManager().saveTeam(team);

		player.sendMessage(ChatColor.YELLOW + "Team created!");
		player.sendMessage(ChatColor.GRAY + "To learn more about teams, do /team.");
	}
}
