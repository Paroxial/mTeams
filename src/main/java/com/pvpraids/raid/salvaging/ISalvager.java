package com.pvpraids.raid.salvaging;

import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface ISalvager {
	List<ItemStack> salvageItem(ItemStack item);

	boolean canSalvage(ItemStack item, Block onBlock);
}
