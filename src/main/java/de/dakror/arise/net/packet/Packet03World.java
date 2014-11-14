package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet03World extends Packet {
	int id;
	String name;
	int speed;
	
	public Packet03World(int id) {
		super(3);
		this.id = id;
	}
	
	public Packet03World(int id, String name, int speed) {
		super(3);
		this.id = id;
		this.name = name;
		this.speed = speed;
	}
	
	public Packet03World(byte[] data) {
		super(3);
		String s = readData(data);
		if (s.contains(":")) {
			String[] parts = s.trim().split(":");
			
			id = Integer.parseInt(new String(parts[0]));
			name = new String(parts[1]);
			speed = Integer.parseInt(new String(parts[2]));
		}
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	@Override
	protected byte[] getPacketData() {
		return (id + ":" + name + ":" + speed).getBytes();
	}
	
}
