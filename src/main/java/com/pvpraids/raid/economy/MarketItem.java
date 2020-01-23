package com.pvpraids.raid.economy;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.economy.material.MarketMaterial;
import com.pvpraids.raid.economy.material.Materials;
import com.pvpraids.raid.players.RaidPlayer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class MarketItem implements Comparable<MarketItem> {
	private long creation;
	private long value;
	private UUID owner;
	private MarketMaterial material;
	@Setter
	private int amount;

	public MarketItem(long value, RaidPlayer player, MarketMaterial material, int amount) {
		this(System.currentTimeMillis(), value, player.getUniqueId(), material, amount);
	}

	public static byte[] write(MarketItem item) throws Exception {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bytes);

		out.writeLong(item.creation);
		out.writeLong(item.value);
		out.writeUTF(item.owner.toString());
		out.writeUTF(item.material == null ? "" : item.material.getId());
		out.writeInt(item.amount);

		out.flush();
		out.close();

		return bytes.toByteArray();
	}

	public static MarketItem read(byte[] binary) throws Exception {
		ByteArrayInputStream bytes = new ByteArrayInputStream(binary);
		DataInputStream in = new DataInputStream(bytes);

		long creation = in.readLong();
		long value = in.readLong();
		UUID owner = UUID.fromString(in.readUTF());
		MarketMaterial material = Materials.get(in.readUTF());
		int amount = in.readInt();

		in.close();

		return new MarketItem(creation, value, owner, material, amount);
	}

	public RaidPlayer getRaidPlayer() {
		return RaidPlugin.getInstance().getPlayerManager().getPlayer(owner);
	}

	public boolean isHigher(MarketItem o) {
		if (value < o.value) {
			return true;
		}

		if (value > o.value) {
			return false;
		}

		return creation < o.creation;
	}

	@Override
	public int compareTo(MarketItem o) {
		return isHigher(o) ? -1 : 1;
	}
}