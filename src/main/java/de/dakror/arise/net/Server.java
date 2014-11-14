package de.dakror.arise.net;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

import de.dakror.arise.AriseServer;
import de.dakror.arise.game.Game;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet00Handshake;
import de.dakror.arise.net.packet.Packet01Login;
import de.dakror.arise.net.packet.Packet01Login.Response;
import de.dakror.arise.net.packet.Packet02Disconnect;
import de.dakror.arise.net.packet.Packet02Disconnect.Cause;
import de.dakror.arise.net.packet.Packet03World;
import de.dakror.arise.net.packet.Packet04City;
import de.dakror.arise.net.packet.Packet05Resources;
import de.dakror.arise.net.packet.Packet06Building;
import de.dakror.arise.net.packet.Packet07RenameCity;
import de.dakror.arise.net.packet.Packet08PlaceBuilding;
import de.dakror.arise.net.packet.Packet09BuildingStage;
import de.dakror.arise.net.packet.Packet10Attribute;
import de.dakror.arise.net.packet.Packet10Attribute.Key;
import de.dakror.arise.net.packet.Packet11DeconstructBuilding;
import de.dakror.arise.net.packet.Packet12UpgradeBuilding;
import de.dakror.arise.net.packet.Packet15BarracksBuildTroop;
import de.dakror.arise.net.packet.Packet16BuildingMeta;
import de.dakror.arise.net.packet.Packet17CityAttack;
import de.dakror.arise.net.packet.Packet19Transfer;
import de.dakror.arise.server.DBManager;
import de.dakror.arise.server.ServerUpdater;
import de.dakror.arise.settings.CFG;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class Server extends Thread {
	public static Server currentServer;
	
	public static final int PORT = 14744;
	public static final int PACKETSIZE = 255; // bytes
	
	public static File dir;
	
	public boolean running;
	public CopyOnWriteArrayList<User> clients = new CopyOnWriteArrayList<>();
	
	ServerUpdater updater;
	
	DatagramSocket socket;
	
	public BufferedWriter logWriter;
	
	public Server(InetAddress ip) {
		currentServer = this;
		try {
			dir = new File(CFG.DIR, "Server");
			dir.mkdir();
			socket = new DatagramSocket(new InetSocketAddress(ip, Server.PORT));
			setName("Server Thread");
			setPriority(MAX_PRIORITY);
			out("Connecting to database");
			DBManager.init();
			out("Fetching configuration");
			Game.loadConfig();
			if (AriseServer.isLogging()) {
				logWriter = new BufferedWriter(new FileWriter(new File(new File(AriseServer.properties.getProperty("logfile")), "status.log")));
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						try {
							logWriter.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
			updater = new ServerUpdater();
			
			out("Starting server at " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
			
			start();
		} catch (BindException e) {
			err("There is a server already running on this machine!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		running = true;
		while (running) {
			byte[] data = new byte[PACKETSIZE];
			
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
				parsePacket(data, packet.getAddress(), packet.getPort());
			} catch (SocketException e) {} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void parsePacket(byte[] data, InetAddress address, int port) {
		PacketTypes type = Packet.lookupPacket(data[0]);
		final User user = getUserForIP(address, port);
		if (user != null) user.interact();
		
		if (AriseServer.trafficLog != null) {
			AriseServer.trafficLog.setText(AriseServer.trafficLog.getText() + new SimpleDateFormat("'['HH:mm:ss']: '").format(new Date()) + "< " + address.getHostAddress() + ":" + port + " " + type.name() + "\n");
			AriseServer.trafficLog.setCaretPosition(AriseServer.trafficLog.getDocument().getLength());
		}
		
		switch (type) {
			case INVALID: {
				err("Received invalid packet: " + new String(data));
				break;
			}
			case HANDSHAKE: {
				try {
					sendPacket(new Packet00Handshake(), user == null ? new User(0, 0, "", address, port) : user);
					if (user == null) out("Shook hands with: " + address.getHostAddress() + ":" + port);
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			case LOGIN: {
				try {
					Packet01Login p = new Packet01Login(data);
					String s = Helper.getURLContent(new URL("http://dakror.de/mp-api/login_noip.php?username=" + p.getUsername() + "&password=" + p.getPwdMd5()));
					boolean loggedIn = s.contains("true");
					boolean worldExists = DBManager.getWorldForId(p.getWorldId()).getId() != -1;
					if (loggedIn && worldExists) {
						String[] parts = s.split(":");
						User u = new User(Integer.parseInt(new String(parts[1]).trim()), p.getWorldId(), new String(parts[2]), address, port);
						boolean alreadyLoggedIn = getUserForId(u.getId()) != null;
						
						if (alreadyLoggedIn) {
							out("Refused login of " + address.getHostAddress() + ":" + port + " (" + Response.ALREADY_LOGGED_IN.name() + ")");
							sendPacket(new Packet01Login(p.getUsername(), 0, p.getWorldId(), Response.ALREADY_LOGGED_IN), u);
						} else {
							out("User " + new String(parts[2]).trim() + " (#" + u.getId() + ")" + " logged in on world #" + p.getWorldId() + ".");
							sendPacket(new Packet01Login(new String(parts[2]), u.getId(), p.getWorldId(), Response.LOGIN_OK), u);
							clients.add(u);
						}
					} else {
						out("Refused login of " + address.getHostAddress() + ":" + port + " (" + (!loggedIn ? Response.BAD_LOGIN : Response.BAD_WORLD_ID).name() + ")");
						sendPacket(new Packet01Login(p.getUsername(), 0, p.getWorldId(), !loggedIn ? Response.BAD_LOGIN : Response.BAD_WORLD_ID), new User(0, 0, "", address, port));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			case DISCONNECT: {
				Packet02Disconnect p = new Packet02Disconnect(data);
				for (User u : clients) {
					if (u.getId() == p.getUserId() && address.equals(u.getIP())) {
						try {
							sendPacket(new Packet02Disconnect(0, Cause.SERVER_CONFIRMED), u);
							out("User disconnected: #" + u.getId() + " (" + p.getCause().name() + ")");
							clients.remove(u);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				break;
			}
			case WORLD: {
				try {
					Packet03World p = new Packet03World(data);
					boolean spawn = DBManager.spawnPlayer(p.getId(), user);
					out("Player's first visit on world? " + spawn);
					sendPacket(DBManager.getWorldForId(p.getId()), user);
					for (User u : clients) {
						if (u.getWorldId() == user.getWorldId() && !(user.getIP().equals(u.getIP()) && user.getPort() == u.getPort())) {
							sendPacket(DBManager.getSpawnCity(p.getId(), user.getId()), u);
						}
					}
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			case RESOURCES: {
				try {
					Packet05Resources p = new Packet05Resources(data);
					if (DBManager.isCityFromUser(p.getCityId(), user)) sendPacket(new Packet05Resources(p.getCityId(), DBManager.getCityResources(p.getCityId())), user);
					
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			case BUILDING: {
				Packet06Building p = new Packet06Building(data);
				if (p.getBuildingType() == 0 && DBManager.isCityFromUser(p.getCityId(), user)) {
					try {
						for (Packet06Building packet : DBManager.getCityBuildings(p.getCityId()))
							sendPacket(packet, user);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			}
			case RENAMECITY: {
				Packet07RenameCity p = new Packet07RenameCity(data);
				if (DBManager.isCityFromUser(p.getCityId(), user)) {
					boolean worked = DBManager.renameCity(p.getCityId(), p.getNewName(), user);
					try {
						sendPacketToAllClients(new Packet07RenameCity(p.getCityId(), worked ? p.getNewName() : "#false#"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			}
			case PLACEBUILDING: {
				Packet08PlaceBuilding p = new Packet08PlaceBuilding(data);
				if (DBManager.isCityFromUser(p.getCityId(), user)) {
					int id = DBManager.placeBuilding(p.getCityId(), p.getBuildingType(), p.getX(), p.getY());
					if (id != 0) {
						try {
							sendPacket(DBManager.getCityBuilding(p.getCityId(), id), user);
							sendPacket(new Packet05Resources(p.getCityId(), DBManager.getCityResources(p.getCityId())), user);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				break;
			}
			case ATTRIBUTE: {
				Packet10Attribute p = new Packet10Attribute(data);
				if (user != null) {
					switch (p.getKey()) {
						case city: {
							user.setCity(Integer.parseInt(p.getValue()));
							break;
						}
						case world_data: {
							try {
								// -- cities -- //
								for (Packet04City packet : DBManager.getCities(Integer.parseInt(p.getValue())))
									sendPacket(packet, user);
								
								// -- transfers -- //
								for (Packet19Transfer packet : DBManager.getTransfers(user))
									sendPacket(packet, user);
								
								sendPacket(new Packet10Attribute(Key.loading_complete), user);
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						}
						default:
							;
					}
				}
				
				break;
			}
			case DECONSTRUCTBUILDING: {
				Packet11DeconstructBuilding p = new Packet11DeconstructBuilding(data);
				if (DBManager.isCityFromUser(p.getCityId(), user)) {
					int timeleft = 0;
					if ((timeleft = DBManager.deconstructBuilding(p.getCityId(), p.getBuildingId())) > -1) {
						try {
							sendPacket(new Packet09BuildingStage(p.getBuildingId(), 2, timeleft), user);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				break;
			}
			case UPGRADEBUILDING: {
				Packet12UpgradeBuilding p = new Packet12UpgradeBuilding(data);
				if (DBManager.isCityFromUser(p.getCityId(), user)) {
					int timeleft = 0;
					if ((timeleft = DBManager.upgradeBuilding(p.getCityId(), p.getBuildingId())) > -1) {
						try {
							sendPacket(new Packet09BuildingStage(p.getBuildingId(), 3, timeleft), user);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				break;
			}
			case BARRACKSBUILDTROOP: {
				Packet15BarracksBuildTroop p = new Packet15BarracksBuildTroop(data);
				
				if (DBManager.isCityFromUser(p.getCityId(), user)) {
					try {
						int timeleft = DBManager.barracksBuildTroops(p);
						if (timeleft > -1) {
							sendPacket(new Packet09BuildingStage(p.getBuildingId(), 1, timeleft), user);
							sendPacket(new Packet16BuildingMeta(p.getBuildingId(), p.getTroopType().ordinal() + ":" + p.getAmount()), user);
							sendPacket(new Packet05Resources(p.getCityId(), DBManager.getCityResources(p.getCityId())), user);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				break;
			}
			case CITYATTACK: {
				final Packet17CityAttack p = new Packet17CityAttack(data);
				
				if (DBManager.isCityFromUser(p.getAttCityId(), user) && !DBManager.isCityFromUser(p.getDefCityId(), user) && p.getAttArmy().getLength() > 0) {
					try {
						sendPacket(DBManager.transferAttackTroops(p), user);
						sendPacket(new Packet05Resources(p.getAttCityId(), DBManager.getCityResources(p.getAttCityId())), user);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				break;
			}
			default:
				err("Received unhandled packet (" + address.getHostAddress() + ":" + port + ") " + type + " [" + Packet.readData(data) + "]");
		}
	}
	
	public void sendPacketToAllClients(Packet p) throws Exception {
		for (User u : clients)
			sendPacket(p, u);
	}
	
	public void sendPacketToAllClientsExceptOne(Packet p, User exception) throws Exception {
		for (User u : clients) {
			if (exception.getId() == 0) {
				if (exception.getIP().equals(u.getIP()) && exception.getPort() == u.getPort()) continue;
			} else if (exception.getId() == u.getId()) continue;
			sendPacket(p, u);
		}
	}
	
	public void sendPacketToAllClientsOnWorld(Packet p, int worldId) throws Exception {
		for (User u : clients)
			if (u.getWorldId() == worldId) sendPacket(p, u);
	}
	
	public void sendPacket(Packet p, User u) throws Exception {
		if (u == null) throw new NullPointerException("user = null");
		
		byte[] data = p.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, u.getIP(), u.getPort());
		
		socket.send(packet);
		if (AriseServer.trafficLog != null) {
			AriseServer.trafficLog.setText(AriseServer.trafficLog.getText() + new SimpleDateFormat("'['HH:mm:ss']: '").format(new Date()) + "> " + u.getIP().getHostAddress() + ":" + u.getPort() + " " + p.getType().name() + "\n");
			AriseServer.trafficLog.setCaretPosition(AriseServer.trafficLog.getDocument().getLength());
		}
	}
	
	public User getUserForIP(InetAddress address, int port) {
		for (User u : clients)
			if (u.getIP().equals(address) && u.getPort() == port) return u;
		
		return null;
	}
	
	public User getUserForId(int id) {
		for (User u : clients)
			if (u.getId() == id) return u;
		
		return null;
	}
	
	public void shutdown() {
		try {
			sendPacketToAllClients(new Packet02Disconnect(0, Packet02Disconnect.Cause.SERVER_CLOSED));
		} catch (Exception e) {
			e.printStackTrace();
		}
		running = false;
		socket.close();
	}
	
	public static void out(Object... p) {
		String timestamp = new SimpleDateFormat("'['HH:mm:ss']: '").format(new Date());
		if (p.length == 1) System.out.println(timestamp + p[0]);
		else System.out.println(timestamp + Arrays.toString(p));
	}
	
	public static void err(Object... p) {
		String timestamp = new SimpleDateFormat("'['HH:mm:ss'] [ERROR]: '").format(new Date());
		if (p.length == 1) System.err.print(timestamp + p[0]);
		else System.err.print(timestamp + Arrays.toString(p));
	}
	
}
