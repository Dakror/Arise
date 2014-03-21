package de.dakror.arise.net.packet;

public class Packet07RenameCity extends Packet
{
	int cityId;
	String newName;
	
	public Packet07RenameCity(int cityId, String newName)
	{
		super(7);
		this.cityId = cityId;
		this.newName = newName;
	}
	
	public Packet07RenameCity(byte[] data)
	{
		super(7);
		String[] parts = readData(data).split(":");
		cityId = Integer.parseInt(parts[0]);
		newName = parts[1];
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (cityId + ":" + newName).getBytes();
	}
	
	public int getCityId()
	{
		return cityId;
	}
	
	public String getNewName()
	{
		return newName;
	}
}
