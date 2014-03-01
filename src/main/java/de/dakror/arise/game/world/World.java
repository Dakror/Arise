package de.dakror.arise.game.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.arise.game.Game;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class World extends Layer
{
	String name;
	int speed;
	int id;
	int width;
	int height;
	float x;
	float y;
	
	long lastCheck;
	
	BufferedImage bi;
	JSONArray citiesData;
	
	public World(int id)
	{
		try
		{
			this.id = id;
			JSONObject data = new JSONObject(Helper.getURLContent(new URL("http://dakror.de/arise/world?get=" + id)));
			speed = data.getInt("SPEED");
			name = data.getString("NAME");
			
			updateGround();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void init()
	{}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(bi, 0, 0, null);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		if (lastCheck == 0 || tick - lastCheck > 3600) // check once a minute
		{
			try
			{
				JSONArray newData = new JSONArray(Helper.getURLContent(new URL("http://dakror.de/arise/world?cities=true&id=" + id)));
				if (citiesData == null || !citiesData.equals(newData))
				{
					citiesData = newData;
					reloadWorld();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			lastCheck = tick;
		}
	}
	
	public void updateGround()
	{
		int minX = -65536, minY = -65536, maxX = -65536, maxY = -65536;
		for (Component c : components)
		{
			if (c.getX() < minX || minX == -65536) minX = c.getX();
			if (c.getY() < minY || minY == -65536) minY = c.getY();
			if (c.getX() + c.getWidth() > maxX || maxX == -65536) maxX = c.getX() + c.getWidth();
			if (c.getY() + c.getHeight() > maxY || maxY == -65536) maxY = c.getY() + c.getHeight();
		}
		
		width = maxX - minX;
		width = width < Game.getWidth() ? Game.getWidth() : width;
		height = maxY - minY;
		height = height < Game.getHeight() ? Game.getHeight() : height;
		
		bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < bi.getWidth(); i += 32)
			for (int j = 0; j < bi.getHeight(); j += 32)
				Helper.drawImage2(Game.getImage("world/ground.png"), i, j, 32, 32, 32, 0, 32, 32, (Graphics2D) bi.getGraphics());
	}
	
	public void reloadWorld() throws JSONException
	{
		int middleX = (Game.getWidth() - City.SIZE) / 2;
		int middleY = (Game.getHeight() - City.SIZE) / 2;
		
		for (int i = 0; i < citiesData.length(); i++)
		{
			JSONObject o = citiesData.getJSONObject(i);
			int x = middleX + o.getInt("X") * City.SIZE;
			int y = middleY + o.getInt("Y") * City.SIZE;
			
			boolean found = false;
			
			for (Component c : components)
			{
				if (c instanceof City && c.getX() == x && c.getY() == y)
				{
					((City) c).setName(o.getString("NAME"));
					((City) c).setLevel(o.getInt("LEVEL"));
					found = true;
					break;
				}
			}
			
			if (!found)
			{
				City c = new City(x, y, o);
				components.add(c);
			}
		}
		
		updateGround();
	}
}
