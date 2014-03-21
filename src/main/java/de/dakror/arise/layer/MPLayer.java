package de.dakror.arise.layer;

import de.dakror.arise.game.Game;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet02Disconnect;
import de.dakror.arise.net.packet.Packet02Disconnect.Cause;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.layer.Layer;

public abstract class MPLayer extends Layer
{
	public void onReceivePacket(Packet p)
	{
		if (p.getType() == PacketTypes.DISCONNECT)
		{
			if (((Packet02Disconnect) p).getCause() == Cause.SERVER_CLOSED)
			{
				Game.currentGame.setLayer(new LoginLayer());
				Game.currentGame.addLayer(new Alert("Der Server wurde geschlossen. Wir untersuchen dieses Problem bereits und versuchen, den Server schnellstmöglichst wieder zu starten.", null));
				Game.userID = 0;
			}
		}
	}
}
