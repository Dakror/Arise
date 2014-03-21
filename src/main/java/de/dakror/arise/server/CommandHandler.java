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
			case "cls":
			case "clear":
			{
				AriseServer.log.setText(new SimpleDateFormat("'['HH:mm:ss']: '").format(new Date()) + "Log cleared.");
				break;
			}
			case "help":
			{
				CFG.p("Available commands:");
				CFG.p("STOP - saves all data and closes the server.");
				CFG.p("CLS / CLEAR - clears the log area.");
				break;
			}
			default:
				Server.err("Unknown command. Type help for a list of available commands.");
		}
	}
}
