package com.pvpraids.raid.util;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

/**
 * Needed for XP bottling because Spigot methods
 * surrounding XP are broken and pedantic.
 */
@UtilityClass
public class XPUtil {
	public int getXP(Player player) {
		int experience;
		int level = player.getLevel();
		if (level >= 0 && level <= 15) {
			experience = (int) Math.ceil(Math.pow(level, 2) + (6 * level));
			int requiredExperience = 2 * level + 7;
			double currentExp = Double.parseDouble(Float.toString(player.getExp()));
			experience += Math.ceil(currentExp * requiredExperience);
			return experience;
		} else if (level > 15 && level <= 30) {
			experience = (int) Math.ceil((2.5 * Math.pow(level, 2) - (40.5 * level) + 360));
			int requiredExperience = 5 * level - 38;
			double currentExp = Double.parseDouble(Float.toString(player.getExp()));
			experience += Math.ceil(currentExp * requiredExperience);
			return experience;
		} else {
			experience = (int) Math.ceil(((4.5 * Math.pow(level, 2) - (162.5 * level) + 2220)));
			int requiredExperience = 9 * level - 158;
			double currentExp = Double.parseDouble(Float.toString(player.getExp()));
			experience += Math.ceil(currentExp * requiredExperience);
			return experience;
		}
	}

	public void setXP(Player player, int xp) {
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0.0F);
		player.giveExp(xp);
	}

	public void addXP(Player player, int xp) {
		setXP(player, getXP(player) + xp);
	}
}
