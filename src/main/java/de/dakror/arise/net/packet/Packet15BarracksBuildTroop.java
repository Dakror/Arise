package de.dakror.arise.net.packet;

import de.dakror.arise.settings.TroopType;

/**
 * @author Dakror
 */
public class Packet15BarracksBuildTroop extends Packet
{
	int buildingId, amount;
	TroopType type;
	
	public Packet15BarracksBuildTroop(int buildingId, TroopType type, int amount)
	{
		super(15);
		this.buildingId = buildingId;
		this.type = type;
		this.amount = amount;
	}
	
	public Packet15BarracksBuildTroop(byte[] data)
	{
		super(15);
		
		String[] parts = readData(data).split(":");
		buildingId = Integer.parseInt(parts[0]);
		type = TroopType.values()[Integer.parseInt(parts[1])];
		amount = Integer.parseInt(parts[2]);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (buildingId + ":" + type.ordinal() + ":" + amount).getBytes();
	}
	
	public int getBuildingId()
	{
		return buildingId;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public TroopType getTroopType()
	{
		return type;
	}
}
