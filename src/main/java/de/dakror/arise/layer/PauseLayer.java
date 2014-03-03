package de.dakror.arise.layer;

import java.awt.Graphics2D;

import de.dakror.arise.game.Game;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class PauseLayer extends Layer
{
	public PauseLayer()
	{
		modal = true;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		drawModality(g);
		Helper.drawContainer((Game.getWidth() - TextButton.WIDTH - 40) / 2, (Game.getHeight() - TextButton.HEIGHT * 3 - 40) / 2, TextButton.WIDTH + 40, TextButton.HEIGHT * 3 + 40, true, false, g);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void init()
	{
		TextButton back = new TextButton((Game.getWidth() - TextButton.WIDTH - 40) / 2 + 20, (Game.getHeight() - TextButton.HEIGHT * 3 - 40) / 2 + 20, "Weiter");
		back.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentGame.removeLayer(PauseLayer.this);
			}
		});
		components.add(back);
		TextButton logout = new TextButton(back.getX(), back.getY() + TextButton.HEIGHT, "Abmelden");
		logout.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.userID = 0;
				Game.world = null;
				Game.worldID = 1;
				
				for (Layer l : Game.currentGame.layers)
					if (l instanceof CityLayer) ((CityLayer) l).saveData();
				
				Game.currentGame.setLayer(new LoginLayer());
			}
		});
		if (Game.userID != 0) components.add(logout);
		
		TextButton exit = new TextButton(back.getX(), back.getY() + TextButton.HEIGHT * 2, "Beenden");
		exit.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.applet.stop();
			}
		});
		components.add(exit);
	}
}
