package com.pvpraids.raid.team.commands.impl;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.team.commands.TeamSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PasswordCommand extends TeamSubCommand {
	public PasswordCommand() {
		super("password", true, true, "pass");
	}

	@Override
	public void onCommand(Player player, String label, String[] args) {
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "You must specify a password.");
			player.sendMessage(ChatColor.GRAY + "To remove password protection use /" + label + " password none");
			player.sendMessage(ChatColor.GRAY + "Usage: /" + label + " password (password)");
			return;
		}

		Team team = RaidPlugin.getInstance().getTeamManager().getTeam(player);
		if (args[0].equalsIgnoreCase("none")) {
			team.setPassword(null);
			player.sendMessage(ChatColor.GRAY + "Password protection turned off.");
		} else {
			team.setPassword(args[0]);
			player.sendMessage(ChatColor.GRAY + "Password set to \"" + args[0] + "\"");
		}
	}
}
