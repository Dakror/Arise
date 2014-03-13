package de.dakror.arise.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import de.dakror.arise.game.Game;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet00Handshake;
import de.dakror.arise.settings.CFG;

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
		
		switch (type)
		{
			case INVALID:
			{
				CFG.p("received invalid packet: " + new String(data));
				return;
			}
			default:
				CFG.p("reveived unhandled packet: " + type + " [" + Packet.readData(data) + "]");
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
		if (!Game.inLan) serverIP = InetAddress.getByName("arisesv.dakror.de");
		else serverIP = getLanServerIP();
		
		return serverIP != null;
	}
	
	public InetAddress getLanServerIP() throws Exception
	{
		socket.setBroadcast(true);
		socket.setSoTimeout(1000);
		
		byte[] data = new Packet00Handshake().getData();
		socket.send(new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"), Server.PORT));
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
			socket.setBroadcast(false);
			socket.setSoTimeout(0);
			return null;
		}
	}
}
