package com.pvpraids.raid.team.commands;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.team.commands.impl.ChatCommand;
import com.pvpraids.raid.team.commands.impl.CreateCommand;
import com.pvpraids.raid.team.commands.impl.DemoteCommand;
import com.pvpraids.raid.team.commands.impl.FriendlyFireCommand;
import com.pvpraids.raid.team.commands.impl.HQCommand;
import com.pvpraids.raid.team.commands.impl.InfoCommand;
import com.pvpraids.raid.team.commands.impl.JoinCommand;
import com.pvpraids.raid.team.commands.impl.KickCommand;
import com.pvpraids.raid.team.commands.impl.LeaveCommand;
import com.pvpraids.raid.team.commands.impl.MessageCommand;
import com.pvpraids.raid.team.commands.impl.PasswordCommand;
import com.pvpraids.raid.team.commands.impl.PromoteCommand;
import com.pvpraids.raid.team.commands.impl.RallyCommand;
import com.pvpraids.raid.team.commands.impl.SetHQCommand;
import com.pvpraids.raid.team.commands.impl.SetRallyCommand;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamCommand extends PlayerCommand {
	private final List<TeamSubCommand> subCommands = Arrays.asList(
			new ChatCommand(), new CreateCommand(), new FriendlyFireCommand(),
			new HQCommand(), new InfoCommand(), new JoinCommand(),
			new KickCommand(), new LeaveCommand(), new MessageCommand(),
			new PasswordCommand(), new PromoteCommand(), new RallyCommand(),
			new SetHQCommand(), new SetRallyCommand(), new DemoteCommand()
	);

	public TeamCommand() {
		super("team");
		setAliases("t");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length == 0) {
			player.sendMessage(ChatColor.YELLOW + "***Anyone***");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " create (name) [password] - Create a team.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " join (name) [password] - Join a team.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " leave - Leave your current team.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " info - Get details about your team.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " info (player) - Get details about a team.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " msg (message) - Send only teammates a message.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " chat - Toggle team chat only mode on or off.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " hq - Teleport to the team headquarters.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " rally - Teleport to the team rally point.");
			player.sendMessage(ChatColor.YELLOW + "***Team Managers Only***");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " password (password) - Change your team's password.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " kick (player) - Kick a player from your team.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " demote (player) - Demote another player to member status.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " promote (player) - Promote another player to manager status.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " sethq - Set the team headquarters warp location.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " setrally - Set the team rally point warp location.");
			player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " ff - Toggle friendly fire.");
		} else {
			Optional<TeamSubCommand> subCommand = subCommands.stream()
					.filter(cmd -> cmd.getName().equalsIgnoreCase(args[0]) || ArrayUtils.contains(cmd.getAliases(), args[0])).findAny();
			if (!subCommand.isPresent()) {
				player.sendMessage(ChatColor.RED + "Unrecognized team command.");
				player.sendMessage(ChatColor.GRAY + "/" + getLabel() + " for details.");
				return;
			}

			TeamSubCommand cmd = subCommand.get();
			if (cmd.isRequiresTeam() && RaidPlugin.getInstance().getTeamManager().getTeam(player) == null) {
				player.sendMessage(ChatColor.RED + "You are not on a team!");
				return;
			}

			if (cmd.isRequiresManager() && !RaidPlugin.getInstance().getTeamManager().getTeam(player).isManager(player)) {
				player.sendMessage(ChatColor.RED + "Only team managers can do that!");
				return;
			}

			String[] newArgs = new String[args.length - 1];
			if (args.length > 1) {
				System.arraycopy(args, 1, newArgs, 0, args.length - 1);
			}

			cmd.onCommand(player, getLabel(), newArgs);
		}
	}
}
