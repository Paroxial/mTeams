package com.pvpraids.raid.economy;

import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.storage.database.MongoRequest;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.economy.material.MarketMaterial;
import com.pvpraids.raid.economy.material.Materials;
import com.pvpraids.raid.players.RaidPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class Market {
	private final RaidPlugin plugin;
	private final Map<MarketMaterial, Set<MarketItem>> items = new HashMap<>();
	private final MarketIO marketIO = new MarketIO();

	public void load() {
		for (MarketMaterial material : Materials.getAll()) {
			items.put(material, marketIO.read(material));
		}
	}

	public void save() {
		items.forEach((material, market) -> {
			if (material != null && market != null) {
				marketIO.write(material, market);
			}
		});
	}

	public void sell(Player seller, MarketMaterial product, int amount, long price) {
		RaidPlayer sellerRp = RaidPlugin.getInstance().getPlayerManager().getPlayer(seller);

		if (EconomyUtil.count(seller.getInventory(), product) < amount) {
			seller.sendMessage(ChatColor.RED + "You don't have " + amount + " " + product.toString() + " to sell!");
			return;
		}

		EconomyUtil.remove(seller.getInventory(), product, amount);
		seller.updateInventory();

		MarketItem item = new MarketItem(price / amount, sellerRp, product, amount);
		market(item.getMaterial()).add(item);
		seller.sendMessage(ChatColor.GOLD + "You put " + amount + " " + product.toString() + " up for sale at " + plugin.getEconomy().toGoldString(item.getValue()) + " each.");
	}

	public void buy(Player customer, MarketMaterial product, int amount, long max) {
		RaidPlayer customerRp = RaidPlugin.getInstance().getPlayerManager().getPlayer(customer);

		if (customerRp.getBalance() <= 0) {
			customer.sendMessage(ChatColor.RED + "You do not have any gold!");
			return;
		}

		Set<MarketItem> marketActual = market(product);
		List<MarketItem> market = new ArrayList<>(marketActual);

		if (market.size() == 0) {
			customer.sendMessage(ChatColor.RED + product.toString() + " is not on the market right now!");
			return;
		}

		Collections.sort(market);

		Map<MarketItem, Integer> list = new HashMap<>();
		long price = 0;

		while (!market.isEmpty() && price <= max && amount > 0) {
			MarketItem item = market.get(0);
			market.remove(0);

			int itemAmount = Math.min(item.getAmount(), amount);
			if (item.getValue() > 0) {
				price += itemAmount * item.getValue();
			}
			amount -= itemAmount;

			list.put(item, itemAmount);

			item.setAmount(item.getAmount() - itemAmount);
			if (item.getAmount() <= 0) {
				marketActual.remove(item);
			}
		}

		if (amount > 0) {
			customer.sendMessage(ChatColor.RED + "There isn't enough " + product.toString() + " available!");
			return;
		}

		if (price > max) {
			customer.sendMessage(ChatColor.RED + "There are enough " + product.toString() + " available, but they cost " + plugin.getEconomy().toGoldString(price - max) + " too much!");
			return;
		}

		if (price > customerRp.getBalance()) {
			customer.sendMessage(ChatColor.RED + "You can't afford that many " + product.toString() + ", they cost " + plugin.getEconomy().toGoldString(price - customerRp.getBalance()) + " too much!");
			return;
		}

		boolean floored = false;

		customer.sendMessage(ChatColor.GREEN + "Receipt: ");
		for (Map.Entry<MarketItem, Integer> buy : list.entrySet()) {
			MarketItem item = buy.getKey();
			int itemAmount = buy.getValue();
			long itemPrice = item.getValue() * itemAmount;
			RaidPlayer itemRp = item.getRaidPlayer();

			customer.sendMessage(ChatColor.GREEN + "Bought " + itemAmount + " " + product.toString() + " for " + plugin.getEconomy().toGoldString(itemPrice) + ".");

			customerRp.subtractBalance(itemPrice);

			if (itemRp != null) {
				itemRp.getPlayer().sendMessage(ChatColor.GOLD + customer.getName() + " bought " + itemAmount + " " + product.toString() + " for " + plugin.getEconomy().toGoldString(itemPrice) + ".");
				itemRp.addBalance(itemPrice);
			} else {
				plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
					Document ownerDoc = CorePlugin.getInstance().getMongoStorage().getDocument("team_players", item.getOwner());

					if (ownerDoc == null) {
						return;
					}

					long balance = ownerDoc.get("balance", 0L);

					MongoRequest.newRequest("team_players", item.getOwner())
							.put("balance", balance + itemPrice)
							.run();
				});
			}

			floored = floored || EconomyUtil.give(customer, product, itemAmount);
		}

		if (floored) {
			customer.sendMessage(ChatColor.GREEN + "Some items wouldn't fit in your inventory and had to be put on the floor.");
		}
	}

	public Set<MarketItem> market(MarketMaterial m) {
		Set<MarketItem> set = items.get(m);

		if (set == null) {
			items.put(m, set = new HashSet<>());
		}

		return set;
	}
}