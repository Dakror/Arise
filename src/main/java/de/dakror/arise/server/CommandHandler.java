package de.dakror.arise.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.dakror.arise.AriseServer;
import de.dakror.arise.net.Server;
import de.dakror.arise.settings.CFG;

/**
 * @author Dakror
 */
public class CommandHandler
{
	public static void handle(String input)
	{
		input = input.trim();
		String[] parts = input.split(" ");
		
		switch (parts[0].toLowerCase())
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
			case "cls":
			case "clear":
			{
				AriseServer.log.setText(new SimpleDateFormat("'['HH:mm:ss']: '").format(new Date()) + "Log cleared.");
				break;
			}
			case "world":
			{
				if (parts.length == 2 && parts[1].equals("-list"))
				{
					WorldData[] worlds = DBManager.listWorlds();
					for (WorldData w : worlds)
						CFG.p("  " + w.name + " (#" + w.id + ") Speed " + w.speed);
					
					if (worlds.length == 0) CFG.p("There aren't any worlds yet. Create some with WORLD -add");
				}
				else if (parts.length == 5 && parts[1].equals("-add"))
				{
					try
					{
						int id = Integer.parseInt(parts[2]);
						int speed = Integer.parseInt(parts[4]);
						
						if (DBManager.createWorld(id, parts[3], speed)) CFG.p("Successfully created world " + parts[3] + " (#" + id + ")");
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
			case "help":
			{
				CFG.p("Available commands:");
				CFG.p("STOP - saves all data and closes the server.");
				CFG.p("CLS / CLEAR - clears the log area.");
				CFG.p("DIR - prints the directory path, where the database is located.");
				CFG.p("WORLD [-add <int:id> <String:name> <int:speed>] [-list] - [creates a new world] [lists all existing worlds].");
				break;
			}
			default:
				Server.err("Unknown command. Type help for a list of available commands.");
		}
	}
}
