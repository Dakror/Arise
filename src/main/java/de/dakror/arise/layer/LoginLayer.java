package de.dakror.arise.layer;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

import javax.swing.JOptionPane;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import de.dakror.arise.Arise;
import de.dakror.arise.game.Game;
import de.dakror.arise.game.building.Building;
import de.dakror.arise.game.world.World;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet01Login;
import de.dakror.arise.net.packet.Packet03World;
import de.dakror.arise.net.packet.Packet04City;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.InputField;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class LoginLayer extends MPLayer
{
	BufferedImage cache;
	TextButton login;
	InputField username, password;
	
	@Override
	public void draw(Graphics2D g)
	{
		if (cache == null)
		{
			cache = new BufferedImage(Game.getWidth(), Game.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D) cache.getGraphics();
			Helper.setRenderingHints(g2, true);
			g2.setFont(g.getFont());
			g2.drawImage(Game.getImage("system/menu.jpg"), 0, 0, Game.getWidth(), Game.getHeight(), null);
			
			Shape oc = g2.getClip();
			g2.setClip(new Rectangle(Game.getWidth() / 4, 190 + 19, Game.getWidth() / 2, 16));
			Helper.drawOutline(Game.getWidth() / 4, 190, Game.getWidth() / 2, 32, true, g2);
			
			g2.setClip(oc);
			Helper.drawImageCenteredRelativeScaled(Game.getImage("system/title.png"), 50, 1920, 1080, Game.getWidth(), Game.getHeight(), g2);
			
			Helper.drawContainer((Game.getWidth() - (TextButton.WIDTH + 40)) / 2, 300, TextButton.WIDTH + 40, TextButton.HEIGHT * 4 + 40, true, false, g2);
		}
		
		g.drawImage(cache, 0, 0, null);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		if (login != null) login.enabled = username.getText().length() >= 4 && password.getText().length() > 0;
		
		if (Game.currentGame.alpha == 1) Game.currentGame.startGame();
	}
	
	public void initFirstPage()
	{
		login = null;
		components.clear();
		
		TextButton login = new TextButton((Game.getWidth() - (TextButton.WIDTH + 40)) / 2 + 20, 320, "Anmelden");
		login.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				initSecondPage();
			}
		});
		components.add(login);
		TextButton register = new TextButton(login.getX(), login.getY() + TextButton.HEIGHT, "Registrieren");
		register.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				try
				{
					if (!Arise.wrapper) Game.applet.getAppletContext().showDocument(new URL("http://dakror.de#register"), "_blank");
					else Desktop.getDesktop().browse(new URL("http://dakror.de#register").toURI());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		components.add(register);
		TextButton exit = new TextButton(login.getX(), register.getY() + TextButton.HEIGHT * 2, "Beenden");
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
	
	public void initSecondPage()
	{
		components.clear();
		TextButton back = new TextButton((Game.getWidth() - (TextButton.WIDTH + 40)) / 2 + 20, 320, "Zurück");
		back.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				initFirstPage();
			}
		});
		components.add(back);
		
		username = new InputField(back.getX(), back.getY() + TextButton.HEIGHT, TextButton.WIDTH, 40);
		username.setAllowed("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890öäüÖÄÜß");
		username.setHint("Benutzername");
		components.add(username);
		
		password = new InputField(back.getX(), back.getY() + TextButton.HEIGHT * 2, TextButton.WIDTH, 40);
		password.setPassword(true);
		String allowed = password.getAllowed();
		allowed += ",;.:-_#'+*~!§$%&/()=?<>| ";
		password.setAllowed(allowed);
		password.setHint("Passwort");
		components.add(password);
		
		login = new TextButton(back.getX(), back.getY() + TextButton.HEIGHT * 3, "Anmelden");
		login.enabled = false;
		login.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				try
				{
					final String pw = new String(HexBin.encode(MessageDigest.getInstance("MD5").digest(password.getText().getBytes()))).toLowerCase();
					Game.client.sendPacket(new Packet01Login(username.getText(), pw));
					Game.currentGame.addLayer(new LoadingLayer());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		components.add(login);
	}
	
	@Override
	public void init()
	{
		initFirstPage();
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		super.keyPressed(e);
		
		if (e.getKeyCode() == KeyEvent.VK_F2)
		{
			String id = JOptionPane.showInputDialog("ID der gewünschten Welt: ", Game.worldID);
			try
			{
				int i = Integer.parseInt(id);
				Game.worldID = i;
			}
			catch (Exception e1)
			{}
		}
	}
	
	@Override
	public void onReceivePacket(Packet p)
	{
		super.onReceivePacket(p);
		
		if (p.getType() == PacketTypes.LOGIN)
		{
			if (!((Packet01Login) p).isLoggedIn())
			{
				Game.currentGame.removeLoadingLayer();
				Game.currentGame.addLayer(new Alert("Login inkorrekt!", new ClickEvent()
				{
					@Override
					public void trigger()
					{
						password.setText("");
					}
				}));
			}
			else
			{
				Game.username = ((Packet01Login) p).getUsername();
				Game.userID = ((Packet01Login) p).getUserId();
				try
				{
					Game.client.sendPacket(new Packet03World(Game.worldID));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		if (p.getType() == PacketTypes.WORLD)
		{
			try
			{
				Game.world = new World((Packet03World) p);
				Building.MAX_QUEUE = Game.config.getInt("maxqueue") * Game.world.getSpeed();
				Game.client.sendPacket(new Packet04City(Game.worldID));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if (p.getType() == PacketTypes.CITY)
		{
			Game.world.onReceivePacket(p);
			
			if (Game.world.components.size() >= ((Packet04City) p).getCities())
			{
				Game.currentGame.removeLoadingLayer();
				Game.currentGame.fadeTo(1, 0.05f);
			}
		}
	}
}
