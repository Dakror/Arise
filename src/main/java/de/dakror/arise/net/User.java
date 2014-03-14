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
	
	public User(int id, InetAddress ip, int port)
	{
		this.ip = ip;
		this.port = port;
		this.id = id;
	}
	
	public User(JSONObject o)
	{
		try
		{
			if (o.has("i")) ip = InetAddress.getByName(o.getString("i"));
			if (o.has("p")) port = o.getInt("p");
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
	
	public String serialize()
	{
		JSONObject o = new JSONObject();
		
		try
		{
			o.put("u", id);
			o.put("i", ip.getHostAddress());
			o.put("p", port);
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
