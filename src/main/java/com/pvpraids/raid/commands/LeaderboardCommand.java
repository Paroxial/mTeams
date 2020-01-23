package com.pvpraids.raid.commands;


import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.core.utils.time.TimeUtil;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.leaderboard.Leaderboard;
import com.pvpraids.raid.leaderboard.LeaderboardEntry;
import com.pvpraids.raid.leaderboard.LeaderboardStat;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

public class LeaderboardCommand extends PlayerCommand {
	private static final int PAGE_SIZE = 10;
	private final RaidPlugin plugin;

	public LeaderboardCommand(RaidPlugin plugin) {
		super("leaderboard");
		this.plugin = plugin;
		setAliases("leaderboards", "lb", "top10", "topstats");
		setUsage(CC.RED + "Usage: /leaderboard <stat> [page]");
	}

	private static int validIntegerOf(int max, String arg) {
		try {
			int i = Integer.parseInt(arg);

			if (i < 1) {
				return 1;
			} else if (i > max) {
				return max;
			} else {
				return i;
			}
		} catch (NumberFormatException ex) {
			return 1;
		}
	}

	private static String formatNumberWithCommas(long amount) {
		return String.format("%,d", amount);
	}

	private String formatStat(LeaderboardStat stat, long amount) {
		switch (stat) {
			case GOLD:
				return plugin.getEconomy().toGoldString(amount);
			case PLAYTIME:
				return TimeUtil.formatTimeSeconds(amount);
		}
		return String.valueOf(amount);
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return;
		}

		LeaderboardStat stat = LeaderboardStat.getByName(args[0]);

		if (stat == null) {
			player.sendMessage(CC.RED + "That's not a stat!");
			player.sendMessage(CC.GREEN + "Valid stats: " + Arrays.stream(LeaderboardStat.values())
					.map(stat1 -> stat1.name().toLowerCase()).collect(Collectors.joining(", ")));
			return;
		}

		Leaderboard leaderboard = plugin.getLeaderboardManager().getLeaderboardByStat(stat);

		if (leaderboard == null || leaderboard.getEntries().isEmpty()) {
			player.sendMessage(CC.RED + "The leaderboards are empty... Please wait until they have been updated.");
			return;
		}

		int size = leaderboard.getEntries().size();
		int maxPages = size % PAGE_SIZE == 0 ? size / PAGE_SIZE : size / PAGE_SIZE + 1;
		int pageIndex = args.length < 2 ? 1 : validIntegerOf(maxPages, args[1]);

		int index = (pageIndex - 1) * PAGE_SIZE;
		int count = index + 1;

		player.sendMessage(CC.PRIMARY + "Leaderboard for " + StringUtils.capitalize(stat.name().toLowerCase().replace("_", ""))
				+ CC.SECONDARY + " (" + pageIndex + " of " + maxPages + ")");
		player.sendMessage("");

		for (LeaderboardEntry entry : leaderboard.getEntries(index)) {
			if (count > index + (PAGE_SIZE + 1)) {
				break;
			}

			player.sendMessage(CC.PRIMARY + formatNumberWithCommas(count) + ". " + CC.PRIMARY
					+ entry.getDisplayName() + CC.GRAY + ": " + CC.SECONDARY + formatStat(stat, entry.getAmount()));

			count++;
		}
	}
}
