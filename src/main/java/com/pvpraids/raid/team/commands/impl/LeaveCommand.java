package com.pvpraids.raid.team.commands.impl;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.team.commands.TeamSubCommand;
import com.pvpraids.raid.util.PlayerUtil;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LeaveCommand extends TeamSubCommand {
	public LeaveCommand() {
		super("leave", true, false, "l");
	}

	@Override
	public void onCommand(Player player, String label, String[] args) {
		Team team = RaidPlugin.getInstance().getTeamManager().getTeam(player);
		team.removePlayer(player);
		player.sendMessage(ChatColor.YELLOW + "You have left the team.");

		if (team.getMembers().size() > 0) {
			team.sendAnonymousMessage(ChatColor.YELLOW + player.getName() + " has left the team.");

			if (!team.hasManager()) {
				UUID newManager = (UUID) team.getMembers().keySet().toArray()[0];
				team.setManager(newManager, true);

				if (PlayerUtil.isOnline(newManager)) {
					RaidPlugin.getInstance().getServer().getPlayer(newManager).sendMessage(ChatColor.YELLOW + "You have been made the new manager of your team.");
				}
			}
		} else {
			RaidPlugin.getInstance().getTeamManager().deleteTeam(team);
		}
	}
}
