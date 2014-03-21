package de.dakror.arise.layer;

import java.awt.Graphics2D;
import java.io.IOException;

import de.dakror.arise.game.Game;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet02Disconnect;
import de.dakror.arise.net.packet.Packet02Disconnect.Cause;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class PauseLayer extends MPLayer
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
		if (Game.currentGame.alpha == 1 && gotoMenu && Game.userID == 0)
		{
			Game.currentGame.removeLayer(Game.world);
			Game.world = null;
			Game.worldID = 1;
			
			Game.currentGame.fadeTo(0, 0.05f);
			Game.currentGame.setLayer(new LoginLayer());
			gotoMenu = false;
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
				try
				{
					gotoMenu = true;
					Game.currentGame.addLayer(new LoadingLayer());
					Game.client.sendPacket(new Packet02Disconnect(Game.userID, Cause.USER_DISCONNECT));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
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
					if (Game.userID != 0)
					{
						Game.currentGame.addLayer(new LoadingLayer());
						Game.client.sendPacket(new Packet02Disconnect(Game.userID, Cause.USER_DISCONNECT));
					}
					else Game.exit();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		components.add(exit);
	}
	
	@Override
	public void onReceivePacket(Packet p)
	{
		if (p.getType() == PacketTypes.DISCONNECT && ((Packet02Disconnect) p).getCause() == Cause.SERVER_CONFIRMED)
		{
			if (gotoMenu)
			{
				Game.userID = 0;
				Game.currentGame.fadeTo(1, 0.05f);
			}
			else Game.exit();
		}
	}
}
