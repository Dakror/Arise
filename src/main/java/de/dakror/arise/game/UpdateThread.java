package de.dakror.arise.game;

import java.io.IOException;

import de.dakror.arise.net.packet.Packet00Handshake;
import de.dakror.gamesetup.Updater;

/**
 * @author Dakror
 */
public class UpdateThread extends Updater {
	long lastPing = 0;
	
	@Override
	public void update() {
		if (tick % 60 == 0) {
			Game.currentGame.usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			try {
				Game.client.sendPacket(new Packet00Handshake());
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.gc();
		}
	}
}
