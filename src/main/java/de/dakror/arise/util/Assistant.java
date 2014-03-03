package de.dakror.arise.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import de.dakror.arise.game.Game;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
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
}
