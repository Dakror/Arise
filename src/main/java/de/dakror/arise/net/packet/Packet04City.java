package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet04City extends Packet
{
	int x, y, userId, cityId, level, cities, worldId;
	String cityName, username;
	
	public Packet04City(int worldId)
	{
		super(4);
		this.worldId = worldId;
	}
	
	public Packet04City(int cities, int cityId, int x, int y, int userId, int level, String cityName, String username)
	{
		super(4);
		this.cities = cities;
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
		if (s.contains(":"))
		{
			String[] parts = s.split(":");
			cities = Integer.parseInt(parts[0].trim());
			cityId = Integer.parseInt(parts[1].trim());
			x = Integer.parseInt(parts[2].trim());
			y = Integer.parseInt(parts[3].trim());
			userId = Integer.parseInt(parts[4].trim());
			level = Integer.parseInt(parts[5].trim());
			cityName = parts[6];
			username = parts[7];
		}
		else worldId = Integer.parseInt(s.trim());
	}
	
	@Override
	protected byte[] getPacketData()
	{
		if (cityName != null) return (cities + ":" + cityId + ":" + x + ":" + y + ":" + userId + ":" + level + ":" + cityName + ":" + username).getBytes();
		else return ("" + worldId).getBytes();
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
	
	public int getCities()
	{
		return cities;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public int getWorldId()
	{
		return worldId;
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
