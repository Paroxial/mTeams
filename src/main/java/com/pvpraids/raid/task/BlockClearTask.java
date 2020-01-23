package com.pvpraids.raid.task;

import com.pvpraids.raid.RaidPlugin;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class BlockClearTask extends BukkitRunnable {
	private static final long PLACED_EXPIRE_MILLIS = 1000 * 60 * 30;

	private final RaidPlugin plugin;

	@Override
	public void run() {
		Iterator<Block> blockIterator = plugin.getBlocksPlacedNearSpawn().keySet().iterator();

		while (blockIterator.hasNext()) {
			Block block = blockIterator.next();

			if (block.getType() == Material.AIR) {
				blockIterator.remove();
				continue;
			}

			long timePlaced = plugin.getBlocksPlacedNearSpawn().get(block);

			if (timePlaced + PLACED_EXPIRE_MILLIS <= System.currentTimeMillis()) {
				block.setType(Material.AIR);
				blockIterator.remove();
			}
		}
	}
}
