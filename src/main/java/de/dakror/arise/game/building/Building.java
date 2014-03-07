package de.dakror.arise.game.building;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.json.JSONException;

import de.dakror.arise.game.Game;
import de.dakror.arise.layer.CityHUDLayer;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.util.Assistant;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public abstract class Building extends ClickableComponent
{
	public static int GRID = 32;
	public static float DECONSTRUCT_FACTOR;
	public static float UPGRADE_FACTOR;
	public static int MAX_LEVEL;
	
	protected int tx, ty, tw, th, typeId, level, minCityLevel;
	public int bx, by, bw, bh;
	/**
	 * 0 = construction<br>
	 * 1 = built<br>
	 * 2 = deconstruction<br>
	 * 3 = upgrading<br>
	 */
	protected int stage, prevStage;
	protected int stageChangeSeconds;
	protected long stageChangeTimestamp;
	protected String name, desc;
	protected Resources buildingCosts, products, scale;
	
	public Building(int x, int y, int width, int height, int level)
	{
		super(x * GRID, y * GRID, width * GRID, height * GRID);
		
		this.level = level;
		buildingCosts = new Resources();
		products = new Resources();
		scale = new Resources();
		bx = by = 0;
		bw = width;
		bh = height;
		
		addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				if (Game.currentGame.getActiveLayer() instanceof CityHUDLayer)
				{
					if (((CityHUDLayer) Game.currentGame.getActiveLayer()).first)
					{
						((CityHUDLayer) Game.currentGame.getActiveLayer()).first = false;
						return;
					}
					
					CityHUDLayer.selectedBuilding = Building.this;
				}
			}
		});
	}
	
	public void init()
	{
		try
		{
			if (Game.buildingsConfig.getJSONObject("buildings").has("" + typeId))
			{
				buildingCosts = new Resources(Game.buildingsConfig.getJSONObject("buildings").getJSONObject(typeId + "").getJSONObject("costs"));
				products = new Resources(Game.buildingsConfig.getJSONObject("buildings").getJSONObject(typeId + "").getJSONObject("products"));
				scale = new Resources(Game.buildingsConfig.getJSONObject("buildings").getJSONObject(typeId + "").getJSONObject("scale"));
				stageChangeSeconds = Game.buildingsConfig.getJSONObject("buildings").getJSONObject(typeId + "").getInt("stage");
				minCityLevel = Game.buildingsConfig.getJSONObject("buildings").getJSONObject(typeId + "").has("mincitylevel") ? Game.buildingsConfig.getJSONObject("buildings").getJSONObject(typeId + "").getInt("mincitylevel") : 0;
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (Game.world == null) return;
		
		if (state != 0 || (CityHUDLayer.selectedBuilding != null && CityHUDLayer.selectedBuilding.equals(this)))
		{
			Color c = g.getColor();
			g.setColor(Color.black);
			g.drawRect(x + bx * GRID - 1, y + by * GRID - 1, bw * GRID + 2, bh * GRID + 2);
			g.setColor(c);
		}
		
		if (stage > 0) drawStage1(g);
		
		if (stage != 1 && stageChangeTimestamp > 0)
		{
			int tx = x + bx * GRID, ty = y + by * GRID, width = 128;
			
			if (stage == 0) Assistant.drawBuildingStage(tx, ty, this, g);
			
			float duration = Math.round(stageChangeSeconds * (stage == 0 ? 1f : DECONSTRUCT_FACTOR) / Game.world.getSpeed());
			long destTimeStamp = stageChangeTimestamp + (long) duration;
			long deltaEnd = (destTimeStamp - System.currentTimeMillis() / 1000);
			long deltaStart = (System.currentTimeMillis() / 1000 - stageChangeTimestamp);
			
			if (deltaEnd >= 0)
			{
				Helper.drawProgressBar(tx + (bw * GRID - width) / 2, ty + (bh * GRID - 22) / 2, width, deltaStart / duration, "ffc744", g);
				
				Color c = g.getColor();
				g.setColor(Color.black);
				Helper.drawHorizontallyCenteredString(Assistant.formatSeconds(deltaEnd), tx + (bw * GRID - width) / 2, width, ty + (bh * GRID) / 2 + 6, g, 20);
				g.setColor(c);
			}
		}
	}
	
	protected void drawStage1(Graphics2D g)
	{
		Helper.setRenderingHints(g, false);
		Helper.drawImage2(Game.getImage("world/structs.png"), x, y, width, height, tx * 32, ty * 32, tw * 32, th * 32, g);
		Helper.setRenderingHints(g, true);
	}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g)
	{
		if (Game.currentGame.getActiveLayer() instanceof CityHUDLayer)
		{
			String string = "Lvl. " + (level + 1) + " " + name + (stage == 2 ? " (Abriss)" : (stage == 3 ? " (Ausbau)" : (stage == 0 ? " (Bau)" : "")));
			
			Helper.drawShadow(x, y, g.getFontMetrics(g.getFont().deriveFont(30f)).stringWidth(string) + 30, 64, g);
			Helper.drawString(string, x + 15, y + 40, g, 30);
		}
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
	
	public int getStage()
	{
		return stage;
	}
	
	public void setStage(int s)
	{
		prevStage = stage;
		stage = s;
	}
	
	public int getStageChangeSeconds()
	{
		return stageChangeSeconds;
	}
	
	public long getStageChangeTimestamp()
	{
		return stageChangeTimestamp;
	}
	
	/**
	 * @param s - time in seconds
	 */
	public void setStageChangeTimestamp(long s)
	{
		stageChangeTimestamp = s;
	}
	
	public boolean isStageChangeReady()
	{
		if (stageChangeTimestamp == 0) return false;
		
		return System.currentTimeMillis() / 1000 - stageChangeTimestamp >= stageChangeSeconds * (stage == 0 ? 1 : DECONSTRUCT_FACTOR) / Game.world.getSpeed();
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
		return typeId + ":" + level + ":" + ((x - 96) / GRID) + ":" + ((y - 96) / GRID) + ":" + stage + ":" + stageChangeTimestamp;
	}
	
	public Resources getBuildingCosts()
	{
		return buildingCosts;
	}
	
	public Resources getUpgradeCosts()
	{
		Resources res = new Resources();
		res.add(buildingCosts);
		
		for (Resource r : res.getFilled())
			res.set(r, (int) Math.round(res.get(r) * Math.pow(UPGRADE_FACTOR, level + 1)));
		
		return res;
	}
	
	public Resources getProducts()
	{
		return products;
	}
	
	public int getMinCityLevel()
	{
		return minCityLevel;
	}
	
	public Resources getScalingProducts()
	{
		Resources p = new Resources();
		p.add(products);
		if (level > 0) p.add(Resources.mul(scale, level));
		
		return p;
	}
	
	public Resources getScale()
	{
		return scale;
	}
	
	@Override
	public boolean contains(int x, int y)
	{
		return new Rectangle(this.x + bx * GRID, this.y + by * GRID, bw * GRID, bh * GRID).contains(x, y);
	}
	
	public void levelUp()
	{
		level++;
		stage = 1;
	}
	
	public static Building getBuildingByTypeId(int x, int y, int level, int typeId)
	{
		switch (typeId)
		{
			case 1:
				return new Center(x, y, level);
			case 2:
				return new Lumberjack(x, y, level);
			case 3:
				return new Mine(x, y, level);
			case 4:
				return new Quarry(x, y, level);
			case 5:
				return new Barracks(x, y, level);
			case 6:
				return new Barn(x, y, level);
			default:
				return null;
		}
	}
}
