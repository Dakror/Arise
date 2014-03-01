package de.dakror.arise.layer;

import java.awt.Graphics2D;
import java.net.URL;

import org.json.JSONObject;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.world.City;
import de.dakror.gamesetup.layer.Layer;
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("system/city.png"), 0, 0, null);
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void init()
	{}
}
