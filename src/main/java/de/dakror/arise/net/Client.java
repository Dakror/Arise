package de.dakror.arise.net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @author Dakror
 */
public class Client extends Thread
{
	boolean connected;
	DatagramSocket socket;
	InetAddress serverIP;
	
	public Client()
	{
		try
		{
			socket = new DatagramSocket();
			setName("Client-Thread");
			setPriority(MAX_PRIORITY);
			connected = false;
			start();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{}
}
