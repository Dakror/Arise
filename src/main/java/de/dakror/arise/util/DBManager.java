package de.dakror.arise.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import de.dakror.arise.net.Server;

/**
 * @author Dakror
 */
public class DBManager
{
	public static File database;
	
	static Connection connection;
	
	public static void init()
	{
		try
		{
			database = new File(Server.dir, "arise.db");
			database.createNewFile();
			
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + database.getPath().replace("\\", "/"));
			
			Statement s = connection.createStatement();
			s.executeUpdate("CREATE TABLE IF NOT EXISTS ARISE_WORLDS(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, NAME varchar(50) NOT NULL, SPEED INTEGER NOT NULL)");
			s.executeUpdate("CREATE TABLE IF NOT EXISTS ARISE_CITIES(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, NAME varchar(50) NOT NULL, X INTEGER NOT NULL, Y INTEGER NOT NULL, USER_ID INTEGER NOT NULL, WORLD_ID INTEGER NOT NULL, LEVEL INTEGER NOT NULL, DATA text NOT NULL, ARMY text NOT NULL, WOOD FLOAT NOT NULL, STONE FLOAT NOT NULL, GOLD FLOAT NOT NULL, LASTVISITED BIGINT NOT NULL)");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
