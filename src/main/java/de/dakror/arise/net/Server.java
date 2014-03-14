package de.dakror.arise.net;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet00Handshake;
import de.dakror.arise.net.packet.Packet01Login;
import de.dakror.arise.settings.CFG;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class Server extends Thread
{
	public static final int PORT = 14744;
	public static final int PACKETSIZE = 255; // bytes
	
	public boolean running;
	public CopyOnWriteArrayList<User> clients = new CopyOnWriteArrayList<>();
	
	DatagramSocket socket;
	
	public Server(InetAddress ip)
	{
		try
		{
			socket = new DatagramSocket(new InetSocketAddress(ip, Server.PORT));
			setName("Server-Thread");
			setPriority(MAX_PRIORITY);
			CFG.p("Starting server at " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
			start();
		}
		catch (BindException e)
		{
			CFG.e("There is a server already running on this machine!");
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		running = true;
		while (running)
		{
			byte[] data = new byte[PACKETSIZE];
			
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try
			{
				socket.receive(packet);
				parsePacket(data, packet.getAddress(), packet.getPort());
			}
			catch (SocketException e)
			{}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void parsePacket(byte[] data, InetAddress address, int port)
	{
		PacketTypes type = Packet.lookupPacket(data[0]);
		switch (type)
		{
			case INVALID:
			{
				CFG.e("Received invalid packet: " + new String(data));
				break;
			}
			case HANDSHAKE:
			{
				try
				{
					sendPacket(new Packet00Handshake(), new User("", address, port));
					CFG.p("Shook hands with: " + address.getHostAddress() + ":" + port);
					break;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			case LOGIN:
			{
				try
				{
					Packet01Login p = new Packet01Login(data);
					String s = Helper.getURLContent(new URL("http://dakror.de/mp-api/login_noip.php?username=" + p.getUsername() + "&password=" + p.getPwdMd5()));
					boolean loggedIn = s.contains("true");
					User u = new User(p.getUsername(), address, port);
					if (loggedIn)
					{
						CFG.p("User logged in: " + p.getUsername() + " (#" + Integer.parseInt(s.replace("true:", "").trim()) + ")");
						clients.add(u);
					}
					
					sendPacket(new Packet01Login(p.getUsername(), loggedIn ? Integer.parseInt(s.replace("true:", "").trim()) : 0, loggedIn), u);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
			}
			default:
				CFG.e("Reveived unhandled packet (" + address.getHostAddress() + ":" + port + ") " + type + " [" + Packet.readData(data) + "]");
		}
	}
	
	public void sendPacketToAllClients(Packet p) throws Exception
	{
		for (User u : clients)
			sendPacket(p, u);
	}
	
	public void sendPacketToAllClientsExceptOne(Packet p, User exception) throws Exception
	{
		for (User u : clients)
		{
			if (exception.getUsername() == null)
			{
				if (exception.getIP().equals(u.getIP()) && exception.getPort() == u.getPort()) continue;
			}
			else if (exception.getUsername().equals(u.getUsername())) continue;
			sendPacket(p, u);
		}
	}
	
	public void sendPacket(Packet p, User u) throws IOException
	{
		byte[] data = p.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, u.getIP(), u.getPort());
		
		socket.send(packet);
	}
	
	public void shutdown()
	{
		// try
		// {
		// sendPacketToAllClients(new Packet01Disconnect("##", de.dakror.spamwars.net.packet.Packet01Disconnect.Cause.SERVER_CLOSED));
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// }
		running = false;
		// if (updater != null) updater.closeRequested = true;
		socket.close();
	}
}
