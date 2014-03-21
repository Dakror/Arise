package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet08PlaceBuilding extends Packet
{
	int cityId, type, x, y;
	
	public Packet08PlaceBuilding(int cityId, int type, int x, int y)
	{
		super(8);
		this.cityId = cityId;
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	public Packet08PlaceBuilding(byte[] data)
	{
		super(8);
		String[] parts = readData(data).split(":");
		
		cityId = Integer.parseInt(parts[0]);
		type = Integer.parseInt(parts[1]);
		x = Integer.parseInt(parts[2]);
		y = Integer.parseInt(parts[3]);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (cityId + ":" + type + ":" + x + ":" + y).getBytes();
	}
	
	public int getCityId()
	{
		return cityId;
	}
	
	public int getBuildingType()
	{
		return type;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
}
