package de.dakror.arise.game.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import org.json.JSONObject;

import de.dakror.arise.game.Game;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class World extends Layer
{
	String name;
	int speed;
	int id;
	
	long lastCheck;
	
	BufferedImage bi;
	
	public World(int id)
	{
		try
		{
			this.id = id;
			JSONObject data = new JSONObject(Helper.getURLContent(new URL("http://dakror.de/arise/world?get=" + id)));
			speed = data.getInt("SPEED");
			name = data.getString("NAME");
			
			bi = new BufferedImage(Game.getWidth(), Game.getHeight(), BufferedImage.TYPE_INT_ARGB);
			for (int i = 0; i < bi.getWidth(); i += 32)
				for (int j = 0; j < bi.getHeight(); j += 32)
					Helper.drawImage2(Game.getImage("world/ground.png"), i, j, 32, 32, 32, 0, 32, 32, (Graphics2D) bi.getGraphics());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void init()
	{}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(bi, 0, 0, null);
	}
	
	@Override
	public void update(int tick)
	{
		if (lastCheck == 0)
		{	
			
		}
	}
}
