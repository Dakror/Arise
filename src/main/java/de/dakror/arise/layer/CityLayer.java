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
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.building.Building;
import de.dakror.arise.game.building.Lumberjack;
import de.dakror.arise.game.world.City;
import de.dakror.arise.settings.CFG;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.ui.BuildingButton;
import de.dakror.arise.ui.ResourceLabel;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.layer.Alert;
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
	public static Resources resources;
	public static boolean allBuildingsEnabled;
	JSONObject data;
	City city;
	
	Building activeBuilding;
	
	BufferedImage cache;
	
	public CityLayer(City city)
	{
		modal = true;
		this.city = city;
		try
		{
			data = new JSONObject(Helper.getURLContent(new URL("http://dakror.de/arise/city?userid=" + Game.userID + "&worldid=" + Game.worldID + "&id=" + city.getId())));
			resources = new Resources();
			resources.set(Resource.WOOD, data.getInt("WOOD"));
			resources.set(Resource.GOLD, data.getInt("GOLD"));
			resources.set(Resource.STONE, data.getInt("STONE"));
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
		try
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
			
			BuildingButton lumberjack = new BuildingButton(15, Game.getHeight() - 64, 48, 48, Game.getImage("system/icons.png").getSubimage(72, 0, 24, 24), new Lumberjack(0, 0, 0));
			lumberjack.addClickEvent(new ClickEvent()
			{
				@Override
				public void trigger()
				{
					activeBuilding = new Lumberjack(0, 0, 0);
				}
			});
			components.add(lumberjack);
			
			ResourceLabel wood = new ResourceLabel(70, 20, resources, Resource.WOOD);
			components.add(wood);
			
			ResourceLabel stone = new ResourceLabel(190 + wood.getX(), 20, resources, Resource.STONE);
			components.add(stone);
			
			ResourceLabel gold = new ResourceLabel(400 + wood.getX(), 20, resources, Resource.GOLD);
			components.add(gold);
			
			ResourceLabel buildings = new ResourceLabel(70 + wood.getX(), 60, resources, Resource.BUILDINGS);
			buildings.off = (data.getInt("LEVEL") + 1) * City.BUILDINGS_SCALE;
			components.add(buildings);
			
			ResourceLabel people = new ResourceLabel(270 + wood.getX(), 60, resources, Resource.PEOPLE);
			people.off = 20;
			components.add(people);
			
			updateBuildingbar();
			
			cache = new BufferedImage(Game.getWidth(), Game.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) cache.getGraphics();
			Helper.setRenderingHints(g, true);
			g.drawImage(Game.getImage("system/city.png"), 0, 0, null);
			Helper.drawShadow(0, 5, Game.getWidth(), 96, g);
			Helper.drawOutline(0, 5, Game.getWidth(), 96, false, g);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(cache, 0, 0, null);
		
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
	{
		updateComponents(tick);
	}
	
	public void updateResources()
	{
		Resources products = new Resources();
		for (Component c : components)
			if (c instanceof Building) products.add(((Building) c).getProducts());
		
		products = Resources.mul(products, Game.world.getSpeed());
		
		for (Resource r : products.getFilled())
		{
			int interval = 60 / products.get(r);
			int perminute = interval < 1 ? interval : 1;
			CFG.p(interval, perminute);
			if (Game.minuteInHour % (interval < 1 ? 1 : interval) == 0) resources.add(r, perminute);
		}
		
		saveData();
	}
	
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
			resources.add(Resource.BUILDINGS, 1);
		}
	}
	
	public void saveData()
	{
		String data = "";
		for (Component c : components)
			if (c instanceof Building) data += ((Building) c).getData() + ";";
		
		try
		{
			if (!Helper.getURLContent(new URL("http://dakror.de/arise/city?keepAlive=true&userid=" + Game.userID + "&worldid=" + Game.worldID + "&id=" + city.getId() + "&data=" + data + "&wood=" + resources.get(Resource.WOOD) + "&stone=" + resources.get(Resource.STONE) + "&gold=" + resources.get(Resource.GOLD))).contains("true"))
			{
				Game.currentGame.addLayer(new Alert("Fehler! Deine Stadt konnte nicht mit dem Server synchronisiert werden. Möglicherweise ist dieser im Moment down.", null));
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void updateBuildingbar()
	{
		try
		{
			Resources products = new Resources();
			for (Component c : components)
			{
				if (c instanceof IconButton && c.getY() == Game.getHeight() - 64) c.enabled = resources.get(Resource.BUILDINGS) < (data.getInt("LEVEL") + 1) * City.BUILDINGS_SCALE;
				if (c instanceof Building) products.add(((Building) c).getProducts());
			}
			
			products = Resources.mul(products, Game.world.getSpeed());
			
			for (Component c : components)
				if (c instanceof ResourceLabel) ((ResourceLabel) c).perHour = products.get(((ResourceLabel) c).getResource());
			
			allBuildingsEnabled = resources.get(Resource.BUILDINGS) < (data.getInt("LEVEL") + 1) * City.BUILDINGS_SCALE;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
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
				resources.add(Resource.BUILDINGS, 1);
				resources.add(Resources.mul(activeBuilding.getBuildingCosts(), -1));
				activeBuilding = null;
				updateBuildingbar();
				saveData();
			}
			if (e.getButton() == MouseEvent.BUTTON3)
			{
				activeBuilding = null;
				Game.applet.setCursor(Cursor.getDefaultCursor());
			}
		}
	}
}
