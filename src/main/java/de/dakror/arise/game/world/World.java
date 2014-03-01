package de.dakror.arise.game.world;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
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
	
	int speed, id, width, height, x, y;
	long lastCheck;
	
	BufferedImage bi;
	JSONArray citiesData;
	
	Point dragStart, worldDragStart;
	
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
		AffineTransform old = g.getTransform();
		AffineTransform at = g.getTransform();
		at.translate(x, y);
		g.setTransform(at);
		g.drawImage(bi, 0, 0, null);
		
		drawComponents(g);
		g.setTransform(old);
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
					updateWorld();
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
		
		minX = minX > 0 ? 0 : minX;
		minY = minY > 0 ? 0 : minY;
		width = maxX - minX;
		width = width < Game.getWidth() ? Game.getWidth() : width;
		height = maxY - minY;
		height = height < Game.getHeight() ? Game.getHeight() : height;
		
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < bi.getWidth(); i += 32)
			for (int j = 0; j < bi.getHeight(); j += 32)
				Helper.drawImage2(Game.getImage("world/ground.png"), i, j, 32, 32, 32, 0, 32, 32, (Graphics2D) bi.getGraphics());
		
		this.bi = bi;
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		
		if (dragStart == null)
		{
			dragStart = e.getPoint();
			worldDragStart = new Point(x, y);
		}
		
		int x = worldDragStart.x + e.getX() - dragStart.x;
		int y = worldDragStart.y + e.getY() - dragStart.y;
		
		x = x + width < Game.getWidth() ? Game.getWidth() - width : x;
		y = y + height < Game.getHeight() ? Game.getHeight() - height : y;
		this.x = x > 0 ? 0 : x;
		this.y = y > 0 ? 0 : y;
		
		e.translatePoint(-this.x, -this.y);
		super.mouseDragged(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		e.translatePoint(-x, -y);
		super.mousePressed(e);
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		e.translatePoint(-x, -y);
		super.mouseMoved(e);
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		worldDragStart = dragStart = null;
		
		e.translatePoint(-x, -y);
		super.mouseReleased(e);
	}
	
	public void updateWorld() throws JSONException
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
