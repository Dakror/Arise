package de.dakror.arise.net.packet;

import java.util.Arrays;

/**
 * @author Dakror
 */
public abstract class Packet
{
	public static enum PacketTypes
	{
		INVALID,
		HANDSHAKE,
		LOGIN,
		
		;
		public int getID()
		{
			return ordinal() - 1;
		}
	}
	
	public byte packetID;
	
	public Packet(int packetID)
	{
		this.packetID = (byte) packetID;
	}
	
	protected abstract byte[] getPacketData();
	
	public byte[] getData()
	{
		byte[] strData = getPacketData();
		
		byte[] data = new byte[strData.length + 1];
		data[0] = packetID;
		
		System.arraycopy(strData, 0, data, 1, strData.length);
		
		return data;
	}
	
	public static String readData(byte[] data)
	{
		return new String(Arrays.copyOfRange(data, 1, data.length)).trim();
	}
	
	public PacketTypes getType()
	{
		return Packet.lookupPacket(packetID);
	}
	
	public static PacketTypes lookupPacket(int id)
	{
		for (PacketTypes pt : PacketTypes.values())
		{
			if (pt.getID() == id) return pt;
		}
		
		return PacketTypes.INVALID;
	}
}
