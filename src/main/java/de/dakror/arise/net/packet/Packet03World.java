package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet03World extends Packet
{
	int id;
	String name;
	int speed;
	
	public Packet03World(int id)
	{
		super(3);
		this.id = id;
	}
	
	public Packet03World(int id, String name, int speed)
	{
		super(3);
		this.id = id;
		this.name = name;
		this.speed = speed;
	}
	
	public Packet03World(byte[] data)
	{
		super(3);
		String s = readData(data);
		if (s.contains(":"))
		{
			String[] parts = s.trim().split(":");
			
			id = Integer.parseInt(parts[0]);
			name = parts[1];
			speed = Integer.parseInt(parts[2]);
		}
		else id = Integer.parseInt(s.trim());
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getSpeed()
	{
		return speed;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		if (name == null) return (id + "").getBytes();
		return (id + ":" + name + ":" + speed).getBytes();
	}
	
}
