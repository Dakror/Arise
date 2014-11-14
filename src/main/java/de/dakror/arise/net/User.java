package de.dakror.arise.net;

import java.net.InetAddress;


/**
 * @author Dakror
 */
public class User {
	private InetAddress ip;
	private String username;
	private int port;
	private int id;
	private int world;
	private int city;
	private long lastInteraction;
	
	public User(int id, int world, String username, InetAddress ip, int port) {
		this.id = id;
		this.ip = ip;
		this.username = username;
		this.world = world;
		this.port = port;
		
		interact();
	}
	
	public InetAddress getIP() {
		return ip;
	}
	
	public void setIP(InetAddress ip) {
		this.ip = ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getId() {
		return id;
	}
	
	public int getWorldId() {
		return world;
	}
	
	public void setCity(int city) {
		this.city = city;
	}
	
	public int getCity() {
		return city;
	}
	
	public void interact() {
		lastInteraction = System.currentTimeMillis();
	}
	
	public long getLastInteraction() {
		return lastInteraction;
	}
	
	public String getUsername() {
		return username;
	}
	
	@Override
	public String toString() {
		return username.trim() + ", " + ip.getHostAddress() + ":" + port + ", UserID=" + id;
	}
}
