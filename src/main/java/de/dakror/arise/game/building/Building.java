package de.dakror.arise.game.building;

import java.awt.Color;
import java.awt.Graphics2D;

import org.json.JSONException;

import de.dakror.arise.game.Game;
import de.dakror.arise.settings.Resources;
import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public abstract class Building extends ClickableComponent
{
	public static int GRID = 32;
	
	protected int tx, ty, tw, th;
	protected int typeId;
	protected int level;
	protected String name, desc;
	protected Resources buildingCosts, products;
	
	public Building(int x, int y, int width, int height, int level)
	{
		super(x * GRID, y * GRID, width * GRID, height * GRID);
		
		this.level = level;
		buildingCosts = new Resources();
		products = new Resources();
	}
	
	public void init()
	{
		if (Game.buildingsConfig.has("" + typeId))
		{
			try
			{
				buildingCosts = new Resources(Game.buildingsConfig.getJSONObject(typeId + "").getJSONObject("costs"));
				products = new Resources(Game.buildingsConfig.getJSONObject(typeId + "").getJSONObject("products"));
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (state != 0)
		{
			Color c = g.getColor();
			g.setColor(Color.black);
			g.drawRect(x, y, width, height);
			g.setColor(c);
		}
		
		Helper.setRenderingHints(g, false);
		Helper.drawImage2(Game.getImage("world/structs.png"), x, y, width, height, tx * 32, ty * 32, tw * 32, th * 32, g);
		Helper.setRenderingHints(g, true);
	}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g)
	{
		Helper.drawShadow(x, y, g.getFontMetrics(g.getFont().deriveFont(30f)).stringWidth(name) + 30, 64, g);
		Helper.drawString(name, x + 15, y + 40, g, 30);
	}
	
	@Override
	public void update(int tick)
	{}
	
	public int getTypeId()
	{
		return typeId;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return desc;
	}
	
	public String getData()
	{
		return typeId + ":" + level + ":" + ((x - 96) / GRID) + ":" + ((y - 96) / GRID);
	}
	
	public Resources getBuildingCosts()
	{
		return buildingCosts;
	}
	
	public Resources getProducts()
	{
		return products;
	}
	
	public static Building getBuildingByTypeId(int x, int y, int level, int typeId)
	{
		switch (typeId)
		{
			case 1:
				return new Centre(x, y, level);
			case 2:
				return new Lumberjack(x, y, level);
			case 3:
				return new Mine(x, y, level);
			case 4:
				return new Quarry(x, y, level);
			default:
				return null;
		}
	}
}
