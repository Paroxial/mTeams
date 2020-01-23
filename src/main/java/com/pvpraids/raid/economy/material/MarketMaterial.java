package com.pvpraids.raid.economy.material;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.util.ItemUtil;
import java.io.File;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecated")
@Getter
public class MarketMaterial {
	protected File file;
	@Setter
	private String name;
	private String id;
	private int priority;
	private Material material;
	private byte data;

	public MarketMaterial(int priority, Material material, byte data) {
		this((material.name() + "#" + data), priority, material, data);
	}

	public MarketMaterial(String id, int priority, Material material, byte data) {
		this.priority = priority;
		this.material = material;
		this.data = data;

		this.id = id.toLowerCase();

		File econ = new File(RaidPlugin.getInstance().getDataFolder().getPath() + "/economy");
		econ.getParentFile().mkdirs();
		econ.mkdirs();

		String path = econ.getAbsolutePath() + "/" + this.id + ".mrkt";

		File f = new File(path);

		f.getParentFile().mkdirs();
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.file = f;

		Materials.add(this);
	}

	public boolean matches(ItemStack stack) {
		// Do not match null.
		if (stack == null) {
			return false;
		}
		// Only match correct Material.
		if (stack.getType() != material) {
			return false;
		}
		// Do not match empty stacks.
		if (stack.getAmount() == 0) {
			return false;
		}
		// Do not match enchanted items.
		if (ItemUtil.hasEnchantment(stack)) {
			return false;
		}
		// Do not match incorrect data.
		if (hasData() && stack.getData().getData() != data) {
			return false;
		}
		// Do not match damaged items.
		return stack.getDurability() == 0 || material.getMaxDurability() == 0;// If we reach this point, the ItemStack matches!
	}

	@Override
	public String toString() {
		return StringUtils.capitalize((name == null ? id : name).toLowerCase()).replace("_", " ");
	}

	private boolean hasData() {
		return priority > 0;
	}

	public ItemStack create(int amount) {
		return new ItemStack(material, amount, data);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MarketMaterial) {
			return ((MarketMaterial) (obj)).id.equals(id);
		}
		return false;
	}
}