package com.pvpraids.raid.util;

import com.pvpraids.raid.RaidPlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

@UtilityClass
public final class LocationUtil {
	public final World WORLD = RaidPlugin.getInstance().getServer().getWorld("world");

	public final int BORDER = 10 * 1000;
	public final int SPAWN_POS_BOUND = 65;
	public final int SPAWN_ANTIBUILD_POS_BOUND = SPAWN_POS_BOUND + 75;
	public final int SPAWN_NEG_BOUND = -64;
	public final int SPAWN_ANTIBUILD_NEG_BOUND = SPAWN_NEG_BOUND - 75;
	public final int BLOCKS_CLEAR = 140;
	private final int DEAD_ZONE = 512; // number of blocks from spawn in which players can't set warps/home/rally/hq

	public String locationToString(Location loc) {
		return loc == null ? "null" : loc.getWorld().getName() +
				", " +
				loc.getX() +
				", " +
				loc.getY() +
				", " +
				loc.getZ() +
				", " +
				loc.getYaw() +
				", " +
				loc.getPitch();
	}

	public Location stringToLocation(String loc) {
		if (loc.equals("null")) {
			return null;
		}

		String[] parts = loc.split(", ");
		World world = RaidPlugin.getInstance().getServer().getWorld(parts[0]);
		double x = Double.parseDouble(parts[1]);
		double y = Double.parseDouble(parts[2]);
		double z = Double.parseDouble(parts[3]);
		float yaw = Float.parseFloat(parts[4]);
		float pitch = Float.parseFloat(parts[5]);
		return new Location(world, x, y, z, yaw, pitch);
	}

	public boolean hasBlockTypeTouching(Block block, Material type) {
		if (block.getRelative(BlockFace.EAST).getType() == type) {
			return true;
		}
		if (block.getRelative(BlockFace.WEST).getType() == type) {
			return true;
		}
		if (block.getRelative(BlockFace.NORTH).getType() == type) {
			return true;
		}
		if (block.getRelative(BlockFace.SOUTH).getType() == type) {
			return true;
		}
		if (block.getRelative(BlockFace.UP).getType() == type) {
			return true;
		}
		return block.getRelative(BlockFace.DOWN).getType() == type;
	}

	public boolean isWithinSpawn(Location l) {
		if (l == null) {
			return false;
		}
		if (!l.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName())) {
			return false;
		}
		return l.getX() <= SPAWN_POS_BOUND && l.getX() >= SPAWN_NEG_BOUND && l.getZ() <= SPAWN_POS_BOUND && l.getZ() >= SPAWN_NEG_BOUND;
	}

	public boolean isWithinSpawnAntiBreak(Location l) {
		if (l == null) {
			return false;
		}
		if (!l.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName())) {
			return false;
		}
		return l.getX() <= SPAWN_ANTIBUILD_POS_BOUND && l.getX() >= SPAWN_ANTIBUILD_NEG_BOUND
				&& l.getZ() <= SPAWN_ANTIBUILD_POS_BOUND && l.getZ() >= SPAWN_ANTIBUILD_NEG_BOUND;
	}

	public boolean isCloseToSpawn(Location l, int radius) {
		if (l == null) {
			return false;
		}
		if (!l.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName())) {
			return false;
		}
		int x = Math.abs(l.getBlockX() - RaidPlugin.getInstance().getSpawn().getBlockX());
		int z = Math.abs(l.getBlockZ() - RaidPlugin.getInstance().getSpawn().getBlockZ());
		return x <= radius && z <= radius;
	}

	public boolean isCloseToSpawn(Location l) {
		return isCloseToSpawn(l, DEAD_ZONE);
	}
}
