package de.dakror.arise.server;

import java.awt.Point;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.dakror.arise.net.Server;
import de.dakror.arise.net.User;
import de.dakror.arise.net.packet.Packet03World;

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
			s.executeUpdate("CREATE TABLE IF NOT EXISTS WORLDS(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, NAME varchar(50) NOT NULL, SPEED INTEGER NOT NULL)");
			s.executeUpdate("CREATE TABLE IF NOT EXISTS CITIES(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, NAME varchar(50) NOT NULL, X INTEGER NOT NULL, Y INTEGER NOT NULL, USER_ID INTEGER NOT NULL, WORLD_ID INTEGER NOT NULL, LEVEL INTEGER NOT NULL, DATA text NOT NULL, ARMY text NOT NULL, WOOD FLOAT NOT NULL, STONE FLOAT NOT NULL, GOLD FLOAT NOT NULL)");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static Packet03World getWorldForId(int worldId)
	{
		try
		{
			ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM WORLDS WHERE ID = " + worldId);
			if (!rs.next()) return new Packet03World(-1);
			
			return new Packet03World(rs.getInt(1), rs.getString(2), rs.getInt(3));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return new Packet03World(-1);
		}
	}
	
	public static boolean spawnPlayer(int worldId, User user)
	{
		try
		{
			ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM CITIES WHERE USER_ID = " + user.getId());
			if (rs.next()) return false;
			
			ResultSet rs2 = connection.createStatement().executeQuery("SELECT COUNT() as COUNT FROM CITIES WHERE WORLD_ID = " + worldId);
			rs2.next();
			
			int cities = rs2.getInt(1);
			Point p = CitySpawner.spawnCity(cities, worldId);
			connection.createStatement().executeUpdate("INSERT INTO CITIES(NAME, X, Y, USER_ID, WORLD_ID, LEVEL, DATA, ARMY, WOOD, STONE, GOLD) VALUES('Neue Stadt', " + p.x + ", " + p.y + ", " + user.getId() + ", " + worldId + ", 0, '1:0:15:7:1:0;', '0:0', 300, 300, 300)");
			
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean cityExists(int x, int y, int worldId)
	{
		try
		{
			ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM CITIES WHERE WORLD_ID = " + worldId + " AND X = " + x + " AND Y = " + y);
			return rs.next();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
