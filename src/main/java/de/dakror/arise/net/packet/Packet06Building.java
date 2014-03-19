package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet06Building extends Packet
{
	int cityId, id, type, level, x, y, stage, timeleft;
	String meta;
	
	public Packet06Building(int cityId)
	{
		super(6);
		this.cityId = cityId;
	}
	
	public Packet06Building(int cityId, int id, int type, int level, int x, int y, int stage, int timeleft, String meta)
	{
		super(6);
		
		this.cityId = cityId;
		this.id = id;
		this.type = type;
		this.level = level;
		this.x = x;
		this.y = y;
		this.stage = stage;
		this.timeleft = timeleft;
		this.meta = meta;
	}
	
	public Packet06Building(byte[] data)
	{
		super(6);
		String s = readData(data);
		if (s.contains(":"))
		{
			String[] parts = s.split(":");
			cityId = Integer.parseInt(parts[0]);
			id = Integer.parseInt(parts[1]);
			type = Integer.parseInt(parts[2]);
			level = Integer.parseInt(parts[3]);
			x = Integer.parseInt(parts[4]);
			y = Integer.parseInt(parts[5]);
			stage = Integer.parseInt(parts[6]);
			timeleft = Integer.parseInt(parts[7]);
			if (parts.length == 9) meta = parts[8];
		}
		else cityId = Integer.parseInt(s.trim());
	}
	
	@Override
	protected byte[] getPacketData()
	{
		if (type != 0) return (cityId + ":" + id + ":" + type + ":" + level + ":" + x + ":" + y + ":" + stage + ":" + timeleft + (meta != null ? ":" + meta : "")).getBytes();
		else return (cityId + "").getBytes();
	}
	
	public int getCityId()
	{
		return cityId;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getBuildingType()
	{
		return type;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getStage()
	{
		return stage;
	}
	
	public int getTimeleft()
	{
		return timeleft;
	}
	
	public String getMeta()
	{
		return meta;
	}
}
