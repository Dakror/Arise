package de.dakror.arisewebsite.ui;

import java.awt.Graphics2D;
import java.awt.Point;

import de.dakror.arisewebsite.layer.CityLayer;
import de.dakror.arisewebsite.settings.Resources.Resource;
import de.dakror.arisewebsite.util.Assistant;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class ArmyLabel extends Component
{
	public static Resource[] ARMY = { Resource.SWORDFIGHTER, Resource.LANCEBEARER };
	
	public ArmyLabel(int x, int y)
	{
		super(x, y, 0, 25);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		int army = 0;
		for (Resource r : ARMY)
			army += CityLayer.resources.get(r);
		
		String string = army + "";
		if (string.length() > 3) string = string.substring(0, string.length() - 3) + "k";
		if (string.length() > 5) string = string.substring(0, string.length() - 5) + "m";
		
		if (width == 0) width = 25 + g.getFontMetrics(g.getFont().deriveFont(25f)).stringWidth(string);
		Assistant.drawLabelWithIcon(x, y, 25, new Point(Resource.ARMY.getIconX(), Resource.ARMY.getIconY()), string, 25, g);
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g)
	{
		int width = 150;
		int height = ARMY.length * 30 + 70;
		int x1 = x;
		int y1 = y;
		
		if (x1 + width > GameFrame.getWidth()) x1 -= (x1 + width) - GameFrame.getWidth();
		if (y1 + height > GameFrame.getHeight()) y1 -= (y1 + height) - GameFrame.getHeight();
		
		Helper.drawShadow(x1, y1, width, height, g);
		Helper.drawString("Truppen", x1 + 20, y1 + 40, g, 35);
		for (int i = 0; i < ARMY.length; i++)
		{
			Resource r = ARMY[i];
			Assistant.drawResource(CityLayer.resources, r, x1 + 20, y1 + i * 30 + 50, 30, 30, g);
		}
	}
}
