package de.dakror.arise.ui;

import java.awt.Graphics2D;
import java.awt.Point;

import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.util.Assistant;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class ResourceLabel extends Component
{
	Resource r;
	Resources res;
	
	public int off;
	public boolean tooltipOnBottom;
	
	public ResourceLabel(int x, int y, Resources res, Resource r)
	{
		super(x, y, 0, 25);
		
		this.res = res;
		this.r = r;
		tooltipOnBottom = false;
		off = -1;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (width == 0) width = 25 + g.getFontMetrics(g.getFont().deriveFont(25f)).stringWidth(res.get(r) + "" + (off > -1 ? " / " + off : ""));
		
		Assistant.drawLabelWithIcon(x, y, 25, new Point(r.getIconX(), r.getIconY()), res.get(r) + "" + (off > -1 ? " / " + off : ""), 25, g);
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g)
	{
		Helper.drawShadow(x, y - (tooltipOnBottom ? 54 : 0), g.getFontMetrics(g.getFont().deriveFont(30f)).stringWidth(r.getName()) + 30, 64, g);
		Helper.drawString(r.getName(), x + 15, y + 40 - (tooltipOnBottom ? 54 : 0), g, 30);
	}
}
