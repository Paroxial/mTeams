package com.pvpraids.raid.util;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.EntityType;

@UtilityClass
public class StringUtil {
	public String formatEntityType(EntityType type) {
		if (type == EntityType.MUSHROOM_COW) {
			return "Mooshroom";
		}

		StringBuilder name = new StringBuilder();

		if (type.name().contains("_")) {
			for (String part : type.name().split("_")) {
				name.append(part.substring(0, 1).toUpperCase()).append(type.name().substring(1).toLowerCase());
			}
		} else {
			name.append(type.name().substring(0, 1).toUpperCase()).append(type.name().substring(1).toLowerCase());
		}

		return name.toString();
	}
}
