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
				if (parts.length != 4) CFG.e("Invalid parameters! Usage: WORLD <int:id> <String:name> <int:speed>");
				
				try
				{
					int id = Integer.parseInt(parts[1]);
					int speed = Integer.parseInt(parts[3]);
					
					if (DBManager.createWorld(id, parts[2], speed)) CFG.p("Successfully created world " + parts[2] + " (#" + id + ")");
					else CFG.e("There's a world with this id already!");
				}
				catch (Exception e)
				{
					CFG.e("Invalid parameters! Usage: WORLD <int:id> <String:name> <int:speed>");
				}
			}
			case "help":
			{
				CFG.p("Available commands:");
				CFG.p("STOP                      - saves all data and closes the server.");
				CFG.p("CLS / CLEAR               - clears the log area.");
				CFG.p("DIR                       - prints the directory path, where the database is located.");
				CFG.p("WORLD <int:id> <String:name> <int:speed> - creates a new world.");
				break;
			}
			default:
				Server.err("Unknown command. Type help for a list of available commands.");
		}
	}
}
