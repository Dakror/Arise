package de.dakror.arise.game.world;

import java.awt.Color;
import java.awt.Graphics2D;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.arise.game.Game;
import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class City extends ClickableComponent
{
	public static int SIZE = 64;
	
	String name;
	String username;
	int id;
	int level;
	int userId;
	
	public City(int x, int y, JSONObject data) throws JSONException
	{
		super(x, y, SIZE, SIZE);
		
		name = data.getString("NAME");
		username = data.getString("USERNAME");
		userId = data.getInt("USER_ID");
		id = data.getInt("ID");
		level = data.getInt("LEVEL");
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (state == 2)
		{
			Color c = g.getColor();
			g.setColor(Color.black);
			g.drawRect(x, y, width, height);
			g.setColor(c);
		}
		Helper.setRenderingHints(g, false);
		if (level == 0) Helper.drawImage2(Game.getImage("world/TileB.png"), x, y, width, height, 320, 352, 32, 32, g);
		Helper.setRenderingHints(g, true);
		
		Helper.drawHorizontallyCenteredString(name + " (" + (level + 1) + ")", x, width, y + height + 10, g, 25);
		Helper.drawHorizontallyCenteredString(username, x, width, y + height + 25, g, 20);
	}
	
	@Override
	public void update(int tick)
	{}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getUserId()
	{
		return userId;
	}
	
}
