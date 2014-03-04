package de.dakror.arise.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

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
	
	public static BufferedImage drawBuildingStage(Building b)
	{
		BufferedImage tile = GameFrame.getImage("world/buildingStage.png");
		
		int width = b.bw * 32;
		int height = b.bh * 32;
		int size = 32;
		
		BufferedImage bi = new BufferedImage(width * size, height * size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		// copy of Assistant.drawShadow(...);
		
		Helper.drawImage(tile, 0, 0, size, size, 0, 0, size, size, g); // lt
		Helper.drawImage(tile, width - size, 0, size, size, size * 2, 0, size, size, g); // rt
		Helper.drawImage(tile, 0, height - size, size, size, 0, size * 2, size, size, g); // lb
		Helper.drawImage(tile, width - size, height - size, size, size, size * 2, size * 2, size, size, g); // rb
		
		for (int i = size; i <= width - size * 2; i += size)
			Helper.drawImage(tile, i, 0, size, size, size, 0, size, size, g);// t
		Helper.drawImage(tile, width - size - (width - size * 2) % size, 0, (width - size * 2) % size, size, size, 0, (width - size * 2) % size, size, g);
		
		for (int i = size; i <= width - size * 2; i += size)
			Helper.drawImage(tile, i, height - size, size, size, size, size * 2, size, size, g); // b
		Helper.drawImage(tile, width - size - (width - size * 2) % size, height - size, (width - size * 2) % size, size, size, size * 2, (width - size * 2) % size, size, g);
		
		for (int i = size; i <= height - size * 2; i += size)
			Helper.drawImage(tile, 0, i, size, size, 0, size, size, size, g); // l
		Helper.drawImage(tile, 0, height - size - (height - size * 2) % size, size, (height - size * 2) % size, 0, size, size, (height - size * 2) % size, g);
		
		for (int i = size; i <= height - size * 2; i += size)
			Helper.drawImage(tile, width - size, i, size, size, size * 2, size, size, size, g); // r
		Helper.drawImage(tile, width - size, height - size - (height - size * 2) % size, size, (height - size * 2) % size, size * 2, size, size, (height - size * 2) % size, g);
		
		Helper.drawImage(tile, size, size, width - size * 2, height - size * 2, size, size, size, size, g); // m
		
		return bi;
	}
}
