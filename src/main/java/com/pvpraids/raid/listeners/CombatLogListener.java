package com.pvpraids.raid.listeners;

import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.player.CoreProfile;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.util.XPUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@RequiredArgsConstructor
public class CombatLogListener implements Listener {
	private final RaidPlugin plugin;
	private final Map<Integer, CombatLogger> combatLoggers = new HashMap<>();
	private final Set<UUID> killedLoggers = new HashSet<>();

	@EventHandler(priority = EventPriority.LOW)
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
		RaidPlayer playerData = plugin.getPlayerManager().getPlayer(player);

		if (playerData == null || playerData.isSpawnProtected() || profile.isVanished()) {
			return;
		}

		List<Player> nearbyPlayers = player.getNearbyEntities(32.0, 32.0, 32.0).stream()
				.filter(Player.class::isInstance)
				.map(Player.class::cast)
				.filter(player::canSee)
				.filter(other -> !plugin.getPlayerManager().getPlayer(other).isSpawnProtected())
				.filter(other -> plugin.getTeamManager().getTeam(player) == null || !plugin.getTeamManager().getTeam(player).isOnTeam(other))
				.collect(Collectors.toList());

		if (playerData.hasCombatTag() || nearbyPlayers.size() > 0) {
			Entity entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);

			entity.setCustomName(player.getDisplayName());
			entity.setCustomNameVisible(true);

			PlayerInventory playerInventory = player.getInventory();

			List<ItemStack> itemsToDrop = new ArrayList<>();

			for (ItemStack item : playerInventory.getContents()) {
				if (item != null && item.getType() != Material.AIR) {
					itemsToDrop.add(item);
				}
			}

			for (ItemStack item : playerInventory.getArmorContents()) {
				if (item != null && item.getType() != Material.AIR) {
					itemsToDrop.add(item);
				}
			}

			CombatLogger logger = new CombatLogger(player.getUniqueId(), itemsToDrop, XPUtil.getXP(player), entity);

			combatLoggers.put(entity.getEntityId(), logger);

			if (nearbyPlayers.size() > 0) {
				for (Player nearbyPlayer : nearbyPlayers) {
					nearbyPlayer.sendMessage(player.getDisplayName() + CC.RED + " has combat logged! An entity has spawned at their location.");
				}
			}

			// remove the combat logger 30 seconds later
			plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
				if (!entity.isDead()) {
					removeVillagerLogger(entity);
				}
			}, 30 * 20L);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onLoggerDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}

		CombatLogger logger = combatLoggers.get(event.getEntity().getEntityId());

		if (logger == null) {
			return;
		}

		Player damager = null;

		if (event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();

			if (projectile.getShooter() instanceof Player) {
				damager = (Player) projectile.getShooter();
			}
		}

		if (damager == null) {
			return;
		}

		Team damagerTeam = plugin.getTeamManager().getTeam(damager);

		if (damagerTeam != null && damagerTeam.isOnTeam(logger.getId())) {
			event.setCancelled(true);
			damager.sendMessage(CC.RED + "You can't kill your own team's combat logger!");
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onLoggerKill(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		CombatLogger logger = combatLoggers.remove(entity.getEntityId());

		if (logger == null) {
			return;
		}

		killedLoggers.add(logger.getId());

		for (ItemStack armor : logger.getItems()) {
			entity.getWorld().dropItemNaturally(entity.getLocation(), armor);
		}

		int xp = logger.getXp();

		if (xp > 0) {
			ExperienceOrb orb = (ExperienceOrb) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.EXPERIENCE_ORB);
			orb.setExperience(xp);
		}

		Player player = event.getEntity().getKiller();

		if (player != null) {
			player.sendMessage(CC.GREEN + "You killed a combat logger! Their items and experience have dropped on the ground.");
		}
	}

	@EventHandler
	public void onLoggerReJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (killedLoggers.remove(player.getUniqueId())) {
			PlayerInventory playerInventory = player.getInventory();

			playerInventory.clear();
			playerInventory.setArmorContents(null);

			XPUtil.setXP(player, 0);

			if (!player.isDead()) {
				player.setHealth(0.0);
			}

			player.sendMessage(CC.RED + "You were killed for combat logging.");
		} else {
			CombatLogger logger = combatLoggers.values().stream().filter(filtered -> filtered.getId().equals(player.getUniqueId())).findAny().orElse(null);

			if (logger != null) {
				Entity entity = logger.getEntity();

				if (!entity.isDead()) {
					removeVillagerLogger(entity);
				}
			}
		}
	}

	@EventHandler
	public void onDisable(PluginDisableEvent event) {
		combatLoggers.values().stream().map(CombatLogger::getEntity).forEach(Entity::remove);
	}

	private void removeVillagerLogger(Entity entity) {
		combatLoggers.remove(entity.getEntityId());
		entity.remove();
	}

	@RequiredArgsConstructor
	@Getter
	private static final class CombatLogger {
		private final UUID id;
		private final List<ItemStack> items;
		private final int xp;
		private final Entity entity;
	}
}
