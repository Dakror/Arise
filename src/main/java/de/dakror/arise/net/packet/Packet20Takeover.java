package de.dakror.arise.net.packet;


/**
 * @author Dakror
 */
public class Packet20Takeover extends Packet
{
	int cityTakenOverId, stage, timeleft, newUserId;
	String newUsername;
	
	public Packet20Takeover(int cityTakenOverId, int stage, int timeleft, int newUserId, String newUsername)
	{
		super(20);
		this.cityTakenOverId = cityTakenOverId;
		this.stage = stage;
		this.timeleft = timeleft;
		this.newUserId = newUserId;
		this.newUsername = newUsername;
	}
	
	public Packet20Takeover(byte[] data)
	{
		super(20);
		String[] parts = readData(data).split(":");
		
		cityTakenOverId = Integer.parseInt(parts[0]);
		stage = Integer.parseInt(parts[1]);
		timeleft = Integer.parseInt(parts[2]);
		newUserId = Integer.parseInt(parts[3]);
		newUsername = parts[4];
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (cityTakenOverId + ":" + stage + ":" + timeleft + ":" + newUserId + ":" + newUsername).getBytes();
	}
	
	public int getCityTakenOverId()
	{
		return cityTakenOverId;
	}
	
	public int getStage()
	{
		return stage;
	}
	
	public boolean isCityTakenOver()
	{
		return stage == -1;
	}
	
	public int getTimeleft()
	{
		return timeleft;
	}
	
	public int getNewUserId()
	{
		return newUserId;
	}
	
	public String getNewUsername()
	{
		return newUsername;
	}
}
