package com.pvpraids.raid.team.commands.impl;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.team.commands.TeamSubCommand;
import com.pvpraids.raid.util.PlayerUtil;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DemoteCommand extends TeamSubCommand {
	public DemoteCommand() {
		super("demote", true, true, "d");
	}

	@Override
	public void onCommand(Player player, String label, String[] args) {
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "You must specify a player to demote!");
			return;
		}

		UUID target = PlayerUtil.isOnline(args[0]) ? RaidPlugin.getInstance().getServer().getPlayer(args[0]).getUniqueId()
				: RaidPlugin.getInstance().getServer().getOfflinePlayer(args[0]).getUniqueId();
		String name = PlayerUtil.isOnline(args[0]) ? RaidPlugin.getInstance().getServer().getPlayer(args[0]).getName()
				: RaidPlugin.getInstance().getServer().getOfflinePlayer(args[0]).getName();
		Team team = RaidPlugin.getInstance().getTeamManager().getTeam(player);

		if (!team.isOnTeam(target)) {
			player.sendMessage(ChatColor.RED + name + " isn't on the team!");
			return;
		}

		if (!team.isManager(target)) {
			player.sendMessage(ChatColor.RED + name + " is not a manager!");
			return;
		}

		team.setManager(target, false);
		player.sendMessage(ChatColor.YELLOW + name + " has been demoted.");

		if (PlayerUtil.isOnline(args[0])) {
			RaidPlugin.getInstance().getServer().getPlayer(args[0]).sendMessage(ChatColor.YELLOW + "You have been demoted.");
		}
	}
}
