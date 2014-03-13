package de.dakror.arisewebsite.net;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Dakror
 */
public class User
{
	private InetAddress ip;
	private int port;
	private String username;
	
	public int K, D;
	
	public User(String username, InetAddress ip, int port)
	{
		this.ip = ip;
		this.port = port;
		this.username = username;
		
		K = D = 0;
	}
	
	public User(JSONObject o)
	{
		try
		{
			if (o.has("i")) ip = InetAddress.getByName(o.getString("i"));
			if (o.has("p")) port = o.getInt("p");
			username = o.getString("u");
			K = o.getInt("K");
			D = o.getInt("D");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public User(String username, int K, int D)
	{
		this.username = username;
		this.K = K;
		this.D = D;
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
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public String serialize()
	{
		JSONObject o = new JSONObject();
		
		try
		{
			o.put("u", username);
			o.put("i", ip.getHostAddress());
			o.put("p", port);
			o.put("K", K);
			o.put("D", D);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		
		return o.toString();
	}
	
	public byte[] getBytes()
	{
		ByteBuffer bb = ByteBuffer.allocate(username.length() + 5);
		bb.putShort((short) K);
		bb.putShort((short) D);
		bb.put((byte) username.length());
		bb.put(username.getBytes());
		
		return bb.array();
	}
	
	@Override
	public String toString()
	{
		return serialize().toString();
	}
}
