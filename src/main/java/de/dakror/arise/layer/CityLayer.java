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
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.building.Building;
import de.dakror.arise.game.building.Lumberjack;
import de.dakror.arise.game.building.Mine;
import de.dakror.arise.game.building.Quarry;
import de.dakror.arise.game.world.City;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.ui.BuildingButton;
import de.dakror.arise.ui.ResourceLabel;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.ui.InputField;
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
			
			final InputField name = new InputField(Game.getWidth() / 2 + 50, 18, Game.getWidth() / 2 - 120, 48);
			name.setMaxlength(50);
			name.setAllowed(name.getAllowed() + " '.#~-");
			name.setText(city.getName());
			name.drawBG = false;
			name.onEnter = new ClickEvent()
			{
				@Override
				public void trigger()
				{
					if (name.getText().trim().length() > 0)
					{
						city.setName(name.getText().trim());
						saveData();
					}
				}
			};
			components.add(name);
			
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
			
			BuildingButton mine = new BuildingButton(15 + 72, Game.getHeight() - 64, 48, 48, Game.getImage("system/icons.png").getSubimage(50, 24, 24, 24), new Mine(0, 0, 0));
			mine.addClickEvent(new ClickEvent()
			{
				@Override
				public void trigger()
				{
					activeBuilding = new Mine(0, 0, 0);
				}
			});
			components.add(mine);
			
			BuildingButton quarry = new BuildingButton(15 + 144, Game.getHeight() - 64, 48, 48, Game.getImage("system/icons.png").getSubimage(24, 24, 24, 24), new Quarry(0, 0, 0));
			quarry.addClickEvent(new ClickEvent()
			{
				@Override
				public void trigger()
				{
					activeBuilding = new Quarry(0, 0, 0);
				}
			});
			components.add(quarry);
			
			ResourceLabel wood = new ResourceLabel(20, 20, resources, Resource.WOOD);
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
			int x = Helper.round(Game.currentGame.mouse.x - activeBuilding.getWidth() / 2, Building.GRID);
			int y = Helper.round(Game.currentGame.mouse.y - activeBuilding.getHeight() / 2, Building.GRID);
			
			AffineTransform old = g.getTransform();
			AffineTransform at = g.getTransform();
			at.translate(x, y);
			g.setTransform(at);
			
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
		
		Component hovered = null;
		for (Component c : components)
		{
			c.draw(g);
			if (c.state == 2) hovered = c;
		}
		
		if (hovered != null) hovered.drawTooltip(GameFrame.currentFrame.mouse.x, GameFrame.currentFrame.mouse.y, g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
		if (activeBuilding != null) activeBuilding.setStage(1);
	}
	
	public void updateResources()
	{
		Resources products = new Resources();
		for (Component c : components)
			if (c instanceof Building) products.add(((Building) c).getProducts());
		
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
			Building b = Building.getBuildingByTypeId(Integer.parseInt(parts[2]) + (96 / Building.GRID), Integer.parseInt(parts[3]) + (96 / Building.GRID), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
			b.setStage(Integer.parseInt(parts[4]));
			b.setStageChangeSeconds(Integer.parseInt(parts[5]));
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
				int x = Helper.round(Game.currentGame.mouse.x - activeBuilding.getWidth() / 2, Building.GRID);
				int y = Helper.round(Game.currentGame.mouse.y - activeBuilding.getHeight() / 2, Building.GRID);
				
				components.add(Building.getBuildingByTypeId(x / 32, y / 32, 0, activeBuilding.getTypeId()));
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
