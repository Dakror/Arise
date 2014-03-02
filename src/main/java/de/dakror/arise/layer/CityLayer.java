package de.dakror.arise.layer;

import java.awt.Graphics2D;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.arise.game.City;
import de.dakror.arise.game.Game;
import de.dakror.arise.game.building.Building;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.IconButton;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class CityLayer extends Layer
{
	JSONObject data;
	City city;
	
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
		map.container = true;
		map.woodOnHover = true;
		components.add(map);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("system/city.png"), 0, 0, null);
		
		drawComponents(g);
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
}
