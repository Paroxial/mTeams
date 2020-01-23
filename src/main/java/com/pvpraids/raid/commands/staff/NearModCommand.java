package com.pvpraids.raid.commands.staff;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.player.rank.Rank;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.core.utils.message.Messages;
import com.pvpraids.core.utils.structure.Cuboid;
import com.pvpraids.core.utils.time.TimeUtil;
import com.pvpraids.raid.RaidPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.coreprotect.CoreProtectAPI;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunkBulk;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class NearModCommand extends PlayerCommand {
	public static final int NEAR_BLOCKS_RADIUS = 10;
	public static HashMap<String, HashSet<Chunk>> chunks = new HashMap<>();
	private static boolean locked = false;
	private static long dontRunUntil = System.currentTimeMillis();

	private static CoreProtectAPI cpapi = new CoreProtectAPI();

	public NearModCommand() {
		super("nearmod", Rank.ADMIN);
		setUsage(CC.RED + "/nearmod <reset|player> [time]");
		setAliases("nearblocks");
	}

	public static boolean hasRemoved(String user, Block block, int time) {
		boolean match = false;
		int stime = (int) (System.currentTimeMillis() / 1000L);
		List check = cpapi.blockLookup(block, time);
		Iterator var10 = check.iterator();

		while (var10.hasNext()) {
			String[] value = (String[]) var10.next();
			CoreProtectAPI.ParseResult result = cpapi.parseResult(value);
			if (user.equalsIgnoreCase(result.getPlayer()) && result.getActionId() == 0 && result.getTime() <= stime) {
				match = true;
				break;
			}
		}

		return match;
	}

	@Override
	public void execute(Player sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(usageMessage);
			return;
		}

		if (args[0].equalsIgnoreCase("reset")) {
			sender.removeMetadata("nearmod", RaidPlugin.getInstance());
			HashSet<Chunk> cs = NearModCommand.chunks.remove(sender.getName());

			if (cs != null) {
				ArrayList<Chunk> chunks = new ArrayList<>(cs);

				PacketPlayOutMapChunkBulk cb = new PacketPlayOutMapChunkBulk(chunks);

				((CraftPlayer) sender).getHandle().playerConnection.sendPacket(cb);
				sender.sendMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 50));
				sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + " ╚>§e Your movement packets are now resumed.");
				sender.sendMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 50));

				for (Chunk ch : chunks) {
					for (Entity e : ch.bukkitChunk.getEntities()) {
						if (e instanceof Player) {
							Player other = (Player) e;
							sender.showPlayer(other);
						}
					}
				}

			}
			return;
		}

		if (args.length < 2) {
			sender.sendMessage(CC.RED + "Specify a time.");
			return;
		}

		Player targetPlayer = Bukkit.getPlayer(args[0]);

		if (targetPlayer == null) {
			sender.sendMessage(Messages.PLAYER_NOT_FOUND);
			return;
		}

		String modName = targetPlayer.getName();
		String time = args[1];

		int seconds = (int) (TimeUtil.parseTime(time) / 1000L);

		if (seconds <= 0) {
			sender.sendMessage(ChatColor.RED + "Could not parse time: " + time);
			return;
		}

		if (locked) {
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + " ╚>§e The block database is currently processing another request");
			return;
		}


		if (dontRunUntil > System.currentTimeMillis()) {
			int cooldownSeconds = (int) ((dontRunUntil - System.currentTimeMillis()) / 1000);
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + " ╚>§e This is a very resource intensive command!");
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + " ╚>§e You cannot run this for another §c" + TimeUtil.formatTimeSeconds(cooldownSeconds));
			return;
		}

		dontRunUntil = System.currentTimeMillis() + 15_000;
		locked = true;

		sender.setMetadata("nearmod", new FixedMetadataValue(RaidPlugin.getInstance(), "nearmod"));

		Bukkit.getServer().getScheduler().runTaskAsynchronously(RaidPlugin.getInstance(), () -> {
			Location l1 = sender.getLocation().clone().add(NEAR_BLOCKS_RADIUS, 0, NEAR_BLOCKS_RADIUS);
			l1.setY(sender.getLocation().getY() - 32);
			Location l2 = sender.getLocation().clone().subtract(NEAR_BLOCKS_RADIUS, 0, NEAR_BLOCKS_RADIUS);
			l2.setY(sender.getLocation().getY() + 32);

			Cuboid cb = new Cuboid(l1, l2);
			HashMap<Location, Integer> blockType = new HashMap<>();

			sender.sendMessage(ChatColor.YELLOW + "Searching for blocks removed by §a" + modName + " in §d"
					+ NEAR_BLOCKS_RADIUS + "§ex§d" + NEAR_BLOCKS_RADIUS + " §ewithin §b" + TimeUtil.formatTimeSeconds(seconds) + "§e...");
			sender.sendMessage(ChatColor.YELLOW + "Y-Value: " + l1.getBlockY() + " §d< §ey" + "§d < §e" + l2.getBlockY());

			int count = 0;

			for (Block b : cb) {
				Location mod = b.getLocation();

				boolean removed = hasRemoved(modName, b, seconds);

				if (!chunks.containsKey(sender.getName())) {
					chunks.put(sender.getName(), new HashSet<>());
				}

				chunks.get(sender.getName()).add(((CraftChunk) b.getChunk()).getHandle());

				if (removed) {
					count++;
					blockType.put(mod, Material.SPONGE.getId());
				} else {
					blockType.put(mod, 0);
				}
			}

			sender.sendMessage(ChatColor.YELLOW + "" + count + " removals found, sending block-change packets.");

			sender.sendMessage("");

			sender.sendMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 50));
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + " ╚>§e You are now in a restricted move-packet state!");
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + " ╚>§e Your body will stay stationary while your camera moves.");
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + " ╚>§e This enables you to packet through the fake blocks");
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + " ╚>§e To move properly, please execute §b/nearmod reset§e.");
			sender.sendMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 50));
			sender.sendMessage("");

			for (Map.Entry<Location, Integer> entry : blockType.entrySet()) {
				sender.sendBlockChange(entry.getKey(), entry.getValue(), (byte) 0);
			}

			locked = false;
		});
	}

}
