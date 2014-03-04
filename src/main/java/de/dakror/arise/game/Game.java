package de.dakror.arise.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.json.JSONObject;

import de.dakror.arise.game.world.World;
import de.dakror.arise.layer.CityLayer;
import de.dakror.arise.layer.LoginLayer;
import de.dakror.arise.layer.PauseLayer;
import de.dakror.gamesetup.applet.GameApplet;
import de.dakror.gamesetup.ui.InputField;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class Game extends GameApplet
{
	public static JSONObject buildingsConfig;
	public static Game currentGame;
	public static World world;
	public static int userID;
	public static int worldID = 1;
	public static String buildDate = "from now";
	public static long buildTimestamp = 0;
	public static int secondInMinute;
	
	boolean debug;
	
	public Game()
	{
		currentGame = this;
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		addLayer(new LoginLayer());
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		drawLayers(g);
		
		if (debug)
		{
			g.setColor(Color.white);
			Helper.drawString("Build " + buildDate, 10, 26, g, 18);
			Helper.drawString("FPS: " + getFPS(), 10, 52, g, 18);
			Helper.drawString("UPS: " + getUPS(), 10, 52 + 26, g, 18);
			if (world != null) Helper.drawString("E: " + world.citiesDrawn + " / " + world.cities, 10, 52 + 52, g, 18);
		}
	}
	
	public void startGame()
	{
		try
		{
			Helper.getURLContent(new URL("http://dakror.de/arise/world?spawn=true&userid=" + userID + "&id=" + worldID));
			buildingsConfig = new JSONObject(Helper.getURLContent(new URL("http://dakror.de/arise/building")));
			
			Calendar calendar = new GregorianCalendar();
			calendar.set(Calendar.MILLISECOND, 0);
			secondInMinute = calendar.get(Calendar.SECOND);
			final Timer t = new Timer();
			t.scheduleAtFixedRate(new TimerTask()
			{
				@Override
				public void run()
				{
					secondInMinute = (secondInMinute + 1) % 60;
					if (getActiveLayer() instanceof CityLayer) ((CityLayer) getActiveLayer()).updateResources();
				}
			}, calendar.getTime(), 1000);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		world = new World(worldID);
		
		layers.clear();
		
		addLayer(world);
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
}
