package de.dakror.arisewebsite.layer;

import java.awt.Graphics2D;
import java.net.MalformedURLException;
import java.net.URL;

import de.dakror.arisewebsite.Arise;
import de.dakror.arisewebsite.game.Game;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class PauseLayer extends Layer
{
	boolean gotoMenu;
	
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
	{
		if (Game.currentGame.alpha == 1 && gotoMenu)
		{
			Game.userID = 0;
			Game.currentGame.removeLayer(Game.world);
			Game.world = null;
			Game.worldID = 1;
			
			for (Layer l : Game.currentGame.layers)
				if (l instanceof CityLayer) ((CityLayer) l).saveData();
			
			Game.currentGame.setLayer(new LoginLayer());
			gotoMenu = false;
			Game.currentGame.fadeTo(0, 0.05f);
		}
	}
	
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
				gotoMenu = true;
				Game.currentGame.fadeTo(1, 0.05f);
			}
		});
		if (Game.userID != 0) components.add(logout);
		
		TextButton exit = new TextButton(back.getX(), back.getY() + TextButton.HEIGHT * 2, "Beenden");
		exit.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				try
				{
					if (!Arise.wrapper) Game.applet.getAppletContext().showDocument(new URL("http://dakror.de"));
					else System.exit(0);
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
			}
		});
		components.add(exit);
	}
}
