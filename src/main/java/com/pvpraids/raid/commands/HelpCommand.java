package com.pvpraids.raid.commands;

import com.pvpraids.core.commands.BaseCommand;
import org.bukkit.command.CommandSender;

public class HelpCommand extends BaseCommand {
	public HelpCommand() {
		super("help");
	}

	@Override
	protected void execute(CommandSender sender, String[] args) {
		sender.sendMessage(new String[]{
				" §e§m------§r §7PvPRaids Help §e§m------",
				" §eMap Duration: §7February 9 - February 28",
				" §7Border is at §e10,000 §7blocks from (§e0§7,§e0§7)",
				" §e/alarm §7- opens the alarm menu (COMING SOON)",
				" §e/home §7- warps you to your 'home'",
				" §e/sethome §7- sets your 'home' position",
				" §e/spawn §7- warps you to spawn",
				" §e/team §7- team related commands",
				" §e/track §7- used to track other players",
				" §e/warp §7- used to go to, set, and list warps",
				" §e/who §7- tells you who is currently online",
				" §e/count §7- shows amount of each item in your inventory"
		});
	}
}
