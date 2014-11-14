package de.dakror.arise.net.packet;

import de.dakror.arise.settings.TroopType;

/**
 * @author Dakror
 */
public class Packet15BarracksBuildTroop extends Packet {
	int cityId, buildingId, amount;
	TroopType type;
	
	public Packet15BarracksBuildTroop(int cityId, int buildingId, TroopType type, int amount) {
		super(15);
		this.cityId = cityId;
		this.buildingId = buildingId;
		this.type = type;
		this.amount = amount;
	}
	
	public Packet15BarracksBuildTroop(byte[] data) {
		super(15);
		
		String[] parts = readData(data).split(":");
		cityId = Integer.parseInt(new String(parts[0]));
		buildingId = Integer.parseInt(new String(parts[1]));
		type = TroopType.values()[Integer.parseInt(new String(parts[2]))];
		amount = Integer.parseInt(new String(parts[3]));
	}
	
	@Override
	protected byte[] getPacketData() {
		return (cityId + ":" + buildingId + ":" + type.ordinal() + ":" + amount).getBytes();
	}
	
	public int getCityId() {
		return cityId;
	}
	
	public int getBuildingId() {
		return buildingId;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public TroopType getTroopType() {
		return type;
	}
}
