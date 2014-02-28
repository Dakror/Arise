package de.dakror.arise.layer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import de.dakror.arise.game.Game;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class LoginLayer extends Layer
{
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("system/menu.jpg"), 0, 0, Game.getWidth(), Game.getHeight(), null);
		
		Shape oc = g.getClip();
		g.setClip(new Rectangle(Game.getWidth() / 4, 190 + 19, Game.getWidth() / 2, 16));
		Helper.drawOutline(Game.getWidth() / 4, 190, Game.getWidth() / 2, 32, true, g);
		
		g.setClip(oc);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("system/title.png"), 50, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		Helper.drawContainer(Game.getWidth() / 3, 300, Game.getWidth() / 3, Game.getHeight() / 3, true, false, g);
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void init()
	{}
}
