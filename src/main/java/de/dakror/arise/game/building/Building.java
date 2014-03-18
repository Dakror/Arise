package de.dakror.arise.game.building;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.arise.game.Game;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.ui.BuildButton;
import de.dakror.arise.util.Assistant;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.gamesetup.ui.Container;
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
	public static JSONObject TROOPS;
	public static int MAX_QUEUE;
	
	protected int tx, ty, tw, th, typeId, level, maxLevel, levelFac, minCityLevel;
	public int bx, by, bw, bh;
	/**
	 * 0 = construction<br>
	 * 1 = built<br>
	 * 2 = deconstruction<br>
	 * 3 = upgrading<br>
	 */
	protected int stage, prevStage;
	protected int stageChangeDuration;
	protected int stageChangeSecondsLeft;
	protected String name, desc;
	protected String metadata;
	protected Resources buildingCosts, products, scale;
	protected Container guiContainer;
	
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
		metadata = "";
		guiContainer = new Container.DefaultContainer();
		
		addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				// CityHUDLayer.selectedBuilding = Building.this;
				//
				// float duration = stageChangeDuration * Building.DECONSTRUCT_FACTOR / Game.world.getSpeed();
				//
				// CityHUDLayer.upgrade.setUpgradeMode("Ausbau", "Die Stufe des Gebäudes wird erhöht. \nDauer: " + Assistant.formatSeconds((long) duration), getUpgradeCosts(), 0);
				// CityHUDLayer.upgrade.setProducts(products);
				// CityHUDLayer.upgrade.setScale(scale);
				// CityHUDLayer.upgrade.setLevel(Building.this.level);
				// CityHUDLayer.upgrade.setMaxLevel(maxLevel);
				//
				// CityHUDLayer.deconstruct.setUpgradeMode("Abriss", "Das Gebäudes wird abgerissen. Es werden keine Resourcen zurückgegeben.", new Resources(), 0);
			}
		});
	}
	
	public void init()
	{
		try
		{
			if (Game.config.getJSONObject("buildings").has("" + typeId))
			{
				buildingCosts = new Resources(Game.config.getJSONObject("buildings").getJSONObject(typeId + "").getJSONObject("costs"));
				products = new Resources(Game.config.getJSONObject("buildings").getJSONObject(typeId + "").getJSONObject("products"));
				scale = new Resources(Game.config.getJSONObject("buildings").getJSONObject(typeId + "").getJSONObject("scale"));
				stageChangeDuration = Game.config.getJSONObject("buildings").getJSONObject(typeId + "").getInt("stage");
				minCityLevel = Game.config.getJSONObject("buildings").getJSONObject(typeId + "").has("mincitylevel") ? Game.config.getJSONObject("buildings").getJSONObject(typeId + "").getInt("mincitylevel") : 0;
				maxLevel = Game.config.getJSONObject("buildings").getJSONObject(typeId + "").has("maxlevel") ? Game.config.getJSONObject("buildings").getJSONObject(typeId + "").getInt("maxlevel") : MAX_LEVEL;
				levelFac = Game.config.getJSONObject("buildings").getJSONObject(typeId + "").has("levelfac") ? Game.config.getJSONObject("buildings").getJSONObject(typeId + "").getInt("levelfac") : MAX_LEVEL;
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
		
		// if (state != 0 || (CityHUDLayer.selectedBuilding != null && CityHUDLayer.selectedBuilding.equals(this)))
		// {
		// Color c = g.getColor();
		// g.setColor(Color.black);
		// g.drawRect(x + bx * GRID - 1, y + by * GRID - 1, bw * GRID + 2, bh * GRID + 2);
		// g.setColor(c);
		// }
		
		if (stage > 0) drawStage1(g);
		
		if (stageChangeSecondsLeft > 0)
		{
			int tx = x + bx * GRID, ty = y + by * GRID, width = 128;
			
			if (stage == 0) Assistant.drawBuildingStage(tx, ty, this, g);
			
			float duration = getStageChangeDuration();
			long destTimeStamp = stageChangeSecondsLeft + (long) duration;
			long deltaEnd = (destTimeStamp - System.currentTimeMillis() / 1000);
			long deltaStart = (System.currentTimeMillis() / 1000 - stageChangeSecondsLeft);
			
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
		// if (Game.currentGame.getActiveLayer() instanceof CityHUDLayer)
		// {
		// String string = getTooltipText();
		//
		// int width = g.getFontMetrics(g.getFont().deriveFont(30f)).stringWidth(string) + 30;
		// int height = 64;
		// int x1 = x;
		// int y1 = y;
		//
		// if (x1 + width > GameFrame.getWidth()) x1 -= (x1 + width) - GameFrame.getWidth();
		// if (y1 + height > GameFrame.getHeight()) y1 -= (y1 + height) - GameFrame.getHeight();
		//
		// Helper.drawShadow(x1, y1, width, height, g);
		// Helper.drawString(string, x1 + 15, y1 + 40, g, 30);
		// }
	}
	
	@Override
	public void update(int tick)
	{}
	
	public String getTooltipText()
	{
		return "Lvl. " + (level + 1) + " " + name + (stage == 2 ? " (Abriss)" : (stage == 3 ? " (Ausbau)" : (stage == 0 ? " (Bau)" : "")));
	}
	
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
		return stageChangeDuration;
	}
	
	public long getStageChangeSecondsLeft()
	{
		return stageChangeSecondsLeft;
	}
	
	/**
	 * @param s - time in seconds
	 */
	public void setStageChangeSecondsLeft(int s)
	{
		stageChangeSecondsLeft = s;
	}
	
	protected float getStageChangeDuration()
	{
		return Math.round(stageChangeDuration * (stage == 0 ? 1f : DECONSTRUCT_FACTOR) / Game.world.getSpeed());
	}
	
	public boolean isStageChangeReady()
	{
		if (stageChangeSecondsLeft == 0) return false;
		
		return System.currentTimeMillis() / 1000 - stageChangeSecondsLeft >= getStageChangeDuration();
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
		return typeId + ":" + level + ":" + ((x - 96) / GRID) + ":" + ((y - 96) / GRID) + ":" + stage + ":" + stageChangeSecondsLeft + (metadata.length() > 0 ? ":" + metadata : "");
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
			res.set(r, (int) Math.round((MAX_LEVEL / (float) levelFac) * res.get(r) * Math.pow(UPGRADE_FACTOR, level + 1)));
		
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
	
	public Container getGuiContainer()
	{
		return guiContainer;
	}
	
	public int getMaxLevel()
	{
		return maxLevel;
	}
	
	public void setMetadata(String s)
	{
		metadata = s;
	}
	
	protected void addGuiButton(int x, int y, Image icon, String tooltip, String desc, Resources buildingCosts, int minCityLevel, ClickEvent action)
	{
		BuildButton b = new BuildButton(x * 56 + Game.getWidth() - 270, y * 56 + Game.getHeight() - 170, 32, 32, icon);
		b.addClickEvent(action);
		b.mode1 = true;
		b.setUpgradeMode(tooltip, desc, buildingCosts, minCityLevel);
		
		guiContainer.components.add(b);
	}
	
	protected void addGuiButton(int x, int y, Point icon, String tooltip, String desc, Resources buildingCosts, int minCityLevel, ClickEvent action)
	{
		addGuiButton(x, y, Game.getImage("system/icons.png").getSubimage(icon.x * 24, icon.y * 24, 24, 24), tooltip, desc, buildingCosts, minCityLevel, action);
	}
	
	public void onSpecificChange()
	{}
	
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
	
	public void updateGuiButtons()
	{}
	
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
