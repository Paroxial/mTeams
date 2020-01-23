package com.pvpraids.raid.listeners.fixes;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StrengthFixListener implements Listener {
	private static final double STRENGTH_PER_LEVEL = 1 * 2;

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getDamager();

		for (PotionEffect effect : player.getActivePotionEffects()) {
			if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
				ItemStack heldItem = player.getItemInHand() != null ? player.getItemInHand() : new ItemStack(Material.AIR);

				int sharpnessLevel = heldItem.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
				int strengthLevel = effect.getAmplifier() + 1;

				double totalDamage = event.getDamage();
				double weaponDamage = (totalDamage - 1.25 * sharpnessLevel) / (1.0 + 1.3 * strengthLevel) - 1.0;
				double finalDamage = 1.0 + weaponDamage + 1.25 * sharpnessLevel + STRENGTH_PER_LEVEL * strengthLevel;

				event.setDamage(finalDamage);
				break;
			}
		}
	}
}
