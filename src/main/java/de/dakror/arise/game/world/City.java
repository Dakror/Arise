package de.dakror.arise.game.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.IOException;

import de.dakror.arise.game.Game;
import de.dakror.arise.layer.CityLayer;
import de.dakror.arise.net.packet.Packet04City;
import de.dakror.arise.net.packet.Packet05Resources;
import de.dakror.arise.net.packet.Packet06Building;
import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class City extends ClickableComponent
{
	public static int SIZE = 96;
	
	public static int[][] levels = { { 320, 352, 32, 32 }, { 352, 352, 32, 32 }, { 448, 320, 32, 32 }, { 256, 352, 32, 32 }, { 320, 416, 32, 32 }, { 448, 192, 64, 64 }, { 256, 192, 64, 64 } };
	
	String name;
	String username;
	int id;
	int level;
	int userId;
	
	public Packet05Resources resourcePacket;
	
	public City(int x, int y, Packet04City data)
	{
		super(x, y, SIZE, SIZE);
		
		name = data.getCityName();
		username = data.getUsername();
		userId = data.getUserId();
		id = data.getCityId();
		level = data.getLevel();
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (state != 0)
		{
			Color c = g.getColor();
			g.setColor(Color.black);
			g.drawRect(x, y, width, height);
			g.setColor(c);
		}
		
		Helper.setRenderingHints(g, false);
		Helper.drawImage2(Game.getImage("world/TileB.png"), x + 16, y + 16, 64, 64, levels[level][0], levels[level][1], levels[level][2], levels[level][3], g);
		Helper.setRenderingHints(g, true);
		
		int y1 = y + height - 15;
		y1 = y1 < Game.world.getHeight() ? y1 : Game.world.getHeight() - 25;
		Color c = g.getColor();
		g.setColor(userId == Game.userID ? Color.decode("#007eff") : Color.white);
		Helper.drawHorizontallyCenteredString(name, x, width, y1, g, 20);
		Helper.drawHorizontallyCenteredString(username, x, width, y1 + 15, g, 17);
		g.setColor(c);
	}
	
	@Override
	public void update(int tick)
	{
		if (Game.currentGame.alpha == 1 && resourcePacket != null)
		{
			// CityHUDLayer.cl = cl;
			Game.currentGame.addLayer(new CityLayer(City.this));
			// Game.currentGame.addLayer(new CityHUDLayer(cl));
			Game.currentGame.fadeTo(0, 0.05f);
			Game.world.gotoCity = null;
			resourcePacket = null;
			try
			{
				Game.client.sendPacket(new Packet06Building(id));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
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
	
	public void levelUp()
	{
		level++;
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		
		if (state == 1 && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && userId == Game.userID)
		{
			Game.world.gotoCity = this;
			try
			{
				Game.client.sendPacket(new Packet05Resources(id));
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
}
