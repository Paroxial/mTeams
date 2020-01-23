package com.pvpraids.raid.commands;

import com.pvpraids.core.commands.BaseCommand;
import com.pvpraids.core.player.rank.Rank;
import com.pvpraids.core.utils.item.ItemBuilder;
import com.pvpraids.core.utils.message.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class TestKitCommand extends BaseCommand {
	public TestKitCommand() {
		super("testkit", Rank.ADMIN);
		setUsage(CC.RED + "Usage: /testkit <kit> <player>");
	}

	@Override
	protected void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(usageMessage);
			return;
		}

		String kit = args[0].toLowerCase();
		Player target = args.length < 2 || Bukkit.getPlayer(args[1]) == null ? null : Bukkit.getPlayer(args[1]);

		if (target == null) {
			sender.sendMessage("You must specify a target.");
			return;
		}

		PlayerInventory inv = target.getInventory();

		inv.clear();
		inv.setArmorContents(null);
		target.getActivePotionEffects().forEach(potionEffect -> target.removePotionEffect(potionEffect.getType()));
		target.setHealth(target.getMaxHealth());
		target.setFoodLevel(20);
		target.setSaturation(5.0F);

		switch (kit) {
			case "1":
			case "normal":
				inv.addItem(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build());

				for (int i = 0; i < inv.getSize(); i++) {
					inv.addItem(new ItemStack(Material.MUSHROOM_SOUP));
				}

				inv.setHelmet(new ItemStack(Material.IRON_HELMET));
				inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
				inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
				inv.setBoots(new ItemStack(Material.IRON_BOOTS));

				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 64));

				target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, 1));

				target.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999, 1));
				break;
			case "2":
			case "nostrength":
				inv.addItem(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build());

				for (int i = 0; i < inv.getSize(); i++) {
					inv.addItem(new ItemStack(Material.MUSHROOM_SOUP));
				}

				inv.setHelmet(new ItemStack(Material.IRON_HELMET));
				inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
				inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
				inv.setBoots(new ItemStack(Material.IRON_BOOTS));

				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 64));

				target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, 1));
				break;
			case "3":
			case "potion":
				inv.setHelmet(new ItemBuilder(Material.DIAMOND_HELMET)
						.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
						.enchant(Enchantment.DURABILITY, 3)
						.build());
				inv.setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE)
						.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
						.enchant(Enchantment.DURABILITY, 3)
						.build());
				inv.setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS)
						.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
						.enchant(Enchantment.DURABILITY, 3)
						.build());
				inv.setBoots(new ItemBuilder(Material.DIAMOND_BOOTS)
						.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
						.enchant(Enchantment.DURABILITY, 3)
						.enchant(Enchantment.PROTECTION_FALL, 3)
						.build());

				inv.addItem(new ItemBuilder(Material.DIAMOND_SWORD)
						.enchant(Enchantment.DAMAGE_ALL, 5)
						.enchant(Enchantment.FIRE_ASPECT, 2)
						.enchant(Enchantment.DURABILITY, 3)
						.build());

				inv.addItem(new ItemBuilder(Material.POTION).durability(8226).build());
				inv.addItem(new ItemBuilder(Material.POTION).durability(8226).build());
				inv.addItem(new ItemBuilder(Material.POTION).durability(8226).build());
				inv.addItem(new ItemBuilder(Material.POTION).durability(8259).build());

				Potion potion = new Potion(PotionType.INSTANT_HEAL);
				potion.setLevel(2);
				potion.setSplash(true);

				for (int i = 0; i < inv.getSize(); i++) {
					inv.addItem(potion.toItemStack(1));
				}

				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 64));
				break;
			default:
				sender.sendMessage(CC.RED + "Invalid kit. Valid kits: normal, nostrength, potion");
				return;
		}


		target.sendMessage(CC.GREEN + "You have been given kit " + kit + ".");
		sender.sendMessage(CC.GREEN + "Applied kit " + kit + " to " + target.getDisplayName() + CC.GREEN + ".");
	}
}
