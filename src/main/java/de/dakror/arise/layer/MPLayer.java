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
			if (((Packet02Disconnect) p).getCause() == Cause.INACTIVE || ((Packet02Disconnect) p).getCause() == Cause.KICK)
			{
				Game.world = null;
				Game.currentGame.setLayer(new LoginLayer());
				Game.currentGame.addLayer(new Alert(((Packet02Disconnect) p).getCause().getDescription(), null));
				Game.userID = 0;
			}
			if (((Packet02Disconnect) p).getCause() == Cause.SERVER_CLOSED)
			{
				Game.world = null;
				Game.currentGame.setLayer(new LoginLayer());
				Game.currentGame.addLayer(new Alert(Cause.SERVER_CLOSED.getDescription(), null));
				Game.userID = 0;
			}
		}
	}
}
