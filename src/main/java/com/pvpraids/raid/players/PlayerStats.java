package com.pvpraids.raid.players;

import com.pvpraids.core.utils.obj.MongoSerializable;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

@Getter
@Setter
public class PlayerStats implements MongoSerializable {
	private int mooshroomsCapped;
	private int diamondsMined;
	private int obsidianMined;

	@Override
	public Document serialize() {
		return new Document()
				.append("mooshrooms_capped", mooshroomsCapped)
				.append("diamonds_mined", diamondsMined)
				.append("obsidian_mined", obsidianMined);
	}

	@Override
	public void deserialize(Document document) {
		this.mooshroomsCapped = document.getInteger("mooshrooms_capped");
		this.diamondsMined = document.getInteger("diamonds_mined");
		this.obsidianMined = document.getInteger("obsidian_mined");
	}
}
