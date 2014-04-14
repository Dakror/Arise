package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet04City extends Packet
{
	int x, y, userId, cityId, level;
	String cityName, username;
	
	public Packet04City(int cityId, int x, int y, int userId, int level, String cityName, String username)
	{
		super(4);
		this.cityId = cityId;
		this.x = x;
		this.y = y;
		this.userId = userId;
		this.level = level;
		this.cityName = cityName;
		this.username = username;
	}
	
	public Packet04City(byte[] data)
	{
		super(4);
		String s = readData(data);
		String[] parts = s.split(":");
		cityId = Integer.parseInt(new String(parts[0]));
		x = Integer.parseInt(new String(parts[1]));
		y = Integer.parseInt(new String(parts[2]));
		userId = Integer.parseInt(new String(parts[3]));
		level = Integer.parseInt(new String(parts[4]));
		cityName = new String(parts[5]);
		username = new String(parts[6]);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (cityId + ":" + x + ":" + y + ":" + userId + ":" + level + ":" + cityName + ":" + username).getBytes();
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getUserId()
	{
		return userId;
	}
	
	public int getCityId()
	{
		return cityId;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public String getCityName()
	{
		return cityName;
	}
	
	public String getUsername()
	{
		return username;
	}
}
