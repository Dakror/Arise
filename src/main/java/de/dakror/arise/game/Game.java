package de.dakror.arise.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.json.JSONObject;

import de.dakror.arise.Arise;
import de.dakror.arise.game.building.Building;
import de.dakror.arise.game.world.World;
import de.dakror.arise.layer.LoadingLayer;
import de.dakror.arise.layer.LoginLayer;
import de.dakror.arise.layer.PauseLayer;
import de.dakror.arise.net.Client;
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
	public static final int INTERVAL = 10;
	
	public static Client client;
	public static Game currentGame;
	public static JSONObject config;
	public static String username;
	public static String buildDate = "from now";
	public static World world;
	
	public static int secondInMinute;
	public static int userID;
	public static int worldID = 1;
	
	
	public static boolean gotoMenu;
	public static boolean inLan = false;
	
	public static long buildTimestamp = 0;
	
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
			config = new JSONObject(Helper.getURLContent(new URL("http://dakror.de/arise/config.json")));
			Building.DECONSTRUCT_FACTOR = (float) config.getDouble("deconstruct");
			Building.UPGRADE_FACTOR = (float) config.getDouble("upgrade");
			Building.MAX_LEVEL = config.getInt("maxlevel");
			Building.TROOPS = config.getJSONObject("troops");
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
			readManifest();
			
			addLayer(new LoginLayer());
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
			Helper.drawString("Build " + buildDate, 10, 26, g, 18);
			Helper.drawString("FPS: " + getFPS(), 10, 52, g, 18);
			Helper.drawString("UPS: " + getUPS(), 10, 52 + 26, g, 18);
			
			if (world != null) Helper.drawString("E: " + world.citiesDrawn + " / " + world.cities, 10, 52 + 52, g, 18);
			
			Helper.drawString("RAM: " + Helper.formatBinarySize(usedMem, 2) + " / " + Helper.formatBinarySize(Runtime.getRuntime().totalMemory(), 2), 10, 52 + 52 + 26, g, 18);
			Helper.drawString("CPUs: " + Runtime.getRuntime().availableProcessors(), 10, 52 + 52 + 52, g, 18);
		}
	}
	
	public void startGame()
	{
		try
		{
			// Helper.getURLContent(new URL("http://dakror.de/arise/world?spawn=true&userid=" + userID + "&id=" + worldID));
			//
			// Calendar calendar = new GregorianCalendar();
			// calendar.set(Calendar.MILLISECOND, 0);
			// secondInMinute = calendar.get(Calendar.SECOND);
			// final Timer t = new Timer();
			// t.scheduleAtFixedRate(new TimerTask()
			// {
			// @Override
			// public void run()
			// {
			// secondInMinute = (secondInMinute + INTERVAL) % 60;
			// for (Layer l : layers)
			// {
			// if (l instanceof CityHUDLayer)
			// {
			// ((CityHUDLayer) l).timerTick();
			// break;
			// }
			// }
			// }
			// }, calendar.getTime(), INTERVAL * 1000);
			
			setLayer(world);
			System.gc();
			
			fadeTo(0, 0.05f);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void readManifest() throws Exception
	{
		String className = getClass().getSimpleName() + ".class";
		String classPath = getClass().getResource(className).toString();
		if (!classPath.startsWith("jar")) return;
		
		String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
		Manifest manifest = new Manifest(new URL(manifestPath).openStream());
		Attributes attr = manifest.getMainAttributes();
		buildDate = attr.getValue("Build-Date");
		buildTimestamp = Long.parseLong(attr.getValue("Build-Timestamp").trim());
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
