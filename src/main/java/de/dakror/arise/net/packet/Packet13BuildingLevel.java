package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet13BuildingLevel extends Packet
{
	int buildingId, newLevel;
	
	public Packet13BuildingLevel(int buildingId, int newLevel)
	{
		super(13);
		this.buildingId = buildingId;
		this.newLevel = newLevel;
	}
	
	public Packet13BuildingLevel(byte[] data)
	{
		super(13);
		String[] parts = readData(data).split(":");
		buildingId = Integer.parseInt(parts[0]);
		newLevel = Integer.parseInt(parts[1]);
	}
	
	public int getBuildingId()
	{
		return buildingId;
	}
	
	public int getNewLevel()
	{
		return newLevel;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (buildingId + ":" + newLevel).getBytes();
	}
}
