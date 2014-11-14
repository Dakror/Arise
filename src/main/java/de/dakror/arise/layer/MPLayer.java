package de.dakror.arise.layer;

import de.dakror.arise.Arise;
import de.dakror.arise.game.Game;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet02Disconnect;
import de.dakror.arise.net.packet.Packet02Disconnect.Cause;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.ClickEvent;

public abstract class MPLayer extends Layer {
	public void onReceivePacket(Packet p) {
		if (p.getType() == PacketTypes.DISCONNECT) {
			if (((Packet02Disconnect) p).getCause() == Cause.SERVER_CONFIRMED) {
				if (Arise.wrapper) {
					System.exit(0);
				} else {
					Game.world = null;
					Game.currentGame.setLayer(new LoginLayer());
					Game.userID = 0;
				}
			} else {
				if (Arise.wrapper) {
					Game.currentGame.addLayer(new Alert(((Packet02Disconnect) p).getCause().getDescription(), new ClickEvent() {
						@Override
						public void trigger() {
							System.exit(0);
						}
					}));
				} else {
					Game.world = null;
					Game.currentGame.setLayer(new LoginLayer());
					Game.currentGame.addLayer(new Alert(((Packet02Disconnect) p).getCause().getDescription(), null));
					Game.userID = 0;
				}
			}
		}
		
		if (p.getType() == PacketTypes.BATTLERESULT && Game.userID != 0 && !(Game.currentGame.getActiveLayer() instanceof Alert)) {
			Game.currentGame.addLayer(new Alert(p.toString(), null));
		}
	}
}
