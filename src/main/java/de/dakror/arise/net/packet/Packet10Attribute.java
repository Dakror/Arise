package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet10Attribute extends Packet
{
	String key;
	boolean value;
	
	public Packet10Attribute(String key, boolean value)
	{
		super(10);
		this.key = key;
		this.value = value;
	}
	
	
	public Packet10Attribute(byte[] data)
	{
		super(10);
		String[] parts = readData(data).split(":");
		key = parts[0];
		value = Boolean.parseBoolean(parts[1]);
	}
	
	public String getKey()
	{
		return key;
	}
	
	public boolean getValue()
	{
		return value;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (key + ":" + Boolean.toString(value)).getBytes();
	}
}
