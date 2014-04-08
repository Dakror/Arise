package de.dakror.arise.net.packet;

import java.nio.ByteBuffer;

import de.dakror.arise.settings.Resources;

/**
 * @author Dakror
 */
public class Packet17CityAttack extends Packet
{
	int attCityId, defCityId;
	Resources attArmy;
	
	public Packet17CityAttack(int attCityId, int defCiyId, Resources attArmy)
	{
		super(17);
		this.attCityId = attCityId;
		defCityId = defCiyId;
		this.attArmy = attArmy;
	}
	
	public Packet17CityAttack(byte[] data)
	{
		super(17);
		
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.get(); // skip id
		int resLength = bb.getInt();
		attCityId = bb.getInt();
		defCityId = bb.getInt();
		byte[] res = new byte[resLength];
		bb.get(res);
		attArmy = new Resources(res);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		byte[] res = attArmy.getBinaryData();
		ByteBuffer bb = ByteBuffer.allocate(12 + res.length);
		bb.putInt(res.length);
		bb.putInt(attCityId);
		bb.putInt(defCityId);
		bb.put(res);
		
		return bb.array();
	}
	
	public int getAttCityId()
	{
		return attCityId;
	}
	
	public int getDefCityId()
	{
		return defCityId;
	}
	
	public Resources getAttArmy()
	{
		return attArmy;
	}
}
