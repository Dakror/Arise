package de.dakror.arise.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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
