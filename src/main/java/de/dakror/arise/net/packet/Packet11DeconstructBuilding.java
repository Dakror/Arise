package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet11DeconstructBuilding extends Packet
{
	int buildingId, cityId;
	
	public Packet11DeconstructBuilding(int buildingId, int cityId)
	{
		super(11);
		this.buildingId = buildingId;
		this.cityId = cityId;
	}
	
	public Packet11DeconstructBuilding(byte[] data)
	{
		super(11);
		String[] parts = readData(data).split(":");
		buildingId = Integer.parseInt(parts[0]);
		cityId = Integer.parseInt(parts[1]);
	}
	
	public int getBuildingId()
	{
		return buildingId;
	}
	
	public int getCityId()
	{
		return cityId;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (buildingId + ":" + cityId).getBytes();
	}
	
}
