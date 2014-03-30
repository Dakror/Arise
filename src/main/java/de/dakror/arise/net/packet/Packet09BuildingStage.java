package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet09BuildingStage extends Packet
{
	int buildingId, newStage, timeLeft;
	
	public Packet09BuildingStage(int buildingId, int newStage, int timeLeft)
	{
		super(9);
		this.buildingId = buildingId;
		this.newStage = newStage;
		this.timeLeft = timeLeft;
	}
	
	public Packet09BuildingStage(byte[] data)
	{
		super(9);
		String[] parts = readData(data).split(":");
		buildingId = Integer.parseInt(parts[0]);
		newStage = Integer.parseInt(parts[1]);
		timeLeft = Integer.parseInt(parts[2]);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (buildingId + ":" + newStage + ":" + timeLeft).getBytes();
	}
	
	public int getBuildingId()
	{
		return buildingId;
	}
	
	public int getNewStage()
	{
		return newStage;
	}
	
	public int getTimeLeft()
	{
		return timeLeft;
	}
}
