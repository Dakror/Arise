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
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.building.Building;
import de.dakror.arise.game.world.City;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class CityLayer extends Layer
{
	public static Resources resources;
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
			resources = new Resources();
			resources.set(Resource.WOOD, data.getInt("WOOD"));
			resources.set(Resource.GOLD, data.getInt("GOLD"));
			resources.set(Resource.STONE, data.getInt("STONE"));
			placeBuildings();
			updateBuildingStages();
			saveData();
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
		g.drawImage(Game.getImage("system/city.png"), 0, 0, null);
		
		Component hovered = null;
		for (Component c : components)
		{
			c.draw(g);
			if (c.state == 2) hovered = c;
		}
		
		if (activeBuilding != null)
		{
			int x = Helper.round(Game.currentGame.mouse.x - activeBuilding.getWidth() / 2, Building.GRID);
			int y = Helper.round(Game.currentGame.mouse.y - activeBuilding.getHeight() / 2, Building.GRID);
			
			AffineTransform old = g.getTransform();
			AffineTransform at = g.getTransform();
			at.translate(x, y);
			g.setTransform(at);
			
			activeBuilding.setStage(1);
			activeBuilding.draw(g);
			
			g.setTransform(old);
			
			Composite c = g.getComposite();
			Color cl = g.getColor();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
			
			boolean free = true;
			
			for (int i = activeBuilding.bx * Building.GRID; i < activeBuilding.bx * Building.GRID + activeBuilding.bw * Building.GRID; i += Building.GRID)
			{
				for (int j = activeBuilding.by * Building.GRID; j < activeBuilding.by * Building.GRID + activeBuilding.bh * Building.GRID; j += Building.GRID)
				{
					boolean green = new Rectangle(96, 96, 1088, 544).contains(new Rectangle(i + x, j + y, Building.GRID, Building.GRID)) && !intersectsBuildings(new Rectangle(i + x, j + y, Building.GRID, Building.GRID));
					g.setColor(green ? Color.decode("#5fff5b") : Color.red);
					g.fillRect(i + x, j + y, Building.GRID, Building.GRID);
					
					if (!green) free = false;
				}
			}
			
			if (free) Game.applet.setCursor(Cursor.getDefaultCursor());
			else Game.applet.setCursor(DragSource.DefaultMoveNoDrop);
			
			g.setColor(Color.black);
			for (int i = 0; i < 1088; i += Building.GRID)
				for (int j = 0; j < 544; j += Building.GRID)
					g.drawRect(i + 96, j + 96, Building.GRID, Building.GRID);
			
			g.setComposite(c);
			g.setColor(cl);
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
			if (c instanceof Building && ((Building) c).getStage() == 1) products.add(((Building) c).getProducts());
		
		products = Resources.mul(products, Game.world.getSpeed());
		
		for (Resource r : products.getFilled())
		{
			float interval = 3600f / products.get(r);
			float persecond = products.get(r) / 3600f;
			
			if (interval >= 1 && Game.secondInMinute % interval == 0)
			{
				resources.add(r, 1);
			}
			else if (persecond > 1)
			{
				int powerOfTen = (int) Math.pow(10, ((int) persecond + "").length());
				int intervalForPowerOfTen = (int) (powerOfTen / persecond);
				if (Game.secondInMinute % intervalForPowerOfTen == 0) resources.add(r, powerOfTen);
			}
		}
	}
	
	public void updateBuildingStages()
	{
		for (Component c : components)
		{
			if (c instanceof Building && ((Building) c).isStageChangeReady())
			{
				((Building) c).setStageChangeTimestamp(0);
				if (((Building) c).getStage() == 0) ((Building) c).setStage(1);
				else components.remove(c);
			}
		}
	}
	
	public void placeBuildings() throws JSONException
	{
		String[] buildings = data.getString("DATA").split(";");
		for (int i = 0; i < buildings.length; i++)
		{
			String building = buildings[i];
			if (!building.contains(":")) continue;
			
			String[] parts = building.split(":");
			Building b = Building.getBuildingByTypeId(Integer.parseInt(parts[2]) + (96 / Building.GRID), Integer.parseInt(parts[3]) + (96 / Building.GRID), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
			b.setStage(Integer.parseInt(parts[4]));
			b.setStageChangeTimestamp(Long.parseLong(parts[5]));
			
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
			String url = "http://dakror.de/arise/city?keepAlive=true";
			url += "&userid=" + Game.userID;
			url += "&worldid=" + Game.worldID;
			url += "&id=" + city.getId();
			url += "&data=" + data;
			url += "&wood=" + resources.get(Resource.WOOD);
			url += "&stone=" + resources.get(Resource.STONE);
			url += "&gold=" + resources.get(Resource.GOLD);
			url += "&name=" + URLEncoder.encode(city.getName(), "UTF-8");
			if (!Helper.getURLContent(new URL(url)).contains("true"))
			{
				Game.currentGame.addLayer(new Alert("Fehler! Deine Stadt konnte nicht mit dem Server synchronisiert werden. Möglicherweise ist dieser im Moment down.", null));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Game.currentGame.addLayer(new Alert("Fehler! Deine Stadt konnte nicht mit dem Server synchronisiert werden. Möglicherweise bist du nicht mit dem Internet verbunden.", null));
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
		
		boolean anyBuildingActive = false;
		for (Component c : components)
		{
			if (c instanceof Building && c.state == 1)
			{
				anyBuildingActive = true;
				break;
			}
		}
		
		if (!anyBuildingActive && !((CityHUDLayer) Game.currentGame.getActiveLayer()).anyComponentClicked) CityHUDLayer.selectedBuilding = null;
		
		if (activeBuilding != null)
		{
			if (e.getButton() == MouseEvent.BUTTON1 && Game.applet.getCursor().equals(Cursor.getDefaultCursor()))
			{
				int x = Helper.round(Game.currentGame.mouse.x - activeBuilding.getWidth() / 2, Building.GRID);
				int y = Helper.round(Game.currentGame.mouse.y - activeBuilding.getHeight() / 2, Building.GRID);
				
				Building b = Building.getBuildingByTypeId(x / 32, y / 32, 0, activeBuilding.getTypeId());
				b.setStageChangeTimestamp(System.currentTimeMillis() / 1000);
				components.add(b);
				resources.add(Resource.BUILDINGS, 1);
				resources.add(Resources.mul(activeBuilding.getBuildingCosts(), -1));
				activeBuilding = null;
				((CityHUDLayer) Game.currentGame.getActiveLayer()).updateBuildingbar();
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
