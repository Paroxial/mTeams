package com.pvpraids.raid.listeners;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import com.pvpraids.raid.util.NumberUtil;
import com.pvpraids.raid.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class MobCaptureListener implements Listener {
	private static final int MOB_LEVEL_CAPTURE = 5;
	private static final int MOOSHROOM_LEVEL_CAPTURE = 30;
	private static final int MOB_CAPTURE_MIN_DISTANCE = 5;
	private final RaidPlugin plugin;

	@EventHandler
	public void onShootEgg(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Egg)) {
			return;
		}

		LivingEntity target = (LivingEntity) e.getEntity();
		if (target instanceof Player) {
			return;
		}

		Egg egg = (Egg) e.getDamager();
		if (!(egg.getShooter() instanceof Player)) {
			return;
		}

		Player shooter = (Player) egg.getShooter();
		double targetHealth = target.getHealth();
		double playerHealth = shooter.getHealth();
		int experience = shooter.getLevel();
		double distance = shooter.getLocation().distance(target.getLocation());

		if (experience < MOB_LEVEL_CAPTURE) {
			shooter.sendMessage(ChatColor.RED + "You must be level " + MOB_LEVEL_CAPTURE + " to capture a mob!");
			return;
		}

		if (distance < MOB_CAPTURE_MIN_DISTANCE) {
			shooter.sendMessage(ChatColor.RED + "You must be at least " + MOB_CAPTURE_MIN_DISTANCE + " blocks away to capture a mob!");
			return;
		}

		if (targetHealth > 15) {
			shooter.sendMessage(ChatColor.RED + "The mob you're trying to capture is too strong! Weaken it first!");
			return;
		}

		if (target instanceof Ageable) {
			if (!((Ageable) target).isAdult()) {
				shooter.sendMessage(ChatColor.RED + "You can't capture babies!");
				return;
			}
		}

		int chance = 50;
		chance -= playerHealth;
		chance += targetHealth;
		chance -= (int) distance;
		chance -= experience;

		if (chance <= 0) {
			chance = 1;
		}

		EntityType mobType = target.getType();

		if (target instanceof MushroomCow) {
			if (experience < MOOSHROOM_LEVEL_CAPTURE) {
				shooter.sendMessage(ChatColor.RED + "You must be at least level " + MOOSHROOM_LEVEL_CAPTURE + " to capture a Mooshroom!");
				return;
			}

			shooter.setLevel(shooter.getLevel() - 30);
			shooter.sendMessage(ChatColor.RED + "The Mooshroom drained " + MOOSHROOM_LEVEL_CAPTURE + " levels of your experience!");
		} else {
			shooter.setLevel(shooter.getLevel() - 5);
			shooter.sendMessage(ChatColor.RED + "The " + StringUtil.formatEntityType(mobType) + " drained " + MOB_LEVEL_CAPTURE + " levels of your experience!");
		}

		if (NumberUtil.RANDOM.nextInt(chance) > 0) {
			shooter.sendMessage(ChatColor.RED + "Your capture attempt was unsuccessful.");
			if (mobType == EntityType.MUSHROOM_COW) {
				shooter.sendMessage(ChatColor.RED + "Mooshrooms are notoriously hard to catch.");
			} else {
				shooter.sendMessage(ChatColor.RED + "Having more experience levels makes success more likely.");
			}
			return;
		}

		target.remove();
		target.getLocation().getWorld().dropItemNaturally(target.getLocation(), new ItemStack(Material.MONSTER_EGG, 1, mobType.getTypeId()));
		shooter.playSound(shooter.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1.0F, 2.0F);
		shooter.sendMessage(ChatColor.GREEN + "You successfully captured a " + StringUtil.formatEntityType(mobType) + "! An egg has dropped at its location.");

		RaidPlayer rp = plugin.getPlayerManager().getPlayer(shooter);

		rp.getStats().setMooshroomsCapped(rp.getStats().getMooshroomsCapped() + 1);
	}
}
