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
	private int port;
	private int id;
	private int world;
	
	public User(int id, int world, InetAddress ip, int port)
	{
		this.id = id;
		this.ip = ip;
		this.world = world;
		this.port = port;
	}
	
	public User(JSONObject o)
	{
		try
		{
			if (o.has("i")) ip = InetAddress.getByName(o.getString("i"));
			if (o.has("p")) port = o.getInt("p");
			world = o.getInt("w");
			id = o.getInt("u");
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
	
	public String serialize()
	{
		JSONObject o = new JSONObject();
		
		try
		{
			o.put("u", id);
			o.put("i", ip.getHostAddress());
			o.put("p", port);
			o.put("w", world);
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
