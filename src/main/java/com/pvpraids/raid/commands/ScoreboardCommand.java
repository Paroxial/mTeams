package com.pvpraids.raid.commands;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import org.bukkit.entity.Player;

public class ScoreboardCommand extends PlayerCommand {
	private final RaidPlugin plugin;

	public ScoreboardCommand(RaidPlugin plugin) {
		super("scoreboard");
		this.plugin = plugin;
		setAliases("sb", "board", "togglescoreboard", "toggleboard", "tsb", "toggle");
	}

	@Override
	public void execute(Player player, String[] strings) {
		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);
		boolean scoreboardVisible = !rp.isScoreboardVisible();

		rp.setScoreboardVisible(scoreboardVisible);
		plugin.getRaidScoreboard().forceUpdate(player);
		player.sendMessage(CC.PRIMARY + "Scoreboard is now " + CC.SECONDARY + (scoreboardVisible ? "visible" : "hidden") + CC.PRIMARY + ".");
	}
}
