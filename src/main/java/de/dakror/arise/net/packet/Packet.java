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
		DISCONNECT,
		WORLD,
		CITY,
		RESOURCES,
		BUILDING
		
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
	
	public static Packet newInstance(PacketTypes type, byte[] data)
	{
		if (type == PacketTypes.INVALID) return null;
		
		String idS = (type.ordinal() - 1) + "";
		idS = idS.length() == 1 ? "0" + idS : idS;
		String pt = type.name().toLowerCase();
		String pName = "Packet" + idS + pt.substring(0, 1).toUpperCase() + pt.substring(1);
		try
		{
			return (Packet) Class.forName("de.dakror.arise.net.packet." + pName).getConstructor(byte[].class).newInstance(data);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
