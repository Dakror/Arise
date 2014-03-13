package de.dakror.arise.ui;

import java.awt.Graphics2D;
import java.awt.Point;

import de.dakror.arise.layer.CityLayer;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.util.Assistant;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class ResourceLabel extends Component
{
	Resource r;
	
	public int off, perHour;
	
	public ResourceLabel(int x, int y, Resource r)
	{
		super(x, y, 0, 25);
		
		this.r = r;
		off = -1;
		perHour = 0;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		String perhour = perHour + "";
		if (perhour.length() > 3) perhour = perhour.substring(0, perhour.length() - 3) + "k";
		if (perhour.length() > 5) perhour = perhour.substring(0, perhour.length() - 5) + "m";
		
		String string = CityLayer.resources.get(r) + "" + (off > -1 ? " / " + off : "") + (perHour != 0 ? " (" + (perHour > 0 ? "+" : "") + perhour + "/h)" : "");
		
		if (width == 0) width = 25 + g.getFontMetrics(g.getFont().deriveFont(25f)).stringWidth(string);
		
		Assistant.drawLabelWithIcon(x, y, 25, new Point(r.getIconX(), r.getIconY()), string, 25, g);
	}
	
	public Resource getResource()
	{
		return r;
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g)
	{
		int width = g.getFontMetrics(g.getFont().deriveFont(30f)).stringWidth(r.getName()) + 30;
		int height = 64;
		int x1 = x;
		int y1 = y;
		
		if (x1 + width > GameFrame.getWidth()) x1 -= (x1 + width) - GameFrame.getWidth();
		if (y1 + height > GameFrame.getHeight()) y1 -= (y1 + height) - GameFrame.getHeight();
		
		Helper.drawShadow(x1, y1, width, height, g);
		Helper.drawString(r.getName(), x1 + 15, y1 + 40, g, 30);
	}
}
