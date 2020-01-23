package com.pvpraids.raid.tracking;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.raid.RaidPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TrackCommand extends PlayerCommand {
	public TrackCommand() {
		super("track");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length != 1) {
			player.sendMessage(ChatColor.RED + "Incorrect syntax. Use /track (player) or /track all.");
			return;
		}

		PlayerTrack tracker = new PlayerTrack(player);

		if (args[0].equalsIgnoreCase("all")) {
			tracker.track(null);
			return;
		}

		Player target = RaidPlugin.getInstance().getServer().getPlayer(args[0]);
		if (target == null || !target.isOnline()) {
			player.sendMessage(ChatColor.RED + args[0] + " is not online.");
			return;
		}

		if (target.getName().equalsIgnoreCase(player.getName())) {
			player.sendMessage(ChatColor.RED + "You cannot track yourself.");
			return;
		}

		tracker.track(target);
	}
}
