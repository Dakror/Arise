package de.dakror.arise.server;

import java.io.File;
import java.io.IOException;

import de.dakror.arise.AriseServer;
import de.dakror.arise.net.Server;
import de.dakror.arise.net.User;
import de.dakror.arise.net.packet.Packet02Disconnect;
import de.dakror.arise.net.packet.Packet02Disconnect.Cause;
import de.dakror.arise.settings.CFG;
import de.dakror.arise.util.Assistant;

/**
 * @author Dakror
 */
public class CommandHandler extends Thread
{
	public CommandHandler()
	{
		setName("CommandHandler Thread");
		start();
	}
	
	@Override
	public void run()
	{
		while (AriseServer.server.running)
			handle(Assistant.readConsoleInput());
	}
	
	public static void handle(String input)
	{
		input = input.trim();
		String[] parts = input.split(" ");
		
		switch (new String(parts[0]).toLowerCase())
		{
			case "stop":
			{
				if (AriseServer.server.running)
				{
					CFG.p("Closing server");
					AriseServer.server.shutdown();
				}
				
				System.exit(0);
				break;
			}
			case "dir":
			{
				CFG.p(CFG.DIR.getPath());
				break;
			}
			case "world":
			{
				if (parts.length == 2 && new String(parts[1]).equals("-list"))
				{
					WorldData[] worlds = DBManager.listWorlds();
					for (WorldData w : worlds)
						CFG.p("  " + w.name + " (#" + w.id + ") Speed " + w.speed);
					
					if (worlds.length == 0) CFG.p("There aren't any worlds yet. Create some with WORLD -add");
				}
				else if (parts.length == 5 && new String(parts[1]).equals("-add"))
				{
					try
					{
						int id = Integer.parseInt(new String(parts[2]));
						int speed = Integer.parseInt(new String(parts[4]));
						
						if (DBManager.createWorld(id, new String(parts[3]), speed)) CFG.p("Successfully created world " + new String(parts[3]) + " (#" + id + ")");
						else CFG.e("There's a world with this id already!");
					}
					catch (Exception e)
					{
						CFG.e("Invalid parameters! Usage: WORLD [-add <int:id> <String:name> <int:speed>] [-list]");
					}
				}
				else CFG.e("Invalid parameters! Usage: WORLD [-add <int:id> <String:name> <int:speed>] [-list]");
				
				break;
			}
			case "kick":
			{
				if (parts.length == 2)
				{
					try
					{
						int id = Integer.parseInt(new String(parts[1]));
						User user = Server.currentServer.getUserForId(id);
						if (user == null) CFG.e("There currently is no player connected with id=" + id);
						else
						{
							Server.currentServer.sendPacket(new Packet02Disconnect(0, Cause.INACTIVE), user);
							Server.out("Kicked user: #" + user.getId() + " (" + Cause.INACTIVE + ")");
							Server.currentServer.clients.remove(user);
						}
					}
					catch (Exception e)
					{
						CFG.e("Invalid parameters! Usage: KICK <int:id>");
					}
				}
				else CFG.e("Invalid parameters! Usage: KICK <int:id>");
				break;
			}
			case "traffic":
			{
				try
				{
					AriseServer.createTrafficFrame();
					break;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			case "log":
			{
				if (parts.length == 2)
				{
					if (new File(new String(parts[1])).isDirectory())
					{
						AriseServer.properties.setProperty("logfile", new File(new String(parts[1])).getPath().replace("\\", "/"));
						AriseServer.saveProperties();
						CFG.p("Log directory set. Restart the server to make it take effect.");
					}
				}
				else CFG.e("Invalid parameters! Usage: LOG <String:directory>");
				break;
			}
			case "players":
			{
				if (Server.currentServer.clients.size() == 0) CFG.p("No users are logged in currently.");
				for (User u : Server.currentServer.clients)
					CFG.p(u.toString());
				break;
			}
			case "help":
			{
				CFG.p("Available commands:");
				CFG.p("STOP - Saves all data and closes the server.");
				CFG.p("DIR - Prints the directory path where the database is located.");
				CFG.p("WRLD [-add <int:id> <String:name> <int:speed>] [-list] - [creates a new world] [lists all existing worlds].");
				CFG.p("KICK <int:id> - Kicks the player with the given id.");
				CFG.p("TRAFFIC - Opens the traffic monitoring console.");
				CFG.p("LOG <String:directory> - Writes from now on the server log to generated files in the given directory. Takes effect after restart.");
				CFG.p("PLAYERS - Lists the currently logged in players.");
				break;
			}
			default:
				Server.err("Unknown command. Type help for a list of available commands.");
		}
	}
}
