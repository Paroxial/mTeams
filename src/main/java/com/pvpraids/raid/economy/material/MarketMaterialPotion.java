package com.pvpraids.raid.economy.material;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

@Getter
public class MarketMaterialPotion extends MarketMaterial {
	private Potion potion;

	public MarketMaterialPotion(int priority, Potion potion) {
		super(id(potion), priority, Material.POTION, (byte) 0);
		this.potion = potion;
	}

	private static String id(Potion p) {
		StringBuilder id = new StringBuilder("pot#");
		id.append(p.getType().name());
		id.append('#');
		id.append(p.getLevel());
		id.append('#');
		id.append(p.isSplash() ? 's' : 'n');
		id.append(p.hasExtendedDuration() ? 'e' : 'n');
		return id.toString();
	}

	@Override
	public boolean matches(ItemStack stack) {
		if (stack == null) {
			return false;
		}

		if (stack.getType() == Material.POTION) {
			Potion pot = Potion.fromItemStack(stack);
			return pot.equals(potion);
		} else {
			return false;
		}
	}

	@Override
	public ItemStack create(int amount) {
		return potion.toItemStack(amount);
	}
}