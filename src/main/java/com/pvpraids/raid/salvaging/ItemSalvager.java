package com.pvpraids.raid.salvaging;

import com.pvpraids.raid.RaidPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

@AllArgsConstructor
public class ItemSalvager implements ISalvager {
	private Material material;
	private Material requiredBlockType;
	private Map<Material, Integer> ingredients;

	public ItemSalvager(RaidPlugin plugin, Material material, Material requiredBlock) {
		this.material = material;
		this.requiredBlockType = requiredBlock;
		this.ingredients = new HashMap<>();

		List<Recipe> recipeList = plugin.getServer().getRecipesFor(new ItemStack(material, 1));

		recipeList.forEach(recipe -> {
			if (recipe instanceof ShapedRecipe) {
				for (ItemStack itemStack : ((ShapedRecipe) recipe).getIngredientMap().values()) {
					if (itemStack == null) {
						continue;
					}
					if (ingredients.get(itemStack.getType()) != null) {
						ingredients.put(itemStack.getType(), ingredients.get(itemStack.getType()) + 1);
					} else {
						ingredients.put(itemStack.getType(), 1);
					}
				}
			}
		});
	}

	@Override
	public List<ItemStack> salvageItem(ItemStack item) {
		List<ItemStack> salvagedItems = new ArrayList<>();
		float durabilityFactor = 1f;

		if (material.getMaxDurability() > 0) {
			durabilityFactor = (material.getMaxDurability() - item.getDurability()) / (float) material.getMaxDurability();
		}

		for (Map.Entry<Material, Integer> entry : ingredients.entrySet()) {
			Material material = entry.getKey();
			Integer amount = entry.getValue();

			int amountToGive = Math.round((amount * durabilityFactor) * (float) item.getAmount());
			if (amountToGive > 0) {
				salvagedItems.add(new ItemStack(material, amountToGive));
			}
		}

		return salvagedItems;
	}

	@Override
	public boolean canSalvage(ItemStack item, Block onBlock) {
		return item.getType() == material && onBlock.getType() == requiredBlockType;
	}
}