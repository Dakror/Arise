package de.dakror.arise.net.packet;

import java.nio.ByteBuffer;

import de.dakror.arise.settings.Resources;

/**
 * @author Dakror
 */
public class Packet05Resources extends Packet
{
	Resources resources;
	int cityId;
	
	public Packet05Resources(int cityId)
	{
		super(5);
		this.cityId = cityId;
	}
	
	public Packet05Resources(int cityId, Resources res)
	{
		super(5);
		resources = res;
	}
	
	public Packet05Resources(byte[] data)
	{
		super(5);
		
		if (data.length > 5)
		{
			ByteBuffer bb = ByteBuffer.wrap(data);
			bb.get(); // skip packet id
			
			cityId = bb.getInt();
			byte[] d = new byte[bb.capacity() - 5];
			bb.get(d, 0, d.length);
			resources = new Resources(d);
		}
		else cityId = Integer.parseInt(readData(data).trim());
	}
	
	public Resources getResources()
	{
		return resources;
	}
	
	public int getCityId()
	{
		return cityId;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		if (resources == null)
		{
			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.putInt(cityId);
			return bb.array();
		}
		
		byte[] b = resources.getBinaryData();
		ByteBuffer bb = ByteBuffer.allocate(b.length + 4);
		bb.putInt(cityId);
		bb.put(b);
		
		return bb.array();
	}
}
