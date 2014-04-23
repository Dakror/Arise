package de.dakror.arise.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import de.dakror.arise.Arise;
import de.dakror.arise.game.world.World;
import de.dakror.arise.layer.LoadingLayer;
import de.dakror.arise.layer.LoginLayer;
import de.dakror.arise.layer.LoginLayerDakrorLauncher;
import de.dakror.arise.layer.PauseLayer;
import de.dakror.arise.layer.WorldHUDLayer;
import de.dakror.arise.net.Client;
import de.dakror.arise.settings.Const;
import de.dakror.dakrorbin.DakrorBin;
import de.dakror.gamesetup.applet.GameApplet;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.InputField;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class Game extends GameApplet
{
	public static Client client;
	public static Game currentGame;
	public static JSONObject config;
	public static String username;
	public static World world;
	
	public static int userID;
	public static int worldID = 1;
	
	public static boolean gotoMenu;
	
	long usedMem;
	
	boolean debug;
	
	public Game()
	{
		currentGame = this;
	}
	
	public static void loadConfig()
	{
		try
		{
			config = new JSONObject(Helper.getURLContent(Game.class.getResource("/config.json")));
			
			Const.DECONSTRUCT_FACTOR = (float) config.getDouble("deconstruct");
			Const.UPGRADE_FACTOR = (float) config.getDouble("upgrade");
			Const.BUILDING_MAX_LEVEL = config.getInt("maxlevel");
			Const.CITY_TAKEOVERS = config.getInt("takeovers");
			Const.TAKEOVER_FACTOR = (float) config.getDouble("takeover_factor");
			Const.MARCH_SECONDS = config.getInt("troops");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void initGame()
	{
		debug = false;
		InputField.h = 14;
		try
		{
			canvas.setFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/MorrisRomanBlack.ttf")));
			
			if (!Arise.wrapper) addLayer(new LoginLayer());
			addLayer(new LoadingLayer());
			
			loadConfig();
			
			client = new Client();
			if (!client.connectToServer())
			{
				addLayer(new Alert("Anscheinend ist der Server aktuell nicht erreichbar. Wir untersuchen dieses Problem bereits, um es so schnell wie möglich zu beheben.", new ClickEvent()
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
				}));
			}
			else
			{
				removeLoadingLayer();
				client.start();
				
				if (Arise.wrapper) addLayer(new LoginLayerDakrorLauncher());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void removeLoadingLayer()
	{
		for (Layer l : layers)
			if (l instanceof LoadingLayer) layers.remove(l);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		drawLayers(g);
		
		if (debug)
		{
			Layer.drawModality(g);
			g.setColor(Color.white);
			Helper.drawString("Build " + DakrorBin.buildDate, 10, 26, g, 18);
			Helper.drawString("FPS: " + getFPS(), 10, 52, g, 18);
			Helper.drawString("UPS: " + getUPS(), 10, 52 + 26, g, 18);
			
			Helper.drawString("RAM: " + Helper.formatBinarySize(usedMem, 2) + " / " + Helper.formatBinarySize(Runtime.getRuntime().totalMemory(), 2), 10, 52 + 52 + 26, g, 18);
			Helper.drawString("CPUs: " + Runtime.getRuntime().availableProcessors(), 10, 52 + 52 + 52, g, 18);
		}
	}
	
	public void startGame()
	{
		setLayer(world);
		addLayer(new WorldHUDLayer());
		
		System.gc();
		
		fadeTo(0, 0.05f);
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		super.keyPressed(e);
		
		if (e.getKeyCode() == KeyEvent.VK_F1) debug = !debug;
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) toggleLayer(new PauseLayer());
	}
	
	public static void exit()
	{
		try
		{
			if (currentGame.updater != null) currentGame.updater.closeRequested = true;
			client.running = false;
			if (!Arise.wrapper) Game.applet.getAppletContext().showDocument(new URL("http://dakror.de"));
			else System.exit(0);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
}
