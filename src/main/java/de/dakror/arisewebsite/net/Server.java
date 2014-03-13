package de.dakror.arisewebsite.net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Dakror
 */
public class Server extends Thread
{
	public static final int PORT = 14744;
	public static final int PACKETSIZE = 255; // bytes
	
	public CopyOnWriteArrayList<User> clients = new CopyOnWriteArrayList<>();
	DatagramSocket socket;
	
	public Server(InetAddress ip)
	{
		try
		{
			socket = new DatagramSocket(new InetSocketAddress(ip, Server.PORT));
		}
		catch (Exception e)
		{}
	}
	
	@Override
	public void run()
	{}
}
