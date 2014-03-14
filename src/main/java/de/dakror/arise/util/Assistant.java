package de.dakror.arise.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.building.Building;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class Assistant
{
	public static void drawResource(Resources resources, Resource r, int x, int y, int size, int space, Graphics2D g)
	{
		drawLabelWithIcon(x, y, size, new Point(r.getIconX(), r.getIconY()), resources.get(r) + "", space, g);
	}
	
	public static void drawResource(Resources resources, Resource r, int amount, int x, int y, int size, int space, Graphics2D g)
	{
		drawLabelWithIcon(x, y, size, new Point(r.getIconX(), r.getIconY()), amount + "", space, g);
	}
	
	public static void drawLabelWithIcon(int x, int y, int size, Point icon, String text, int space, Graphics2D g)
	{
		Helper.drawImage(Game.getImage("system/icons.png"), x, y, 24, 24, icon.x * 24, icon.y * 24, 24, 24, g);
		Font old = g.getFont();
		g.setFont(g.getFont().deriveFont((float) size));
		FontMetrics fm = g.getFontMetrics();
		g.drawString(text, x + space, y + fm.getAscent() + 2);
		g.setFont(old);
	}
	
	public static void drawBuildingStage(int x, int y, Building b, Graphics2D g)
	{
		BufferedImage tile = GameFrame.getImage("world/buildingStage.png");
		
		int width = b.bw * 32;
		int height = b.bh * 32;
		int size = 32;
		
		Helper.drawImage(tile, x, y, size, size, 0, 0, size, size, g); // lt
		Helper.drawImage(tile, x + width - size, y, size, size, size * 2, 0, size, size, g); // rt
		Helper.drawImage(tile, x, y + height - size, size, size, 0, size * 2, size, size, g); // lb
		Helper.drawImage(tile, x + width - size, y + height - size, size, size, size * 2, size * 2, size, size, g); // rb
		
		for (int i = size; i <= width - size * 2; i += size)
			Helper.drawImage(tile, x + i, y, size, size, size, 0, size, size, g);// t
		
		for (int i = size; i <= width - size * 2; i += size)
			Helper.drawImage(tile, x + i, y + height - size, size, size, size, size * 2, size, size, g); // b
		
		for (int i = size; i <= height - size * 2; i += size)
			Helper.drawImage(tile, x, y + i, size, size, 0, size, size, size, g); // l
		
		for (int i = size; i <= height - size * 2; i += size)
			Helper.drawImage(tile, x + width - size, y + i, size, size, size * 2, size, size, size, g); // r
		
		for (int i = size; i <= width - size * 2; i += size)
			for (int j = size; j <= height - size * 2; j += size)
				Helper.drawImage(tile, x + i, y + j, size, size, size, size, size, size, g); // m
	}
	
	public static String formatSeconds(long s)
	{
		String seconds = s % 60 + "";
		seconds = seconds.length() == 1 ? "0" + seconds : seconds;
		String minutes = (s / 60) % 60 + "";
		minutes = minutes.length() == 1 ? "0" + minutes : minutes;
		String hours = s / 3600 + "";
		hours = hours.length() == 1 ? "0" + hours : hours;
		
		return (hours.equals("00") ? "" : hours + ":") + minutes + ":" + seconds;
	}
	
	public static InetAddress getBroadcastAddress()
	{
		try
		{
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements())
			{
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback()) continue;
				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
				{
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast != null) return broadcast;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
