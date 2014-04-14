package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet10Attribute extends Packet
{
	public static enum Key
	{
		city,
		world_data,
		loading_complete,
		
		;
	}
	
	Key key;
	String value;
	
	public Packet10Attribute(Key key, Object value)
	{
		super(10);
		this.key = key;
		this.value = value.toString();
	}
	
	public Packet10Attribute(Key key)
	{
		this(key, true);
	}
	
	public Packet10Attribute(byte[] data)
	{
		super(10);
		String[] parts = readData(data).split(":");
		key = Key.values()[Integer.parseInt(parts[0])];
		value = new String(parts[1]);
	}
	
	public Key getKey()
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
		return (key.ordinal() + ":" + value).getBytes();
	}
}
