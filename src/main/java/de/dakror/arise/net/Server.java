package de.dakror.arise.net;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet00Handshake;
import de.dakror.arise.net.packet.Packet01Login;
import de.dakror.arise.net.packet.Packet02Disconnect;
import de.dakror.arise.net.packet.Packet02Disconnect.Cause;
import de.dakror.arise.net.packet.Packet03World;
import de.dakror.arise.net.packet.Packet04City;
import de.dakror.arise.net.packet.Packet05Resources;
import de.dakror.arise.server.DBManager;
import de.dakror.arise.settings.CFG;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class Server extends Thread
{
	public static final int PORT = 14744;
	public static final int PACKETSIZE = 255; // bytes
	
	public static File dir;
	
	public boolean running;
	public CopyOnWriteArrayList<User> clients = new CopyOnWriteArrayList<>();
	
	DatagramSocket socket;
	
	public Server(InetAddress ip)
	{
		try
		{
			dir = new File(CFG.DIR, "Server");
			dir.mkdir();
			socket = new DatagramSocket(new InetSocketAddress(ip, Server.PORT));
			setName("Server-Thread");
			setPriority(MAX_PRIORITY);
			out("Connecting to database");
			DBManager.init();
			
			out("Starting server at " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
			start();
		}
		catch (BindException e)
		{
			err("There is a server already running on this machine!");
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
				err("Received invalid packet: " + new String(data));
				break;
			}
			case HANDSHAKE:
			{
				try
				{
					sendPacket(new Packet00Handshake(), new User(0, address, port));
					out("Shook hands with: " + address.getHostAddress() + ":" + port);
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
					if (loggedIn)
					{
						String[] parts = s.split(":");
						User u = new User(Integer.parseInt(parts[1].trim()), address, port);
						out("User " + parts[2].trim() + " logged in. " + "(#" + u.getId() + ")");
						sendPacket(new Packet01Login(parts[2], u.getId(), loggedIn), u);
						clients.add(u);
					}
					else sendPacket(new Packet01Login(p.getUsername(), 0, loggedIn), new User(0, address, port));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
			}
			case DISCONNECT:
			{
				Packet02Disconnect p = new Packet02Disconnect(data);
				for (User u : clients)
				{
					if (u.getId() == p.getUserId() && address.equals(u.getIP()))
					{
						try
						{
							sendPacket(new Packet02Disconnect(0, Cause.SERVER_CONFIRMED), u);
							out("User disconnected: #" + u.getId() + " (" + p.getCause().name() + ")");
							clients.remove(u);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
				break;
			}
			case WORLD:
			{
				try
				{
					Packet03World p = new Packet03World(data);
					User user = getUserForIP(address, port);
					boolean spawn = DBManager.spawnPlayer(p.getId(), user);
					out("Player's first visit on world? " + spawn);
					sendPacket(DBManager.getWorldForId(p.getId()), user);
					sendPacketToAllClientsExceptOne(DBManager.getSpawnCity(p.getId(), user.getId()), user);
					break;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			case CITY:
			{
				Packet04City p = new Packet04City(data);
				try
				{
					for (Packet04City packet : DBManager.getCities(p.getWorldId()))
						sendPacket(packet, getUserForIP(address, port));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
			}
			case RESOURCES:
			{
				try
				{
					Packet05Resources p = new Packet05Resources(data);
					sendPacket(new Packet05Resources(p.getCityId(), DBManager.getCityResources(p.getCityId())), getUserForIP(address, port));
					break;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			default:
				err("Reveived unhandled packet (" + address.getHostAddress() + ":" + port + ") " + type + " [" + Packet.readData(data) + "]");
		}
	}
	
	public void sendPacketToAllClients(Packet p) throws Exception
	{
		for (User u : clients)
			sendPacket(p, u);
	}
	
	public void sendPacketToAllClientsExceptOne(Packet p, User exception) throws IOException
	{
		for (User u : clients)
		{
			if (exception.getId() == 0)
			{
				if (exception.getIP().equals(u.getIP()) && exception.getPort() == u.getPort()) continue;
			}
			else if (exception.getId() == u.getId()) continue;
			sendPacket(p, u);
		}
	}
	
	public void sendPacket(Packet p, User u) throws IOException
	{
		byte[] data = p.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, u.getIP(), u.getPort());
		
		socket.send(packet);
	}
	
	public User getUserForIP(InetAddress address, int port)
	{
		for (User u : clients)
			if (u.getIP().equals(address) && u.getPort() == port) return u;
		
		return null;
	}
	
	public void shutdown()
	{
		try
		{
			sendPacketToAllClients(new Packet02Disconnect(0, Packet02Disconnect.Cause.SERVER_CLOSED));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		running = false;
		// if (updater != null) updater.closeRequested = true;
		socket.close();
	}
	
	public static void out(Object... p)
	{
		String timestamp = new SimpleDateFormat("'['HH:mm:ss']: '").format(new Date());
		if (p.length == 1) System.out.println(timestamp + p[0]);
		else System.out.println(timestamp + Arrays.toString(p));
	}
	
	public static void err(Object... p)
	{
		String timestamp = new SimpleDateFormat("'['HH:mm:ss']: '").format(new Date());
		if (p.length == 1) System.err.println(timestamp + p[0]);
		else System.err.println(timestamp + Arrays.toString(p));
	}
}
