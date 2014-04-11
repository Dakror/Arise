package de.dakror.arise.net.packet;


/**
 * @author Dakror
 */
public class Packet16BuildingMeta extends Packet
{
	int buildingId;
	String meta;
	
	public Packet16BuildingMeta(int buildingId, String meta)
	{
		super(16);
		this.buildingId = buildingId;
		this.meta = meta;
	}
	
	public Packet16BuildingMeta(byte[] data)
	{
		super(16);
		String[] parts = readData(data).split("\\[#\\]");
		buildingId = Integer.parseInt(new String(parts[0]));
		if (parts.length > 1) meta = new String(parts[1]);
		else meta = "";
	}
	
	public int getBuildingId()
	{
		return buildingId;
	}
	
	public String getMeta()
	{
		return meta;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (buildingId + "[#]" + meta).getBytes();
	}
}
