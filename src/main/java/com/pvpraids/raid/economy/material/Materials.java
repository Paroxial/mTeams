package com.pvpraids.raid.economy.material;

import com.pvpraids.raid.RaidPlugin;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 * TODO: Clean this aids
 */
public class Materials {
	private static final Map<String, MarketMaterial> MATERIALS = new HashMap<>();
	private static final Set<MarketMaterial> MATERIALS_ALL = new HashSet<>();

	static {
		for (Material material : Material.values()) {
			// Add every native Bukkit Material.
			alias(material.name(), new MarketMaterial(0, material, (byte) 0));
		}

		// Potions.
		for (PotionType type : PotionType.values()) {
			if (type == PotionType.WATER) {
				continue;
			}

			for (int level = 1; level != 3; level++) {
				String name = type.name() + "_" + level;

				try {
					Potion normal = new Potion(type, level);
					alias(name, new MarketMaterialPotion(1, normal));
				} catch (Exception e) {
				}

				try {
					Potion splash = new Potion(type, level).splash();
					alias(name + "_SPLASH", new MarketMaterialPotion(1, splash));
				} catch (Exception e) {
				}

				try {
					Potion extended = new Potion(type, level).extend();
					alias(name + "_EXTENDED", new MarketMaterialPotion(1, extended));
				} catch (Exception e) {
				}

				try {
					Potion splashExtended = new Potion(type, level).splash().extend();
					alias(name + "_SPLASH_EXTENDED", new MarketMaterialPotion(1, splashExtended));
				} catch (Exception e) {
				}
			}
		}

		// Anvil types.
		alias("damaged_anvil", new MarketMaterial(1, Material.ANVIL, (byte) 1));
		alias("badly_damaged_anvil", new MarketMaterial(1, Material.ANVIL, (byte) 2));

		// Dirt types.
		alias("coarse_dirt", new MarketMaterial(1, Material.DIRT, (byte) 1));
		alias("podzol", new MarketMaterial(1, Material.DIRT, (byte) 2));

		// Sand.
		alias("red_sand", new MarketMaterial(1, Material.SAND, (byte) 1));

		// Fish.
		alias("pufferfish", new MarketMaterial(1, Material.RAW_FISH, (byte) 3));

		// Golden Apples.
		alias("enchanted_golden_apple", new MarketMaterial(1, Material.GOLDEN_APPLE, (byte) 1));

		// Log types.
		alias("log_oak", new MarketMaterial(1, Material.LOG, (byte) 0));
		alias("log_spruce", new MarketMaterial(1, Material.LOG, (byte) 1));
		alias("log_birch", new MarketMaterial(1, Material.LOG, (byte) 2));
		alias("log_jungle", new MarketMaterial(1, Material.LOG, (byte) 3));
		alias("log_acacia", new MarketMaterial(1, Material.LOG_2, (byte) 0));
		alias("log_darkoak", new MarketMaterial(1, Material.LOG_2, (byte) 1));

		// Plank types.
		alias("planks_oak", new MarketMaterial(1, Material.WOOD, (byte) 0));
		alias("planks_spruce", new MarketMaterial(1, Material.WOOD, (byte) 1));
		alias("planks_birch", new MarketMaterial(1, Material.WOOD, (byte) 2));
		alias("planks_jungle", new MarketMaterial(1, Material.WOOD, (byte) 3));
		alias("planks_acacia", new MarketMaterial(1, Material.WOOD, (byte) 4));
		alias("planks_darkoak", new MarketMaterial(1, Material.WOOD, (byte) 5));

		// Dyes.
		for (DyeColor dye : DyeColor.values()) {
			alias(dye.name() + "_wool", new MarketMaterial(1, Material.WOOL, dye.getWoolData()));
			alias(dye.name() + "_dye", new MarketMaterial(1, Material.INK_SACK, dye.getDyeData()));
		}

		// Mob eggs.
		for (EntityType type : EntityType.values()) {
			alias("SPAWN_" + type.name(), new MarketMaterial(1, Material.MONSTER_EGG, (byte) type.getTypeId()));
		}

		// General aliases.
		alias("log_oak", "log", "tree");
		alias("planks_oak", "planks", "wood");
		alias("cobblestone", "cobble", "cobbl", "coble");
		alias("nether_stalk", "nether_wart", "wart");

		// Potion aliases.
		alias("strength_1", "str", "strp", "str1", "strp1");
		alias("strength_1_splash", "strs", "strps", "str1s", "strp1s");
		alias("strength_2", "str2", "strp2");
		alias("strength_2_splash", "str2s", "strp2s");
		alias("speed_2", "swp2");
		alias("speed_1", "swp", "swp1");
		alias("speed_2_splash", "swp2s");
		alias("speed_1_splash", "swps", "swp1s");
		alias("fire_resistance_1", "fres");

		// Egg aliases.
		alias("spawn_mushroom_cow", "moosh");
		alias("spawn_mushroom_cow", "moosh_egg");

		File aliases = new File(RaidPlugin.getInstance().getDataFolder().getPath() + "/aliases");
		aliases.getParentFile().mkdirs();
		aliases.mkdirs();

		String path = aliases.getAbsolutePath() + "/market.csv";

		File f = new File(path);

		f.getParentFile().mkdirs();
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!f.exists()) {
			Bukkit.getLogger().warning("No aliases file found for market items.");
		} else {
			try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
				String line;

				while ((line = reader.readLine()) != null) {
					String[] parts = line.split(",");
					String alias = parts[0];

					try {
						int blockId = Integer.parseInt(parts[1]);
						int blockData = Integer.parseInt(parts[2]);

						alias(alias, new MarketMaterial(1, Material.getMaterial(blockId), (byte) blockData));
					} catch (NumberFormatException e) {
						Bukkit.getLogger().severe("Error loading aliases: could not parse block data");
					}
				}
			} catch (IOException e) {
				Bukkit.getLogger().severe("Error loading aliases:");
				e.printStackTrace();
			}
		}
	}

	public static void add(MarketMaterial material) {
		MATERIALS_ALL.add(material);
	}

	public static MarketMaterial get(ItemStack stack) {
		MarketMaterial result = null;

		for (MarketMaterial mat : MATERIALS.values()) {
			if (mat.matches(stack)) {
				if (result == null || (mat.getPriority() >= result.getPriority())) {
					result = mat;
				}
			}
		}

		return result;
	}

	public static MarketMaterial get(String alias) {
		if (alias == null || alias.length() == 0) {
			return null;
		}

		return MATERIALS.get(alias.toLowerCase().trim());
	}

	public static MarketMaterial[] getAll() {
		return MATERIALS_ALL.toArray(new MarketMaterial[]{});
	}

	public static MarketMaterial[] getAllNamed() {
		Set<MarketMaterial> set = new HashSet<>(MATERIALS.values());
		return set.toArray(new MarketMaterial[]{});
	}

	private static void alias(String masked, String... aliases) {
		for (String a : aliases) {
			alias(a, get(masked));
		}
	}

	private static void alias(String alias, MarketMaterial material) {
		MATERIALS.put(alias.toLowerCase(), material);
		material.setName((material.getName() == null && !material.getId().equalsIgnoreCase(alias)) ? alias : material.getName());
	}
}