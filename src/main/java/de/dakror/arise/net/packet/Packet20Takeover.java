package de.dakror.arise.net.packet;


/**
 * @author Dakror
 */
public class Packet20Takeover extends Packet
{
	int cityTakenOverId, stage, timeleft;
	
	public Packet20Takeover(int cityTakenOverId, int stage, int timeleft)
	{
		super(20);
		this.cityTakenOverId = cityTakenOverId;
		this.stage = stage;
		this.timeleft = timeleft;
	}
	
	public Packet20Takeover(byte[] data)
	{
		super(20);
		String[] parts = readData(data).split(":");
		
		cityTakenOverId = Integer.parseInt(parts[0]);
		stage = Integer.parseInt(parts[1]);
		timeleft = Integer.parseInt(parts[2]);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (cityTakenOverId + ":" + stage + ":" + timeleft).getBytes();
	}
	
	public int getCityTakenOverId()
	{
		return cityTakenOverId;
	}
	
	public int getStage()
	{
		return stage;
	}
	
	public int getTimeleft()
	{
		return timeleft;
	}
}
