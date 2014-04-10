package de.dakror.arise.net.packet;

import java.nio.ByteBuffer;

import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.TransferType;

/**
 * @author Dakror
 */
public class Packet19Transfer extends Packet
{
	int cityFrom, cityTo, timeleft;
	TransferType type;
	Resources value;
	
	public Packet19Transfer(int cityFrom, int cityTo, TransferType type, Resources value, int timeleft)
	{
		super(19);
		this.cityFrom = cityFrom;
		this.cityTo = cityTo;
		this.type = type;
		this.value = value;
		this.timeleft = timeleft;
	}
	
	public Packet19Transfer(byte[] data)
	{
		super(19);
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.get(); // skip id
		
		cityFrom = bb.getInt();
		cityTo = bb.getInt();
		type = TransferType.values()[bb.getInt()];
		timeleft = bb.getInt();
		cityFrom = bb.getInt();
		byte[] val = new byte[bb.getInt()];
		bb.get(val);
		value = new Resources(val);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		byte[] val = value.getBinaryData();
		
		ByteBuffer bb = ByteBuffer.allocate(val.length + 20);
		bb.putInt(cityFrom);
		bb.putInt(cityTo);
		bb.putInt(type.ordinal());
		bb.putInt(timeleft);
		bb.putInt(val.length);
		bb.put(val);
		
		return bb.array();
	}
	
	public int getCityFrom()
	{
		return cityFrom;
	}
	
	public int getCityTo()
	{
		return cityTo;
	}
	
	public int getTimeleft()
	{
		return timeleft;
	}
	
	public TransferType getTransferType()
	{
		return type;
	}
	
	public Resources getValue()
	{
		return value;
	}
	
}
