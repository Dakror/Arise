package de.dakror.arise.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import de.dakror.arise.Arise;
import de.dakror.arise.game.Game;
import de.dakror.arise.layer.MPLayer;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet00Handshake;
import de.dakror.arise.net.packet.Packet01Login;
import de.dakror.arise.settings.CFG;
import de.dakror.arise.util.Assistant;
import de.dakror.gamesetup.layer.Layer;

/**
 * @author Dakror
 */
public class Client extends Thread
{
	public boolean running;
	
	DatagramSocket socket;
	InetAddress serverIP;
	
	public Client()
	{
		try
		{
			socket = new DatagramSocket();
			setName("Client-Thread");
			setPriority(MAX_PRIORITY);
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
			byte[] data = new byte[Server.PACKETSIZE];
			
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try
			{
				socket.receive(packet);
				parsePacket(data);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void parsePacket(byte[] data)
	{
		PacketTypes type = Packet.lookupPacket(data[0]);
		
		Packet p = null;
		
		switch (type)
		{
			case INVALID:
			{
				CFG.p("received invalid packet: " + new String(data));
				return;
			}
			case LOGIN:
			{
				p = new Packet01Login(data);
				break;
			}
			default:
				CFG.p("reveived unhandled packet: " + type + " [" + Packet.readData(data) + "]");
				
		}
		if (p != null)
		{
			for (Layer l : Game.currentGame.layers)
				if (l instanceof MPLayer) ((MPLayer) l).onReceivePacket(p);
		}
	}
	
	public void sendPacket(Packet p) throws IOException
	{
		if (serverIP == null) return;
		
		byte[] data = p.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, serverIP, Server.PORT);
		socket.send(packet);
	}
	
	public boolean connectToServer() throws Exception
	{
		if (!Game.inLan)
		{
			serverIP = InetAddress.getByName("arisesv.dakror.de");
			
			socket.setSoTimeout(1000);
			sendPacket(new Packet00Handshake());
			DatagramPacket packet = new DatagramPacket(new byte[Server.PACKETSIZE], Server.PACKETSIZE);
			try
			{
				socket.receive(packet);
				
				PacketTypes type = Packet.lookupPacket(packet.getData()[0]);
				
				socket.setSoTimeout(0);
				
				return type == PacketTypes.HANDSHAKE;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				socket.setSoTimeout(0);
				return false;
			}
		}
		else serverIP = getLanServerIP();
		
		return serverIP != null;
	}
	
	public InetAddress getLanServerIP() throws Exception
	{
		socket.setBroadcast(true);
		socket.setSoTimeout(1000);
		
		byte[] data = new Packet00Handshake().getData();
		socket.send(new DatagramPacket(data, data.length, Arise.lanserverIP == null ? Assistant.getBroadcastAddress() : InetAddress.getByName(Arise.lanserverIP), Server.PORT));
		DatagramPacket packet = new DatagramPacket(new byte[Server.PACKETSIZE], Server.PACKETSIZE);
		try
		{
			socket.receive(packet);
			
			PacketTypes type = Packet.lookupPacket(packet.getData()[0]);
			
			socket.setBroadcast(false);
			socket.setSoTimeout(0);
			
			if (type == PacketTypes.HANDSHAKE) return packet.getAddress();
			else return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			socket.setBroadcast(false);
			socket.setSoTimeout(0);
			return null;
		}
	}
}
