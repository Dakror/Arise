package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet14CityLevel extends Packet {
	int cityId, newLevel;
	
	public Packet14CityLevel(int cityId, int newLevel) {
		super(14);
		this.cityId = cityId;
		this.newLevel = newLevel;
	}
	
	public Packet14CityLevel(byte[] data) {
		super(14);
		String[] parts = readData(data).split(":");
		cityId = Integer.parseInt(new String(parts[0]));
		newLevel = Integer.parseInt(new String(parts[1]));
	}
	
	public int getCityId() {
		return cityId;
	}
	
	public int getNewLevel() {
		return newLevel;
	}
	
	@Override
	protected byte[] getPacketData() {
		return (cityId + ":" + newLevel).getBytes();
	}
	
}
