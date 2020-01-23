package com.pvpraids.raid.tracking;

import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PlayerTrack {
	private static final int COBBLESTONE_DISTANCE = 25;
	private static final int STONE_DISTANCE = 25;
	private static final int OBSIDIAN_DISTANCE = 25;
	private static final int GOLD_DISTANCE = 25;
	private static final int IRON_DISTANCE = 100;
	private static final int EMERALD_DISTANCE = 250;

	private static final List<String> ALLOWED_TRACKING_WORLDS = Arrays.asList("world", "world_nether");

	private final Player player;
	private final Set<Block> blocksToDelete = new HashSet<>();

	public void track(Player target) {
		World world = player.getWorld();
		Location loc = player.getLocation();
		int x = loc.getBlockX();
		int y = loc.getBlockY() - 1;
		int z = loc.getBlockZ();
		Block center = world.getBlockAt(x, y, z);

		if (target == null && center.getType() == Material.OBSIDIAN) {
			player.sendMessage(CC.RED + "You must use a diamond center and gold ends to track everyone.");
			return;
		}

		if (center.getType() != Material.OBSIDIAN && center.getType() != Material.DIAMOND_BLOCK) {
			player.sendMessage(CC.RED + "Not a valid tracking compass.");
			return;
		}

		check(center, player, target, 0, -1);
		check(center, player, target, 0, 1);
		check(center, player, target, 1, 0);
		check(center, player, target, -1, 0);

		blocksToDelete.forEach(block -> block.setType(Material.AIR));
	}

	private void check(Block center, Player player, Player target, int xMod, int zMod) {
		int x = center.getX();
		int z = center.getZ();

		String direction = "";
		if (xMod == 1) {
			direction = "East";
		} else if (xMod == -1) {
			direction = "West";
		} else if (zMod == 1) {
			direction = "South";
		} else if (zMod == -1) {
			direction = "North";
		}

		Material endType = center.getType() == Material.DIAMOND_BLOCK ? Material.GOLD_BLOCK : Material.STONE;

		int distance = getTrackingDistance(center, xMod, zMod);
		if (distance == 0) {
			player.sendMessage(CC.RED + direction + " tracking point must end in " + (endType == Material.GOLD_BLOCK ? "gold" : "stone") + ".");
			return;
		}

		if (target != null && player.canSee(target)) {
			Location l = target.getLocation();
			if (isInTrackedWorld(target) && (isInRange(x, l.getBlockX(), distance * xMod) || isInRange(z, l.getBlockZ(), distance * zMod))) {
				player.sendMessage(getColorForPlayer(target) + target.getName() + CC.GREEN + CC.B + " is " + CC.R + CC.GRAY + "within " + distance + " blocks " + direction + " of here.");
			} else {
				player.sendMessage(CC.GRAY + target.getName() + CC.RED + CC.B + " is NOT " + CC.R + CC.GRAY + "within " + distance + " blocks " + direction + " of here.");
			}
		} else {
			StringBuilder result = new StringBuilder();
			for (Player targ : RaidPlugin.getInstance().getPlayerManager().getPlayers().keySet().stream().map(uuid -> RaidPlugin.getInstance().getServer().getPlayer(uuid)).collect(Collectors.toSet())) {
				if (targ == null) {
					continue;
				}
				if (targ.getName().equalsIgnoreCase(player.getName()) || !isInTrackedWorld(targ) || !player.canSee(targ)) {
					continue;
				}

				if ((xMod != 0 && isInRange(x, targ.getLocation().getBlockX(), distance * xMod)) || (zMod != 0 && isInRange(z, targ.getLocation().getBlockZ(), distance * zMod))) {
					result.append(CC.YELLOW).append(targ.getName()).append(CC.GRAY).append(", ");
				}
			}
			if (result.length() == 0) {
				player.sendMessage(CC.RED + "No players found within " + distance + " blocks " + direction + " of here.");
			} else {
				player.sendMessage(CC.GOLD + direction + CC.R + CC.GRAY + " (" + CC.YELLOW + distance + CC.GRAY + "): " + result.substring(0, result.length() - 2));
			}
		}
	}

	private int getTrackingDistance(Block center, int xMod, int zMod) {
		int x = center.getX();
		int y = center.getY();
		int z = center.getZ();

		Material centerType = center.getType();

		int distance = 0;
		int inc = 0;

		if (centerType == Material.OBSIDIAN) {
			Set<Block> blocksInArm = new HashSet<>();
			Block currentBlock;
			do {
				inc++;
				currentBlock = center.getWorld().getBlockAt(x + inc * xMod, y, z + inc * zMod);
				if (currentBlock.getType() == Material.COBBLESTONE) {
					distance += COBBLESTONE_DISTANCE;
				}
				blocksInArm.add(currentBlock);
			} while (currentBlock.getType() == Material.COBBLESTONE);

			if (currentBlock.getType() != Material.STONE) {
				return 0;
			}

			blocksToDelete.addAll(blocksInArm);
			blocksToDelete.add(center);
			distance += STONE_DISTANCE;

			return distance;
		}

		if (centerType == Material.DIAMOND_BLOCK) {
			Block currentBlock;
			do {
				inc++;
				currentBlock = center.getWorld().getBlockAt(x + inc * xMod, y, z + inc * zMod);
				if (currentBlock.getType() == Material.OBSIDIAN) {
					distance += OBSIDIAN_DISTANCE;
				}
				if (currentBlock.getType() == Material.IRON_BLOCK) {
					distance += IRON_DISTANCE;
				}
				if (currentBlock.getType() == Material.EMERALD_BLOCK) {
					distance += EMERALD_DISTANCE;
				}
			}
			while (currentBlock.getType() == Material.OBSIDIAN || currentBlock.getType() == Material.IRON_BLOCK || currentBlock.getType() == Material.EMERALD_BLOCK);

			if (currentBlock.getType() != Material.GOLD_BLOCK) {
				return 0;
			}

			distance += GOLD_DISTANCE;
			return distance;
		}

		return 0;
	}

	private boolean isInTrackedWorld(Player player) {
		return ALLOWED_TRACKING_WORLDS.stream().anyMatch(world -> world.equalsIgnoreCase(player.getWorld().getName()));
	}

	private boolean isInRange(int start, int target, int distance) {
		int end = start + distance;
		int max = Math.max(start, end);
		int min = Math.min(start, end);
		return target >= min && target <= max;
	}

	private String getColorForPlayer(Player player) {
		return player.getWorld().getName().equalsIgnoreCase("world_nether") ? CC.RED : CC.GRAY;
	}
}
