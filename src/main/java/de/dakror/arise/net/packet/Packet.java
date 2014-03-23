package de.dakror.arise.net.packet;

import java.util.Arrays;

/**
 * @author Dakror
 */
public abstract class Packet
{
	public static enum PacketTypes
	{
		INVALID(null),
		HANDSHAKE(Packet00Handshake.class),
		LOGIN(Packet01Login.class),
		DISCONNECT(Packet02Disconnect.class),
		WORLD(Packet03World.class),
		CITY(Packet04City.class),
		RESOURCES(Packet05Resources.class),
		BUILDING(Packet06Building.class),
		RENAMECITY(Packet07RenameCity.class),
		PLACEBUILDING(Packet08PlaceBuilding.class),
		BUILDINGSTAGECHANGE(Packet09BuildingStageChange.class),
		
		;
		
		private Class<?> class1;
		
		private PacketTypes(Class<?> class1)
		{
			this.class1 = class1;
		}
		
		public int getID()
		{
			return ordinal() - 1;
		}
		
		public Class<?> getPacketClass()
		{
			return class1;
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
		
		try
		{
			return (Packet) type.getPacketClass().getConstructor(byte[].class).newInstance(data);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
