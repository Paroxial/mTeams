package com.pvpraids.raid.team.commands.impl;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.team.commands.TeamSubCommand;
import com.pvpraids.raid.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PromoteCommand extends TeamSubCommand {
	public PromoteCommand() {
		super("promote", true, true, "p");
	}

	@Override
	public void onCommand(Player player, String label, String[] args) {
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "You must specify a player to promote!");
			return;
		}

		if (!PlayerUtil.isOnline(args[0])) {
			player.sendMessage(ChatColor.RED + args[0] + " is not online.");
			return;
		}

		Player target = RaidPlugin.getInstance().getServer().getPlayer(args[0]);
		Team team = RaidPlugin.getInstance().getTeamManager().getTeam(player);

		if (!team.isOnTeam(target)) {
			player.sendMessage(ChatColor.RED + target.getName() + " isn't on the team!");
			return;
		}

		if (team.isManager(target)) {
			player.sendMessage(ChatColor.RED + target.getName() + " is already manager!");
			return;
		}

		team.setManager(target, true);
		player.sendMessage(ChatColor.YELLOW + target.getName() + " has been promoted.");
		target.sendMessage(ChatColor.YELLOW + "You have been promoted.");
	}
}
