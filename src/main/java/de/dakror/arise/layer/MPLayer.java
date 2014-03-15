package de.dakror.arise.layer;

import de.dakror.arise.game.Game;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet02Disconnect;
import de.dakror.arise.net.packet.Packet02Disconnect.Cause;
import de.dakror.gamesetup.layer.Layer;

public abstract class MPLayer extends Layer
{
	public void onReceivePacket(Packet p)
	{
		if (p.getType() == PacketTypes.DISCONNECT && ((Packet02Disconnect) p).getCause() == Cause.SERVER_CONFIRMED) Game.exit();
	}
}
