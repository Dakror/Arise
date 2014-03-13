package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet01Login extends Packet
{
	String username;
	String pwdMd5;
	boolean loggedIn;
	
	public Packet01Login(String username, String pwdMd5)
	{
		super(1);
		this.username = username;
		this.pwdMd5 = pwdMd5;
	}
	
	public Packet01Login(String username, boolean loggedIn)
	{
		super(1);
		this.username = username;
		this.loggedIn = loggedIn;
	}
	
	public Packet01Login(byte[] data)
	{
		super(1);
		String[] parts = readData(data).split(":");
		username = parts[0];
		if (parts[1].equals("null")) loggedIn = Boolean.parseBoolean(parts[2]);
		else pwdMd5 = parts[1];
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (username + ":" + (pwdMd5 != null ? pwdMd5 : "null") + (pwdMd5 == null ? ":" + Boolean.toString(loggedIn) : "")).getBytes();
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPwdMd5()
	{
		return pwdMd5;
	}
	
	public boolean isLoggedIn()
	{
		return loggedIn;
	}
}
