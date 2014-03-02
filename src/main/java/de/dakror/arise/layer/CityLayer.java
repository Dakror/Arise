package de.dakror.arise.layer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.arise.game.City;
import de.dakror.arise.game.Game;
import de.dakror.arise.game.building.Building;
import de.dakror.arise.game.building.Lumberjack;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.ui.button.IconButton;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class CityLayer extends Layer
{
	JSONObject data;
	City city;
	
	Building activeBuilding;
	
	public CityLayer(City city)
	{
		modal = true;
		this.city = city;
		try
		{
			data = new JSONObject(Helper.getURLContent(new URL("http://dakror.de/arise/city?userid=" + Game.userID + "&worldid=" + Game.worldID + "&id=" + city.getId())));
			placeBuildings();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void init()
	{
		IconButton map = new IconButton((Game.getWidth() - 64) / 2, 20, 64, 64, "system/map.png");
		map.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentGame.removeLayer(CityLayer.this);
				Game.world.updateWorld();
			}
		});
		map.tooltip = "Weltkarte";
		map.mode2 = true;
		components.add(map);
		
		IconButton lumberjack = new IconButton(15, Game.getHeight() - 64, 48, 48, Game.getImage("system/icons.png").getSubimage(72, 0, 24, 24));
		lumberjack.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				activeBuilding = new Lumberjack(0, 0, 0);
			}
		});
		lumberjack.mode1 = true;
		lumberjack.tooltip = "Holzfäller";
		lumberjack.tooltipOnBottom = true;
		components.add(lumberjack);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("system/city.png"), 0, 0, null);
		
		if (activeBuilding != null)
		{
			int x = Helper.round(Game.currentGame.mouse.x - activeBuilding.getWidth() / 2, 32);
			int y = Helper.round(Game.currentGame.mouse.y - activeBuilding.getHeight() / 2, 32);
			
			Composite c = g.getComposite();
			Color cl = g.getColor();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
			
			boolean free = true;
			
			for (int i = 0; i < activeBuilding.getWidth(); i += 32)
			{
				for (int j = 0; j < activeBuilding.getHeight(); j += 32)
				{
					boolean green = new Rectangle(96, 96, 1088, 544).contains(new Rectangle(i + x, j + y, 32, 32)) && !intersectsBuildings(new Rectangle(i + x, j + y, 32, 32));
					g.setColor(green ? Color.decode("#5fff5b") : Color.red);
					g.fillRect(i + x, j + y, 32, 32);
					
					if (!green) free = false;
				}
			}
			
			if (free) Game.applet.setCursor(Cursor.getDefaultCursor());
			else Game.applet.setCursor(DragSource.DefaultMoveNoDrop);
			
			g.setColor(Color.black);
			for (int i = 0; i < 1088; i += 32)
				for (int j = 0; j < 544; j += 32)
					g.drawRect(i + 96, j + 96, 32, 32);
			
			g.setComposite(c);
			g.setColor(cl);
		}
		
		Component hovered = null;
		for (Component c : components)
		{
			c.draw(g);
			if (c.state == 2) hovered = c;
		}
		
		if (activeBuilding != null)
		{
			int x = Helper.round(Game.currentGame.mouse.x - activeBuilding.getWidth() / 2, 32);
			int y = Helper.round(Game.currentGame.mouse.y - activeBuilding.getHeight() / 2, 32);
			
			AffineTransform old = g.getTransform();
			AffineTransform at = g.getTransform();
			at.translate(x, y);
			g.setTransform(at);
			
			activeBuilding.draw(g);
			
			g.setTransform(old);
		}
		
		if (hovered != null) hovered.drawTooltip(GameFrame.currentFrame.mouse.x, GameFrame.currentFrame.mouse.y, g);
	}
	
	@Override
	public void update(int tick)
	{}
	
	public void placeBuildings() throws JSONException
	{
		String[] buildings = data.getString("DATA").split(";");
		for (int i = 0; i < buildings.length; i++)
		{
			String building = buildings[i];
			if (!building.contains(":")) continue;
			
			String[] parts = building.split(":");
			Building b = Building.getBuildingByTypeId(Integer.parseInt(parts[2]) + 3, Integer.parseInt(parts[3]) + 3, Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
			components.add(b);
		}
	}
	
	public boolean intersectsBuildings(Rectangle r)
	{
		for (Component c : components)
			if (c instanceof Building && new Rectangle(c.getX(), c.getY(), c.getWidth(), c.getHeight()).intersects(r)) return true;
		
		return false;
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		
		if (activeBuilding != null)
		{
			if (e.getButton() == MouseEvent.BUTTON1 && Game.applet.getCursor().equals(Cursor.getDefaultCursor()))
			{
				int x = Helper.round(Game.currentGame.mouse.x - activeBuilding.getWidth() / 2, 32);
				int y = Helper.round(Game.currentGame.mouse.y - activeBuilding.getHeight() / 2, 32);
				
				activeBuilding.x = x;
				activeBuilding.y = y;
				components.add(activeBuilding);
				activeBuilding = null;
			}
			if (e.getButton() == MouseEvent.BUTTON3)
			{
				activeBuilding = null;
				Game.applet.setCursor(Cursor.getDefaultCursor());
			}
		}
	}
}
