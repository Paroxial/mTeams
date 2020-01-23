package com.pvpraids.raid.economy;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.economy.material.MarketMaterial;
import com.pvpraids.raid.economy.material.Materials;
import com.pvpraids.raid.util.NumberUtil;
import java.io.File;
import lombok.Getter;

@Getter
public class Economy {
	public static final long UNIT_GOLD = 1000;
	public static final MarketMaterial UNIT_GOLD_MATERIAL = Materials.get("gold_ingot");
	public static final File DIRECTORY = new File(RaidPlugin.getInstance().getDataFolder().getPath() + "/economy");
	public static boolean ENABLED = true;

	static {
		// Make sure our directory exists.
		DIRECTORY.mkdirs();
	}

	private Market market;

	public Economy(RaidPlugin plugin) {
		market = new Market(plugin);
		market.load();

		plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
			plugin.getLogger().info("Performing automatic Economy save...");
			market.save();
			plugin.getLogger().info("Saved Economy.");
		}, 20 * 60 * 6, 20 * 60 * 10);
	}

	public double toGold(long value) {
		if (value > Double.MAX_VALUE || value < Double.MIN_VALUE) {
			return (double) value / (double) UNIT_GOLD;
		} else {
			return NumberUtil.roundOff(((double) value / (double) UNIT_GOLD), 5);
		}
	}

	public String toGoldString(long value) {
		return toGold(value) + "G";
	}

	public long toValue(double gold) {
		return (long) (gold * UNIT_GOLD);
	}
}