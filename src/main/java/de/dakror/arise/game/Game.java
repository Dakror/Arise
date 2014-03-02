package de.dakror.arise.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import de.dakror.arise.layer.LoginLayer;
import de.dakror.gamesetup.applet.GameApplet;
import de.dakror.gamesetup.ui.InputField;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class Game extends GameApplet
{
	public static Game currentGame;
	public static World world;
	public static int userID;
	public static int worldID = 1;
	
	public Game()
	{
		currentGame = this;
	}
	
	@Override
	public void initGame()
	{
		InputField.h = 14;
		try
		{
			canvas.setFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/MorrisRomanBlack.ttf")));
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
		
		g.setColor(Color.white);
		Helper.drawString("FPS: " + getFPS(), 10, 26, g, 18);
		Helper.drawString("UPS: " + getUPS(), 10, 52, g, 18);
		if (world != null) Helper.drawString("E: " + world.citiesDrawn + " / " + world.cities, 10, 52 + 26, g, 18);
	}
	
	public void startGame()
	{
		try
		{
			Helper.getURLContent(new URL("http://dakror.de/arise/world?spawn=true&userid=" + userID + "&id=" + worldID));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		
		world = new World(worldID);
		
		layers.clear();
		
		addLayer(world);
	}
	
	@Override
	public BufferedImage loadImage(String p)
	{
		try
		{
			BufferedImage i = ImageIO.read(Game.class.getResource("/img/" + p));
			
			return i;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
