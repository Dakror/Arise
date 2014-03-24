package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet09BuildingStageChange extends Packet
{
	int buildingId, cityId, newStage;
	
	public Packet09BuildingStageChange(int buildingId, int cityId, int newStage)
	{
		super(9);
		this.buildingId = buildingId;
		this.cityId = cityId;
		this.newStage = newStage;
	}
	
	public Packet09BuildingStageChange(byte[] data)
	{
		super(9);
		String[] parts = readData(data).split(":");
		buildingId = Integer.parseInt(parts[0]);
		cityId = Integer.parseInt(parts[1]);
		newStage = Integer.parseInt(parts[2]);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (buildingId + ":" + cityId + ":" + newStage).getBytes();
	}
	
	public int getBuildingId()
	{
		return buildingId;
	}
	
	public int getCityId()
	{
		return cityId;
	}
	
	public int getNewStage()
	{
		return newStage;
	}
}
