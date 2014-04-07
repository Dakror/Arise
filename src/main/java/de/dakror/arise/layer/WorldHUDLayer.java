package de.dakror.arise.layer;

import java.awt.Graphics2D;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.world.City;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class WorldHUDLayer extends MPLayer
{
	public static City selectedCity;
	
	@Override
	public void init()
	{}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (selectedCity != null)
		{
			int width = 300, height = 200;
			Helper.drawContainer(Game.getWidth() - width, Game.getHeight() - height, width, height, true, false, g);
		}
	}
	
	@Override
	public void update(int tick)
	{
		if (!Game.world.anyCityActive) selectedCity = null;
	}
}
