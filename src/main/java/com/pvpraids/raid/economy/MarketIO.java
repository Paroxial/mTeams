package com.pvpraids.raid.economy;

import com.pvpraids.raid.economy.material.MarketMaterial;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;

public class MarketIO {
	public boolean write(MarketMaterial material, Set<MarketItem> market) {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(material.getFile()));
			out.writeUTF(material.getId());

			if (market == null) {
				out.writeInt(0);
			} else {
				out.writeInt(market.size());
				for (MarketItem item : market) {
					byte[] binary = MarketItem.write(item);
					out.writeInt(binary.length);
					for (byte b : binary) {
						out.writeByte(b);
					}
					out.flush();
				}
			}

			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to save " + material.getId() + " Market!");
			Bukkit.getLogger().log(Level.SEVERE, e.toString());
			return false;
		}
	}

	public Set<MarketItem> read(MarketMaterial material) {
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(material.getFile()));
			if (!material.getId().equalsIgnoreCase(in.readUTF())) {
				in.close();
				throw new IllegalStateException("Identifiers do not match!");
			}

			Set<MarketItem> market = new HashSet<>();
			int size = in.readInt();
			for (int i = 0; i != size; i++) {
				byte[] binary = new byte[in.readInt()];
				for (int b = 0; b != binary.length; b++) {
					binary[b] = in.readByte();
				}
				market.add(MarketItem.read(binary));
			}
			in.close();
			return market;
		} catch (EOFException ex) {
			// ignore
			return null;
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to load " + material.getId() + " Market!");
			Bukkit.getLogger().log(Level.SEVERE, e.toString());
			return null;
		}
	}
}