package de.dakror.arise.server;

import java.awt.Point;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONObject;

import de.dakror.arise.game.building.Building;
import de.dakror.arise.net.Server;
import de.dakror.arise.net.User;
import de.dakror.arise.net.packet.Packet03World;
import de.dakror.arise.net.packet.Packet04City;
import de.dakror.arise.net.packet.Packet06Building;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.ui.ArmyLabel;
import de.dakror.gamesetup.util.Helper;

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
			s.executeUpdate("CREATE TABLE IF NOT EXISTS CITIES(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, NAME varchar(50) NOT NULL, X INTEGER NOT NULL, Y INTEGER NOT NULL, USER_ID INTEGER NOT NULL, WORLD_ID INTEGER NOT NULL, LEVEL INTEGER NOT NULL, ARMY text NOT NULL, WOOD FLOAT NOT NULL, STONE FLOAT NOT NULL, GOLD FLOAT NOT NULL)");
			s.executeUpdate("CREATE TABLE IF NOT EXISTS BUILDINGS(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, CITY_ID INTEGER NOT NULL, TYPE INTEGER NOT NULL, LEVEL INTEGER NOT NULL, X INTEGER NOT NULL, Y INTEGER NOT NULL,STAGE INTEGER NOT NULL, TIMELEFT INTEGER NOT NULL, META text)");
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
			connection.createStatement().executeUpdate("INSERT INTO CITIES(NAME, X, Y, USER_ID, WORLD_ID, LEVEL, ARMY, WOOD, STONE, GOLD) VALUES('Neue Stadt', " + p.x + ", " + p.y + ", " + user.getId() + ", " + worldId + ", 0,  '0:0', 300, 300, 300)");
			
			ResultSet rs3 = connection.createStatement().executeQuery("SELECT ID FROM CITIES WHERE USER_ID = " + user.getId());
			int cityId = rs3.getInt(1);
			
			connection.createStatement().executeUpdate("INSERT INTO BUILDINGS(CITY_ID, TYPE, LEVEL, X, Y, STAGE, TIMELEFT) VALUES(" + cityId + ", 1, 0, 15, 7, 1, 0)");
			
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static Packet04City getSpawnCity(int worldId, int userId)
	{
		try
		{
			JSONObject users = getUsersFromWebsite();
			ResultSet rs = connection.createStatement().executeQuery("SELECT ID, X, Y, USER_ID, LEVEL, NAME FROM CITIES WHERE WORLD_ID = " + worldId + " AND USER_ID = " + userId);
			rs.next();
			
			return new Packet04City(1, rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getString(6), users.getString("" + rs.getInt(4)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
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
	
	public static ArrayList<Packet04City> getCities(int worldId)
	{
		ArrayList<Packet04City> packets = new ArrayList<>();
		JSONObject users = getUsersFromWebsite();
		try
		{
			ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT() as CITIES FROM CITIES WHERE WORLD_ID = " + worldId);
			rs.next();
			int cities = rs.getInt(1);
			
			ResultSet rs2 = connection.createStatement().executeQuery("SELECT ID, X, Y, USER_ID, LEVEL, NAME FROM CITIES WHERE WORLD_ID = " + worldId);
			while (rs2.next())
			{
				Packet04City p = new Packet04City(cities, rs2.getInt(1), rs2.getInt(2), rs2.getInt(3), rs2.getInt(4), rs2.getInt(5), rs2.getString(6), users.getString("" + rs2.getInt(4)));
				packets.add(p);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return packets;
	}
	
	public static JSONObject getUsersFromWebsite()
	{
		try
		{
			return new JSONObject(Helper.getURLContent(new URL("http://dakror.de/mp-api/users")));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static Resources getCityResources(int cityId)
	{
		Resources res = new Resources();
		try
		{
			ResultSet rs = connection.createStatement().executeQuery("SELECT ARMY, WOOD, STONE, GOLD FROM CITIES WHERE ID = " + cityId);
			rs.next();
			
			ResultSet rs2 = connection.createStatement().executeQuery("SELECT COUNT() FROM BUILDINGS WHERE CITY_ID = " + cityId);
			rs2.next();
			int buildings = rs2.getInt(1);
			
			String a = rs.getString(1);
			if (a.trim().length() > 0)
			{
				String[] army = a.split(":");
				for (int i = 0; i < army.length; i++)
					res.set(ArmyLabel.ARMY[i], Integer.parseInt(army[i]));
			}
			res.set(Resource.WOOD, rs.getFloat(2));
			res.set(Resource.STONE, rs.getFloat(3));
			res.set(Resource.GOLD, rs.getFloat(4));
			res.set(Resource.BUILDINGS, buildings);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return res;
	}
	
	public static ArrayList<Packet06Building> getCityBuildings(int cityId)
	{
		ArrayList<Packet06Building> p = new ArrayList<>();
		try
		{
			ResultSet rs = connection.createStatement().executeQuery("SELECT ID, TYPE, LEVEL, X, Y, STAGE, TIMELEFT, META FROM BUILDINGS WHERE CITY_ID = " + cityId);
			while (rs.next())
				p.add(new Packet06Building(cityId, rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8)));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return p;
	}
	
	public static Packet06Building getCityBuilding(int cityId, int buildingId)
	{
		try
		{
			ResultSet rs = connection.createStatement().executeQuery("SELECT ID, TYPE, LEVEL, X, Y, STAGE, TIMELEFT, META FROM BUILDINGS WHERE CITY_ID = " + cityId + " AND ID = " + buildingId);
			if (rs.next()) return new Packet06Building(cityId, rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static void updateBuildingTimers()
	{
		try
		{
			connection.createStatement().executeUpdate("UPDATE BUILDINGS SET TIMELEFT = TIMELEFT - 1 WHERE TIMELEFT > 0");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean isCityFromUser(int cityId, User user)
	{
		if (user == null) return false;
		try
		{
			ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM CITIES WHERE ID = " + cityId + " AND USER_ID = " + user.getId());
			return rs.next();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean renameCity(int cityId, String newName, User user)
	{
		try
		{
			ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM CITIES WHERE ID = " + cityId);
			if (!rs.next()) return false;
			
			connection.createStatement().executeUpdate("UPDATE CITIES SET NAME = '" + newName + "' WHERE ID = " + cityId);
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean buy(int cityId, Resources res)
	{
		try
		{
			ResultSet rs = connection.createStatement().executeQuery("SELECT ((WOOD - " + res.get(Resource.WOOD) + " >= 0) + (STONE - " + res.get(Resource.STONE) + " >= 0) + (GOLD - " + res.get(Resource.GOLD) + " >= 0)) == 3 as CANEFFORT FROM CITIES WHERE ID = " + cityId + " AND CANEFFORT == 1");
			if (!rs.next()) return false;
			
			connection.createStatement().executeUpdate("UPDATE CITIES SET WOOD = WOOD - " + res.get(Resource.WOOD) + ", STONE = STONE - " + res.get(Resource.STONE) + ", GOLD = GOLD - " + res.get(Resource.GOLD) + " WHERE ID = " + cityId);
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static int placeBuilding(int cityId, int type, int x, int y)
	{
		try
		{
			Building b = Building.getBuildingByTypeId(x / 32, y / 32, 0, type);
			
			if (buy(cityId, b.getBuildingCosts()))
			{
				connection.createStatement().executeUpdate("INSERT INTO BUILDINGS(CITY_ID, TYPE, LEVEL, X, Y, STAGE, TIMELEFT) VALUES(" + cityId + ", " + type + ", 0, " + x + ", " + x + ", 0, " + b.getStageChangeSeconds() + ")");
				ResultSet rs = connection.createStatement().executeQuery("SELECT last_insert_rowid()");
				rs.next();
				return rs.getInt(1);
			}
			else return 0;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return 0;
		}
	}
}
