package de.dakror.arise.layer;

import de.dakror.arise.net.packet.Packet;
import de.dakror.gamesetup.layer.Layer;

public abstract class MPLayer extends Layer
{
	public abstract void onReceivePacket(Packet p);
}
