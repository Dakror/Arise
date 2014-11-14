package de.dakror.arise.game.building;

import java.awt.Graphics2D;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.world.City;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class Center extends Building {
	public Center(int x, int y, int level) {
		super(x, y, 5, 5, level);
		typeId = 1;
		name = level > 3 ? "Stadtzentrum" : "Dorfzentrum";
		
		init();
	}
	
	@Override
	public void drawStage1(Graphics2D g) {
		Helper.setRenderingHints(g, false);
		Helper.drawImage2(Game.getImage("world/TileB.png"), x, y, width, height, City.levels[level][0], City.levels[level][1], City.levels[level][2], City.levels[level][3], g);
		Helper.setRenderingHints(g, true);
	}
	
	@Override
	public void update(int tick) {
		super.update(tick);
		name = level > 3 ? "Stadtzentrum" : "Dorfzentrum";
	}
}
