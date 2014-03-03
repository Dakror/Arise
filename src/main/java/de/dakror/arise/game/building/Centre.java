package de.dakror.arise.game.building;

import java.awt.Color;
import java.awt.Graphics2D;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.world.City;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class Centre extends Building
{
	public Centre(int x, int y, int level)
	{
		super(x, y, 3, 3, level);
		typeId = 1;
		name = level > 3 ? "Stadtzentrum" : "Dorfzentrum";
		
		init();
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
		Helper.drawImage2(Game.getImage("world/TileB.png"), x, y, width, height, City.levels[level][0], City.levels[level][1], City.levels[level][2], City.levels[level][3], g);
		Helper.setRenderingHints(g, true);
	}
}
