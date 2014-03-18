package de.dakror.arise.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.building.Building;
import de.dakror.arise.layer.CityLayer;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.util.Assistant;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.ui.button.IconButton;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class BuildButton extends IconButton
{
	Resources products, scale, buildingCosts;
	String desc;
	BufferedImage tooltipCache;
	int tooltipRows, tooltipHeight, minCityLevel, level, maxLevel;
	public int number;
	boolean upgradeMode;
	
	public BuildButton(int x, int y, int width, int height, Image img)
	{
		super(x, y, width, height, img);
		mode1 = true;
		
		level = 0;
	}
	
	public BuildButton(int x, int y, int width, int height, Image img, Building b)
	{
		this(x, y, width, height, img);
		setBuildingMode(b);
	}
	
	public void setBuildingMode(Building b)
	{
		upgradeMode = false;
		tooltip = b.getName();
		desc = b.getDescription();
		products = b.getProducts();
		scale = b.getScale();
		buildingCosts = b.getBuildingCosts();
		minCityLevel = b.getMinCityLevel();
		level = b.getLevel();
		maxLevel = b.getMaxLevel();
		tooltipCache = null;
	}
	
	public void setUpgradeMode(String text, String desc, Resources buildingCosts, int minCityLevel)
	{
		upgradeMode = true;
		products = new Resources();
		scale = new Resources();
		tooltip = text;
		this.desc = desc;
		this.buildingCosts = buildingCosts;
		this.minCityLevel = minCityLevel;
		
		tooltipCache = null;
	}
	
	public void setProducts(Resources p)
	{
		products = p;
	}
	
	public void setScale(Resources s)
	{
		scale = s;
	}
	
	public void setLevel(int l)
	{
		level = l;
	}
	
	public void setMaxLevel(int l)
	{
		maxLevel = l;
	}
	
	public Resources getBuildingCosts()
	{
		return buildingCosts;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		super.draw(g);
		
		if (number > 0) Helper.drawRightAlignedString(number + "", x + width + 7, y + height + 5, g, 35);
	}
	
	@Override
	public void update(int tick)
	{
		if (buildingCosts != null) checkIfCanEffort();
	}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g)
	{
		if (level < maxLevel - 1 || maxLevel == 0)
		{
			Color c = g.getColor();
			ArrayList<Resource> resources = buildingCosts.getFilled();
			ArrayList<Resource> products = this.products.getFilled();
			
			if (tooltipCache == null)
			{
				int hW = g.getFontMetrics(g.getFont().deriveFont(40f)).stringWidth(tooltip) + 40;
				int width = hW > 250 ? hW : 250;
				tooltipRows = Helper.getLineCount(desc, width - 40, g, 25);
				tooltipHeight = tooltipRows * 25 + 75 + resources.size() * 30 + (products.size() > 0 ? 55 + products.size() * 30 : 30) + (minCityLevel > 0 ? 35 : 0) - (buildingCosts.size() == 0 ? 30 : 0);
				
				tooltipCache = new BufferedImage(width, tooltipHeight, BufferedImage.TYPE_INT_ARGB);
				
				Graphics2D g2 = (Graphics2D) tooltipCache.getGraphics();
				Helper.setRenderingHints(g2, true);
				g2.setFont(g.getFont());
				
				Helper.drawShadow(0, 0, width, tooltipHeight, g2);
				g2.setColor(Color.white);
				Helper.drawString(tooltip, 20, 50, g2, 40);
				Helper.drawStringWrapped(desc, 30, 80, width - 40, g2, 25);
				if (resources.size() > 0) Helper.drawString("Kosten", 25, 50 + tooltipRows * 25 + 35, g2, 30);
				if (products.size() > 0) Helper.drawString("Produktion", 25, 80 + tooltipRows * 25 + 35 + resources.size() * 30, g2, 30);
				for (int i = 0; i < products.size(); i++)
				{
					Resource r = products.get(i);
					int f = this.products.get(r) * (r.isUsable() ? Game.world.getSpeed() : 1) * (level + 1);
					int sc = scale.get(r) * (r.isUsable() ? Game.world.getSpeed() : 1);
					
					String pr = f + "";
					if (pr.length() > 3) pr = pr.substring(0, pr.length() - 3) + "k";
					if (pr.length() > 5) pr = pr.substring(0, pr.length() - 5) + "m";
					
					String scs = sc + "";
					if (scs.length() > 3) scs = scs.substring(0, scs.length() - 3) + "k";
					if (scs.length() > 5) scs = scs.substring(0, scs.length() - 5) + "m";
					
					String scp = (sc + f) + "";
					if (scp.length() > 3) scp = scp.substring(0, scp.length() - 3) + "k";
					if (scp.length() > 5) scp = scp.substring(0, scp.length() - 5) + "m";
					
					String scStr = sc > 0 ? (upgradeMode ? " -> " + (sc + f > 0 && r.isUsable() ? "+" : "") + scp + (r.isUsable() ? "/h" : "") : " (+" + scs + "/lvl)") : "";
					String str = (f > 0 && r.isUsable() ? "+" : "") + pr + (r.isUsable() ? "/h" : "") + scStr;
					
					Assistant.drawLabelWithIcon(30, 80 + tooltipRows * 25 + 40 + resources.size() * 30 + i * 30 + (minCityLevel > 0 ? 60 : 0), 25, new Point(r.getIconX(), r.getIconY()), str, 30, g2);
				}
			}
			int width = tooltipCache.getWidth();
			int height = tooltipCache.getHeight();
			int x1 = x;
			int y1 = y;
			
			if (x1 + width > GameFrame.getWidth()) x1 -= (x1 + width) - GameFrame.getWidth();
			if (y1 + height > GameFrame.getHeight()) y1 -= (y1 + height) - GameFrame.getHeight();
			
			g.drawImage(tooltipCache, x1, y1, null);
			
			for (int i = 0; i < resources.size(); i++)
			{
				Resource r = resources.get(i);
				if (r.isUsable())
				{
					boolean en = CityLayer.resources.get(r) >= buildingCosts.get(r);
					g.setColor(en ? Color.white : Color.red);
				}
				else
				{
					boolean en = CityLayer.resources.get(r) < buildingCosts.get(r);
					g.setColor(en ? Color.decode("#18acf1") : Color.red);
				}
				Assistant.drawResource(buildingCosts, r, x1 + 30, y1 + 100 + tooltipRows * 25 + i * 30, 25, 30, g);
			}
			
			// if (minCityLevel > 0)
			// {
			// g.setColor(CityHUDLayer.cl.city.getLevel() >= minCityLevel ? Color.white : Color.red);
			// Helper.drawString("min. Stadtlevel: " + (minCityLevel + 1), x1 + 25, y1 + 80 + tooltipRows * 25 + 40 + resources.size() * 30 + products.size() * 30, g, 25);
			// }
			
			g.setColor(c);
		}
		else
		{
			String tooltip = "Bereits maximiert.";
			int width = g.getFontMetrics(g.getFont().deriveFont(30f)).stringWidth(tooltip) + 30;
			int height = 64;
			int x1 = x;
			int y1 = y;
			
			if (x1 + width > GameFrame.getWidth()) x1 -= (x1 + width) - GameFrame.getWidth();
			if (y1 + height > GameFrame.getHeight()) y1 -= (y1 + height) - GameFrame.getHeight();
			
			Helper.drawShadow(x1, y1, g.getFontMetrics(g.getFont().deriveFont(30f)).stringWidth(tooltip) + 30, height, g);
			Helper.drawString(tooltip, x1 + 15, y1 + 40, g, 30);
		}
	}
	
	public void checkIfCanEffort()
	{
		ArrayList<Resource> resources = buildingCosts.getFilled();
		
		boolean canEffort = true;
		for (int i = 0; i < resources.size(); i++)
		{
			Resource r = resources.get(i);
			if (r.isUsable())
			{
				boolean en = CityLayer.resources.get(r) >= buildingCosts.get(r);
				if (!en) canEffort = false;
			}
			else
			{
				boolean en = CityLayer.resources.get(r) < buildingCosts.get(r);
				if (!en) canEffort = false;
			}
		}
		
		// if (CityHUDLayer.cl.city.getLevel() < minCityLevel) canEffort = false;
		
		// if (CityLayer.resources.get(Resource.BUILDINGS) >= new Center(0, 0, CityHUDLayer.cl.city.getLevel()).getScalingProducts().get(Resource.BUILDINGS)) canEffort = false;
		
		enabled = canEffort;
	}
}
