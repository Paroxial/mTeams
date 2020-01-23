package com.pvpraids.raid.listeners;

import com.pvpraids.core.utils.item.ItemBuilder;
import com.pvpraids.raid.util.ItemUtil;
import com.pvpraids.raid.util.PlayerUtil;
import com.pvpraids.raid.util.XPUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class XPListener implements Listener {
	public static final String XP_BOTTLE_LORE = ChatColor.YELLOW + "Right click to store or retrieve XP";
	public static final String XP_BOTTLE_EMPTY = ChatColor.GREEN + "Empty XP Bottle";
	private static final String XP_BOTTLE_SUFFIX = " Bottled XP";

	@EventHandler
	public void onUse(PlayerInteractEvent e) {
		if (!e.getAction().name().contains("RIGHT") || !e.hasItem() || !e.getItem().hasItemMeta()
				|| !e.getItem().getItemMeta().hasDisplayName()) {
			return;
		}

		ItemStack item = e.getItem();
		String name = item.getItemMeta().getDisplayName();

		if (!name.equals(XP_BOTTLE_EMPTY) && !name.endsWith(XP_BOTTLE_SUFFIX)) {
			return;
		}

		e.setCancelled(true);

		Player player = e.getPlayer();

		if (item.getType() == Material.GLASS_BOTTLE && name.equals(XP_BOTTLE_EMPTY)) {
			if (player.getExp() < 0.1 && player.getLevel() == 0) {
				player.sendMessage(ChatColor.RED + "You have no XP to bottle!");
				return;
			}

			ItemUtil.decrementStack(player.getInventory(), item);

			int amount = XPUtil.getXP(player);

			ItemStack newItem = new ItemBuilder(Material.EXP_BOTTLE).name(ChatColor.GREEN.toString() + amount + XP_BOTTLE_SUFFIX).lore(XP_BOTTLE_LORE).build();

			PlayerUtil.giveItems(player, newItem);

			player.sendMessage(ChatColor.GREEN + "Bottled " + amount + " XP!");

			XPUtil.setXP(player, 0);

			player.updateInventory();
		} else if (item.getType() == Material.EXP_BOTTLE && name.endsWith(XP_BOTTLE_SUFFIX)) {
			int amount = Integer.parseInt(ChatColor.stripColor(name).split(" ")[0]);

			XPUtil.addXP(player, amount);

			player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 2.0F);
			player.sendMessage(ChatColor.GREEN + "Unbottled " + amount + " XP!");

			ItemUtil.decrementStack(player.getInventory(), item);

			ItemStack newItem = new ItemBuilder(Material.GLASS_BOTTLE).name(XPListener.XP_BOTTLE_EMPTY).lore(XPListener.XP_BOTTLE_LORE).build();

			PlayerUtil.giveItems(player, newItem);

			player.updateInventory();
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory().getType() == InventoryType.ANVIL && e.getCursor().getType() == Material.EXP_BOTTLE) {
			if (e.getSlotType() == InventoryType.SlotType.CRAFTING) {
				e.setCancelled(true);
			}
		}
	}
}
