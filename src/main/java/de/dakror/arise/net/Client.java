/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


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
import de.dakror.arise.settings.CFG;
import de.dakror.gamesetup.layer.Layer;

/**
 * @author Dakror
 */
public class Client extends Thread {
	public boolean running;
	
	DatagramSocket socket;
	InetAddress serverIP;
	
	public Client() {
		try {
			socket = new DatagramSocket();
			setName("Client-Thread");
			setPriority(MAX_PRIORITY);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		running = true;
		while (running) {
			byte[] data = new byte[Server.PACKETSIZE];
			
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
				parsePacket(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void parsePacket(byte[] data) {
		PacketTypes type = Packet.lookupPacket(data[0]);
		
		Packet p = Packet.newInstance(type, data);
		
		switch (type) {
			case INVALID: {
				CFG.e("received invalid packet: " + new String(data));
				return;
			}
			default:
				break;
		}
		
		if (p != null) {
			for (Layer l : Game.currentGame.layers)
				if (l instanceof MPLayer) ((MPLayer) l).onReceivePacket(p);
		}
	}
	
	public void sendPacket(Packet p) throws IOException {
		if (serverIP == null) return;
		
		byte[] data = p.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, serverIP, Server.PORT);
		socket.send(packet);
	}
	
	public boolean connectToServer() throws Exception {
		serverIP = Arise.localServer ? InetAddress.getLocalHost() : InetAddress.getByName("h2284175.stratoserver.net");
		
		socket.setSoTimeout(1000);
		sendPacket(new Packet00Handshake());
		DatagramPacket packet = new DatagramPacket(new byte[Server.PACKETSIZE], Server.PACKETSIZE);
		try {
			socket.receive(packet);
			
			PacketTypes type = Packet.lookupPacket(packet.getData()[0]);
			
			socket.setSoTimeout(0);
			
			return type == PacketTypes.HANDSHAKE;
		} catch (Exception e) {
			e.printStackTrace();
			socket.setSoTimeout(0);
			return false;
		}
	}
}
