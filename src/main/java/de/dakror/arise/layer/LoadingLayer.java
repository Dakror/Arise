package de.dakror.arise.layer;

import java.awt.Graphics2D;

import de.dakror.arise.game.Game;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class LoadingLayer extends MPLayer
{
	int frame = 0;
	
	public LoadingLayer()
	{
		modal = true;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		drawModality(g);
		Helper.drawContainer((Game.getWidth() - 220) / 2, (Game.getHeight() - 80) / 2, 220, 80, false, false, g);
		Helper.drawImage2(Game.getImage("system/loader.png"), (Game.getWidth() - 180) / 2, (Game.getHeight() - 40) / 2, 180, 40, frame * 180, 0, 180, 40, g);
	}
	
	@Override
	public void update(int tick)
	{
		if (tick % 3 == 0) frame = (frame + 1) % 20;
	}
	
	@Override
	public void init()
	{}
}
