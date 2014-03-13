package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet00Handshake extends Packet
{
	String username;
	
	public Packet00Handshake(String username)
	{
		super(0);
		this.username = username;
	}
	
	public Packet00Handshake()
	{
		this("");
	}
	
	public Packet00Handshake(byte[] data)
	{
		super(0);
		username = readData(data);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return username.getBytes();
	}
}
