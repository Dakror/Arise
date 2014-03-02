package de.dakror.arise.game.building;

import java.awt.Graphics2D;

import de.dakror.arise.game.Game;
import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public abstract class Building extends ClickableComponent
{
	protected int tx, ty, tw, th;
	protected int typeId;
	protected int level;
	protected String name;
	
	public Building(int x, int y, int width, int height, int level)
	{
		super(x * 32, y * 32, width * 32, height * 32);
		
		this.level = level;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
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
	
	public String getData()
	{
		return typeId + ":" + level + ":" + (x / 32) + ":" + (y / 32);
	}
	
	public static Building getBuildingByTypeId(int x, int y, int level, int typeId)
	{
		switch (typeId)
		{
			case 1:
				return new Centre(x, y, level);
				
			default:
				return null;
		}
	}
}
