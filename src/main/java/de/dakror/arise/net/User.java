package de.dakror.arise.net;

import java.net.InetAddress;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Dakror
 */
public class User
{
	private InetAddress ip;
	private String username;
	private int port;
	private int id;
	private int world;
	private int city;
	private long lastInteraction;
	
	public User(int id, int world, String username, InetAddress ip, int port)
	{
		this.id = id;
		this.ip = ip;
		this.username = username;
		this.world = world;
		this.port = port;
		
		interact();
	}
	
	public User(JSONObject o)
	{
		try
		{
			if (o.has("i")) ip = InetAddress.getByName(o.getString("i"));
			if (o.has("p")) port = o.getInt("p");
			world = o.getInt("w");
			id = o.getInt("u");
			interact();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public InetAddress getIP()
	{
		return ip;
	}
	
	public void setIP(InetAddress ip)
	{
		this.ip = ip;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getWorldId()
	{
		return world;
	}
	
	public void setCity(int city)
	{
		this.city = city;
	}
	
	public int getCity()
	{
		return city;
	}
	
	public void interact()
	{
		lastInteraction = System.currentTimeMillis();
	}
	
	public long getLastInteraction()
	{
		return lastInteraction;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String serialize()
	{
		JSONObject o = new JSONObject();
		
		try
		{
			o.put("u", id);
			o.put("i", ip.getHostAddress());
			o.put("p", port);
			o.put("w", world);
			o.put("c", city);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return o.toString();
	}
	
	@Override
	public String toString()
	{
		return serialize().toString();
	}
}
