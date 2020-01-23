package com.pvpraids.raid.commands;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.utils.item.ItemBuilder;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.core.utils.timer.Timer;
import com.pvpraids.core.utils.timer.impl.IntegerTimer;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KitCommand extends PlayerCommand implements Listener {
	private static final List<String> kits = Arrays.asList("tracker", "pvp", "op");
	private Map<UUID, Timer> opTimers = new HashMap<>();
	private Map<UUID, Timer> tTimers = new HashMap<>();
	private Map<UUID, Timer> pvpTimers = new HashMap<>();

	public KitCommand() {
		super("kit");
		setUsage(CC.RED + "Usage: /kit <tracker|pvp|op>");
		Bukkit.getPluginManager().registerEvents(this, RaidPlugin.getInstance());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!opTimers.containsKey(event.getPlayer().getUniqueId()))
			opTimers.put(event.getPlayer().getUniqueId(), new IntegerTimer(TimeUnit.MINUTES, 15));
		if (!tTimers.containsKey(event.getPlayer().getUniqueId()))
			tTimers.put(event.getPlayer().getUniqueId(), new IntegerTimer(TimeUnit.MINUTES, 1));
		if (!pvpTimers.containsKey(event.getPlayer().getUniqueId()))
			pvpTimers.put(event.getPlayer().getUniqueId(), new IntegerTimer(TimeUnit.MINUTES, 1));
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return;
		}

		if (!kits.contains(args[0].toLowerCase())) {
			player.sendMessage(usageMessage);
			return;
		}

		PlayerInventory inventory = player.getInventory();
		RaidPlayer rp = RaidPlugin.getInstance().getPlayerManager().getPlayer(player);

		switch (args[0].toLowerCase()) {
			case "tracker":
				if (tTimers.get(player.getUniqueId()).isActive()) {
					player.sendMessage(CC.RED + "You can't use this for another " + tTimers.get(player.getUniqueId()).formattedExpiration());
					return;
				}

				inventory.addItem(new ItemStack(Material.OBSIDIAN, 64));
				inventory.addItem(new ItemStack(Material.OBSIDIAN, 64));
				inventory.addItem(new ItemStack(Material.OBSIDIAN, 64));
				inventory.addItem(new ItemStack(Material.DIAMOND_BLOCK, 1));
				inventory.addItem(new ItemStack(Material.GOLD_BLOCK, 4));
				break;
			case "pvp":
				if (pvpTimers.get(player.getUniqueId()).isActive()) {
					player.sendMessage(CC.RED + "You can't use this for another " + pvpTimers.get(player.getUniqueId()).formattedExpiration());
					return;
				}

				inventory.setArmorContents(new ItemStack[]{
						new ItemStack(Material.IRON_BOOTS),
						new ItemStack(Material.IRON_LEGGINGS),
						new ItemStack(Material.IRON_CHESTPLATE),
						new ItemStack(Material.IRON_HELMET)
				});
				inventory.addItem(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build());
				inventory.setItem(8, new ItemStack(Material.COOKED_BEEF, 64));

				for (int i = 0; i < inventory.getSize(); i++) {
					ItemStack item = inventory.getItem(i);

					if (item == null || item.getType() == Material.AIR) {
						inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
					}
				}

				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999, 1));
				break;
			case "op":
				if (opTimers.get(player.getUniqueId()).isActive()) {
					player.sendMessage(CC.RED + "You can't use this for another " + opTimers.get(player.getUniqueId()).formattedExpiration());
					return;
				}

				inventory.setArmorContents(new ItemStack[]{
						new ItemBuilder(Material.DIAMOND_BOOTS)
								.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
								.enchant(Enchantment.DURABILITY, 2)
								.enchant(Enchantment.PROTECTION_FALL, 4)
								.build(),
						new ItemBuilder(Material.DIAMOND_LEGGINGS)
								.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
								.enchant(Enchantment.DURABILITY, 2)
								.build(),
						new ItemBuilder(Material.DIAMOND_CHESTPLATE)
								.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
								.enchant(Enchantment.DURABILITY, 2)
								.build(),
						new ItemBuilder(Material.DIAMOND_HELMET)
								.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
								.enchant(Enchantment.DURABILITY, 2)
								.build()
				});
				inventory.addItem(new ItemBuilder(Material.DIAMOND_SWORD)
						.enchant(Enchantment.DAMAGE_ALL, 3)
						.enchant(Enchantment.FIRE_ASPECT, 2).build());
				inventory.setItem(8, new ItemStack(Material.COOKED_BEEF, 64));

				for (int i = 0; i < inventory.getSize(); i++) {
					ItemStack item = inventory.getItem(i);

					if (item == null || item.getType() == Material.AIR) {
						inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
					}
				}

				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 99999, 0));
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999, 1));
				break;
		}

		player.sendMessage(CC.GREEN + "Applied the " + args[0].toLowerCase() + " kit!");
		player.updateInventory();
	}
}
