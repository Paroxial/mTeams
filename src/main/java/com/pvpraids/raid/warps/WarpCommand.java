package com.pvpraids.raid.warps;

import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.player.CoreProfile;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import com.pvpraids.raid.task.WarpTask;
import com.pvpraids.raid.util.LocationUtil;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WarpCommand extends PlayerCommand {
	private final RaidPlugin plugin;

	public WarpCommand(RaidPlugin plugin) {
		super("warp");
		this.plugin = plugin;
		setAliases("go");
	}

	@Override
	public void execute(Player player, String[] args) {
		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);

		if (rp.isSpawnProtected()) {
			player.sendMessage(CC.RED + "You can't warp while spawn protected!");
			return;
		}

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Incorrect syntax.");
			player.sendMessage(CC.GRAY + "/" + getLabel() + " set (name) - Set a warp");
			player.sendMessage(CC.GRAY + "/" + getLabel() + " delete (name) - Delete a warp");
			player.sendMessage(CC.GRAY + "/" + getLabel() + " (name) - Warp to one of your warps");
			player.sendMessage(CC.GRAY + "/" + getLabel() + " list - List all your warps");
			return;
		}

		if (args[0].equalsIgnoreCase("set")) {
			if (args.length != 2) {
				player.sendMessage(CC.RED + "Incorrect syntax. Use /" + getLabel() + " set (name)");
				return;
			}

			String name = args[1];
			if (name.equalsIgnoreCase("set") || name.equalsIgnoreCase("delete")
					|| name.equalsIgnoreCase("list") || name.equalsIgnoreCase("del")) {
				player.sendMessage(CC.RED + "That is an invalid warp name.");
				return;
			}

			if (name.isEmpty() || name.length() > 20) {
				player.sendMessage(CC.RED + "Warp names must be alphanumeric and up to 20 characters long.");
				return;
			}

			Location loc = player.getLocation();

			if (LocationUtil.isCloseToSpawn(loc)) {
				player.sendMessage(CC.RED + "You cannot set a warp this close to spawn.");
				return;
			}

			CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
			int warps = plugin.getWarpManager().getWarps(player).size();

			if (warps >= plugin.getWarpManager().getMaxWarpsByRank(profile)) {
				player.sendMessage(CC.RED + "Your rank only allows a maximum of " + plugin.getWarpManager().getMaxWarpsByRank(profile) + " warps!");
				return;
			}

			Warp warp = new Warp(name, loc);

			plugin.getWarpManager().addWarp(player, warp);

			player.sendMessage(CC.GRAY + "Warp \"" + name + "\" has been set.");
		} else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del")) {
			if (args.length != 2) {
				player.sendMessage(CC.RED + "Incorrect syntax. Use /" + getLabel() + " delete (name)");
				return;
			}

			Set<Warp> warpSet = plugin.getWarpManager().getWarps(player);

			Optional<Warp> warp = warpSet.stream().filter(other -> other.getName().equalsIgnoreCase(args[1])).findAny();
			if (!warp.isPresent()) {
				player.sendMessage(CC.RED + "Warp \"" + args[1] + "\" doesn't exist!");
				return;
			}

			plugin.getWarpManager().removeWarp(player, warp.get());
			player.sendMessage(CC.GRAY + "Warp \"" + args[1] + "\" has been deleted.");
		} else if (args[0].equalsIgnoreCase("list")) {
			Set<String> warps = plugin.getWarpManager().getWarps(player).stream().map(Warp::getName).collect(Collectors.toSet());

			if (warps.size() == 0) {
				player.sendMessage(CC.RED + "You have no warps.");
				return;
			}

			CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

			player.sendMessage(CC.GRAY + "***Warp List***" + CC.SECONDARY + "(" + warps.size() + "/" + plugin.getWarpManager().getMaxWarpsByRank(profile) + ")");
			player.sendMessage(CC.GRAY + warps.toString());
		} else {
			if (args.length != 1) {
				player.sendMessage(CC.RED + "Incorrect syntax. Use /" + getLabel() + " (name)");
				return;
			}

			Set<Warp> warpList = plugin.getWarpManager().getWarps(player);
			Warp warp = warpList.stream().filter(other -> other.getName().equalsIgnoreCase(args[0])).findAny().orElse(null);

			if (warp == null) {
				player.sendMessage(CC.RED + "Warp \"" + args[0] + "\" doesn't exist!");
				return;
			}

			new WarpTask(player, warp.getLocation());
		}
	}
}
