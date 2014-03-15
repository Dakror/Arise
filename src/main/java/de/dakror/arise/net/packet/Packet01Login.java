package de.dakror.arise.net.packet;


/**
 * @author Dakror
 */
public class Packet01Login extends Packet
{
	String username;
	String pwdMd5;
	boolean loggedIn;
	int userId;
	
	public Packet01Login(String username, String pwdMd5)
	{
		super(1);
		this.username = username;
		this.pwdMd5 = pwdMd5;
	}
	
	public Packet01Login(String username, int userId, boolean loggedIn)
	{
		super(1);
		this.username = username;
		this.loggedIn = loggedIn;
		this.userId = userId;
	}
	
	public Packet01Login(byte[] data)
	{
		super(1);
		
		String[] parts = readData(data).split(":");
		username = parts[0];
		if (parts.length == 3)
		{
			userId = Integer.parseInt(parts[1]);
			loggedIn = Boolean.parseBoolean(parts[2]);
		}
		else pwdMd5 = parts[1];
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (username + ":" + (pwdMd5 == null ? userId + "" : pwdMd5) + (pwdMd5 == null ? ":" + Boolean.toString(loggedIn) : "")).getBytes();
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
	
	public int getUserId()
	{
		return userId;
	}
}
