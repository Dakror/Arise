package de.dakror.arise.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.dakror.arise.game.building.Building;
import de.dakror.arise.layer.CityLayer;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.util.Assistant;
import de.dakror.gamesetup.ui.button.IconButton;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class BuildingButton extends IconButton
{
	Building building;
	BufferedImage tooltipCache;
	int tooltipRows, tooltipHeight;
	
	public BuildingButton(int x, int y, int width, int height, Image img, Building building)
	{
		super(x, y, width, height, img);
		mode1 = true;
		this.building = building;
		tooltip = building.getName();
		tooltipOnBottom = true;
	}
	
	@Override
	public void update(int tick)
	{
		if (CityLayer.allBuildingsEnabled) checkIfCanEffort();
	}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g)
	{
		Color c = g.getColor();
		ArrayList<Resource> resources = building.getBuildingCosts().getFilled();
		ArrayList<Resource> products = building.getProducts().getFilled();
		
		if (tooltipCache == null)
		{
			g.setColor(new Color(0, 0, 0, 0));
			int width = 250;
			tooltipRows = Helper.drawStringWrapped(building.getDescription(), x, y + 40, width - 20, g, 25);
			tooltipHeight = tooltipRows * 25 + 75 + resources.size() * 30 + 55 + products.size() * 30;
			
			tooltipCache = new BufferedImage(width, tooltipHeight, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2 = (Graphics2D) tooltipCache.getGraphics();
			Helper.setRenderingHints(g2, true);
			g2.setFont(g.getFont());
			
			Helper.drawShadow(0, 0, width, tooltipHeight, g2);
			g2.setColor(Color.white);
			Helper.drawString(tooltip, 20, 50, g2, 40);
			Helper.drawStringWrapped(building.getDescription(), 30, 80, width - 20, g2, 25);
			Helper.drawString("Baukosten", 25, 50 + tooltipRows * 25 + 35, g2, 30);
			Helper.drawString("Produktion", 25, 80 + tooltipRows * 25 + 35 + resources.size() * 30, g2, 30);
			for (int i = 0; i < products.size(); i++)
			{
				Resource r = products.get(i);
				int f = building.getProducts().get(r);
				Assistant.drawLabelWithIcon(30, 80 + tooltipRows * 25 + 40 + resources.size() * 30 + i * 30, 25, new Point(r.getIconX(), r.getIconY()), (f > 0 ? "+" : "") + f + "/h", 30, g2);
			}
		}
		else g.drawImage(tooltipCache, x, y - tooltipCache.getHeight(), null);
		
		for (int i = 0; i < resources.size(); i++)
		{
			Resource r = resources.get(i);
			if (r.isUsable())
			{
				boolean en = CityLayer.resources.get(r) >= building.getBuildingCosts().get(r);
				g.setColor(en ? Color.white : Color.red);
			}
			else
			{
				boolean en = CityLayer.resources.get(r) < building.getBuildingCosts().get(r);
				g.setColor(en ? Color.decode("#18acf1") : Color.red);
			}
			Assistant.drawResource(building.getBuildingCosts(), r, x + 30, y - tooltipHeight + 95 + tooltipRows * 25 + i * 30, 25, 30, g);
		}
		
		g.setColor(c);
	}
	
	public void checkIfCanEffort()
	{
		ArrayList<Resource> resources = building.getBuildingCosts().getFilled();
		
		boolean canEffort = true;
		for (int i = 0; i < resources.size(); i++)
		{
			Resource r = resources.get(i);
			if (r.isUsable())
			{
				boolean en = CityLayer.resources.get(r) >= building.getBuildingCosts().get(r);
				if (!en) canEffort = false;
			}
			else
			{
				boolean en = CityLayer.resources.get(r) < building.getBuildingCosts().get(r);
				if (!en) canEffort = false;
			}
		}
		
		enabled = canEffort;
	}
}
