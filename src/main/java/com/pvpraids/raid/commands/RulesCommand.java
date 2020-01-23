package com.pvpraids.raid.commands;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.utils.message.CC;
import org.bukkit.entity.Player;

public class RulesCommand extends PlayerCommand {
	public RulesCommand() {
		super("Rules");
	}

	@Override
	public void execute(Player player, String[] strings) {
		player.sendMessage(CC.GRAY + CC.S + "----------" + CC.YELLOW + " Rules " + CC.GRAY + CC.S + "----------");
		player.sendMessage(CC.YELLOW + "1. " + CC.GRAY + "No cheating of any kind is allowed including unfair" +
				"modifications. Some modifications, however, are allowed and the list can be found on our Discord server.");
		player.sendMessage(CC.YELLOW + "2. " + CC.GRAY + "No racsim/sexism will be tolerated on PvPRaids. We're fairly" +
				"level-headed about things, so don't push us to the point where we take action.");
		player.sendMessage(CC.YELLOW + "3. " + CC.GRAY + "Don't bother our developers; message staff members for assistance.");
		player.sendMessage(CC.YELLOW + "4. " + CC.GRAY + "Use common sense. If you don't think something is allowed, then it " +
				"probably isn't. Don't look for loopholes in the rules.");
		player.sendMessage(CC.YELLOW + "5. " + CC.GRAY + "Show respect to our staff team and players and we will show " +
				"respect back to you. Staff complaints can be sent directly through e-mail to StudiesOfficialBusiness@gmail.com " +
				"with evidence only.");
		player.sendMessage(CC.YELLOW + "6. " + CC.GRAY + "No personal attacks on others, including doxing, DDoSing, hate speech, etc., " +
				"will be tolerated whatsoever and you will be blacklisted from our network.");
	}
}
