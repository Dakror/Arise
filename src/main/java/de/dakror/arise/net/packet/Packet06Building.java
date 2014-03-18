package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet06Building extends Packet
{
	String rawData;
	int cityId;
	
	public Packet06Building(int cityId)
	{
		super(6);
		this.cityId = cityId;
	}
	
	public Packet06Building(String s, int cityId)
	{
		super(6);
		rawData = s;
		this.cityId = cityId;
	}
	
	public Packet06Building(byte[] data)
	{
		super(6);
		String s = readData(data);
		if (s.contains(":"))
		{
			cityId = Integer.parseInt(s.substring(0, s.indexOf(":")));
			rawData = s.substring(s.indexOf(":") + 1);
		}
		else cityId = Integer.parseInt(s);
	}
	
	public String getRawData()
	{
		return rawData;
	}
	
	public int getCityId()
	{
		return cityId;
	}
	
	public int getTypeId()
	{
		String[] p = rawData.split(":");
		return Integer.parseInt(p[0]);
	}
	
	public int getLevel()
	{
		String[] p = rawData.split(":");
		return Integer.parseInt(p[1]);
	}
	
	public int getX()
	{
		String[] p = rawData.split(":");
		return Integer.parseInt(p[2]);
	}
	
	public int getY()
	{
		String[] p = rawData.split(":");
		return Integer.parseInt(p[3]);
	}
	
	public int getStage()
	{
		String[] p = rawData.split(":");
		return Integer.parseInt(p[4]);
	}
	
	public int getSecondsLeft()
	{
		String[] p = rawData.split(":");
		return Integer.parseInt(p[5]);
	}
	
	public String getMetadata()
	{
		String[] p = rawData.split(":");
		if (p.length == 7) return p[6];
		return null;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		if (rawData == null) return (cityId + "").getBytes();
		return (cityId + ":" + rawData).getBytes();
	}
}
