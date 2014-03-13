package de.dakror.arise.game.world;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dakror.arise.game.Game;
import de.dakror.arisewebsite.layer.CityLayer;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class World extends Layer
{
	public static int CHUNKSIZE = 256;
	
	String name;
	
	int speed, id, width, height, tick, minX, minY;
	public int x, y, cities, citiesDrawn;
	
	long lastCheck;
	
	BufferedImage chunk;
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
			
			minX = minY = 0;
			
			chunk = new BufferedImage(CHUNKSIZE, CHUNKSIZE, BufferedImage.TYPE_INT_ARGB);
			for (int i = 0; i < chunk.getWidth(); i += 32)
				for (int j = 0; j < chunk.getHeight(); j += 32)
					Helper.drawImage2(Game.getImage("world/ground.png"), i, j, 32, 32, 32, 0, 32, 32, (Graphics2D) chunk.getGraphics());
			
			updateSize();
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
		if (Game.currentGame.getActiveLayer() instanceof CityLayer)
		{
			citiesDrawn = 0;
			return;
		}
		
		AffineTransform old = g.getTransform();
		AffineTransform at = g.getTransform();
		at.translate(x, y);
		g.setTransform(at);
		for (int i = 0; i < Math.ceil(width / (float) CHUNKSIZE); i++)
			for (int j = 0; j < Math.ceil(height / (float) CHUNKSIZE); j++)
				if (new Rectangle(0, 0, Game.getWidth(), Game.getHeight()).intersects(new Rectangle(minX + i * CHUNKSIZE + x, minY + j * CHUNKSIZE + y, CHUNKSIZE, CHUNKSIZE))) g.drawImage(chunk, minX + i * CHUNKSIZE, minY + j * CHUNKSIZE, null);
		
		int citiesDrawn = 0;
		
		Component hovered = null;
		
		for (Component c : components)
		{
			if (!new Rectangle(0, 0, Game.getWidth(), Game.getHeight()).intersects(new Rectangle(c.getX() + x, c.getY() + y, c.getWidth(), c.getHeight()))) continue;
			c.draw(g);
			if (c.state == 2) hovered = c;
			citiesDrawn++;
		}
		
		if (hovered != null) hovered.drawTooltip(GameFrame.currentFrame.mouse.x, GameFrame.currentFrame.mouse.y, g);
		
		this.citiesDrawn = citiesDrawn;
		
		g.setTransform(old);
	}
	
	@Override
	public void update(int tick)
	{
		this.tick = tick;
		if (lastCheck == 0 || tick - lastCheck > 1800 && Game.currentGame.getActiveLayer() instanceof World) updateWorld(); // check once 30 secs
		
		updateComponents(tick);
	}
	
	public void updateSize()
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
		
		this.minX = minX;
		this.minY = minY;
		
		width = maxX - minX;
		width = width < Game.getWidth() ? Game.getWidth() : width;
		height = maxY - minY;
		height = height < Game.getHeight() ? Game.getHeight() : height;
		
		x = x < -(width - Game.getWidth() + minX) ? -(width - Game.getWidth() + minX) : x;
		y = y < -(height - Game.getHeight() + minY) ? -(height - Game.getHeight() + minY) : y;
		x = x > -minX ? -minX : x;
		y = y > -minY ? -minY : y;
	}
	
	public void updateWorld()
	{
		try
		{
			JSONArray newData = new JSONArray(Helper.getURLContent(new URL("http://dakror.de/arise/world?cities=true&id=" + id)));
			if (citiesData == null || !citiesData.equals(newData))
			{
				citiesData = newData;
				cities = citiesData.length();
				
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
				
				updateSize();
			}
			
			lastCheck = tick;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
		x = x < -(width - Game.getWidth() + minX) ? -(width - Game.getWidth() + minX) : x;
		y = y < -(height - Game.getHeight() + minY) ? -(height - Game.getHeight() + minY) : y;
		this.x = x > -minX ? -minX : x;
		this.y = y > -minY ? -minY : y;
		
		e.translatePoint(-x, -y);
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
	
	public int getSpeed()
	{
		return speed;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
}
