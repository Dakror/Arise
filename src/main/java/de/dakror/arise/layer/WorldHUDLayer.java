package de.dakror.arise.layer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.world.City;
import de.dakror.gamesetup.ui.Component;

/**
 * @author Dakror
 */
public class WorldHUDLayer extends MPLayer
{
	public static City selectedCity;
	public static City hoveredCity;
	
	boolean showArrow;
	Point drag;
	City draggedOnto;
	
	@Override
	public void init()
	{}
	
	@Override
	public void draw(Graphics2D g)
	{
		// if (selectedCity != null)
		// {
		// int width = 300, height = 200;
		// Helper.drawContainer(Game.getWidth() - width, Game.getHeight() - height, width, height, true, false, g);
		// }
		
		if (showArrow && drag != null && hoveredCity != null)
		{
			Stroke s = g.getStroke();
			Color c = g.getColor();
			g.setStroke(new BasicStroke(10, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
			g.setColor(draggedOnto == null ? Color.decode("#a80000") : (draggedOnto.getUserId() == Game.userID ? Color.decode("#25c5ed") : Color.decode("#a80000")));
			g.drawLine(hoveredCity.getX() + Game.world.x + City.SIZE / 2, hoveredCity.getY() + Game.world.y + City.SIZE / 2, drag.x, drag.y);
			g.setColor(c);
			g.setStroke(s);
		}
	}
	
	@Override
	public void update(int tick)
	{
		if (!Game.world.anyCityActive) selectedCity = null;
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		super.mouseDragged(e);
		showArrow = hoveredCity != null && hoveredCity.getUserId() == Game.userID && e.getModifiers() == 16; // LMB
		drag = e.getPoint();
		
		for (Component c : Game.world.components)
		{
			if (!c.equals(hoveredCity)) c.state = 0;
			if (c.contains(drag.x - Game.world.x, drag.y - Game.world.y) && !c.equals(hoveredCity))
			{
				drag = new Point(c.getX() + Game.world.x + City.SIZE / 2, c.getY() + Game.world.y + City.SIZE / 2);
				draggedOnto = (City) c;
				draggedOnto.state = 2;
			}
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased(e);
		drag = null;
		showArrow = false;
		draggedOnto = null;
	}
}
