package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet10Attribute extends Packet
{
	String key;
	String value;
	
	public Packet10Attribute(String key, Object value)
	{
		super(10);
		this.key = key;
		this.value = value.toString();
	}
	
	public Packet10Attribute(byte[] data)
	{
		super(10);
		String[] parts = readData(data).split(":");
		key = new String(parts[0]);
		value = new String(parts[1]);
	}
	
	public String getKey()
	{
		return key;
	}
	
	public String getValue()
	{
		return value;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (key + ":" + value).getBytes();
	}
}
