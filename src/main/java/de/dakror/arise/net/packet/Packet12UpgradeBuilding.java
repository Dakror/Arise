package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet12UpgradeBuilding extends Packet
{
	int buildingId, cityId;
	
	public Packet12UpgradeBuilding(int buildingId, int cityId)
	{
		super(12);
		this.buildingId = buildingId;
		this.cityId = cityId;
	}
	
	public Packet12UpgradeBuilding(byte[] data)
	{
		super(12);
		String[] parts = readData(data).split(":");
		buildingId = Integer.parseInt(new String(parts[0]));
		cityId = Integer.parseInt(new String(parts[1]));
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
