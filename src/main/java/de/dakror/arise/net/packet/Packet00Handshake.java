package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet00Handshake extends Packet
{
	public Packet00Handshake()
	{
		super(0);
	}
	
	public Packet00Handshake(byte[] data)
	{
		super(0);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return "hi".getBytes();
	}
}
