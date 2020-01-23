package com.pvpraids.raid.team.commands.impl;

import com.pvpraids.core.utils.ProfileUtil;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.team.commands.TeamSubCommand;
import com.pvpraids.raid.util.PlayerUtil;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KickCommand extends TeamSubCommand {
	public KickCommand() {
		super("kick", true, true, "k");
	}

	@Override
	public void onCommand(Player player, String label, String[] args) {
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "You must specify a player to kick.");
			return;
		}

		UUID target;
		if (PlayerUtil.isOnline(args[0])) {
			target = RaidPlugin.getInstance().getServer().getPlayer(args[0]).getUniqueId();
		} else {
			ProfileUtil.MojangProfile profile = ProfileUtil.lookupProfile(args[0]);
			target = profile.getId();
		}

		if (target == null) {
			player.sendMessage(ChatColor.RED + args[0] + " could not be found.");
			return;
		}

		Team team = RaidPlugin.getInstance().getTeamManager().getTeam(player);

		if (!team.isOnTeam(target)) {
			player.sendMessage(ChatColor.RED + args[0] + " isn't on the team!");
			return;
		}

		if (team.isManager(target)) {
			player.sendMessage(ChatColor.RED + "You can't kick another manager from the team!");
			return;
		}

		team.removePlayer(target);
		if (PlayerUtil.isOnline(target)) {
			RaidPlugin.getInstance().getServer().getPlayer(target).sendMessage(ChatColor.YELLOW + "You have been kicked from the team.");
		}

		if (team.getMembers().size() > 0) {
			team.sendAnonymousMessage(ChatColor.YELLOW + args[0] + " has been kicked from the team.");
		} else {
			RaidPlugin.getInstance().getTeamManager().deleteTeam(team);
		}
	}
}
