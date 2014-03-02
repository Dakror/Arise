package de.dakror.arise.game.building;

import java.awt.Graphics2D;

import de.dakror.arise.game.City;
import de.dakror.arise.game.Game;
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
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Helper.setRenderingHints(g, false);
		Helper.drawImage2(Game.getImage("world/TileB.png"), x + 16, y + 16, 64, 64, City.levels[level][0], City.levels[level][1], City.levels[level][2], City.levels[level][3], g);
		Helper.setRenderingHints(g, true);
	}
}
