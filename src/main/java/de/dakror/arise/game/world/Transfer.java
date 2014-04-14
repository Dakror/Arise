package de.dakror.arise.game.world;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

import de.dakror.arise.net.packet.Packet19Transfer;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.TransferType;
import de.dakror.gamesetup.ui.ClickableComponent;

/**
 * @author Dakror
 */
public class Transfer extends ClickableComponent
{
	Area arrow;
	TransferType type;
	Resources value;
	int id, timeleft, distance;
	double angle;
	City from, to;
	
	public Transfer(City from, City to, Packet19Transfer data)
	{
		super(from.x + City.SIZE / 2, from.y + City.SIZE / 2, Math.abs(to.x - from.x), Math.abs(to.y - from.y));
		
		this.from = from;
		this.to = to;
		id = data.getId();
		type = data.getTransferType();
		value = data.getValue();
		timeleft = data.getTimeleft();
		
		angle = Math.atan2(to.y - from.y, to.x - from.x);
		distance = (int) Math.sqrt(Math.pow((to.x + City.SIZE / 2) - (from.x + City.SIZE / 2), 2) + Math.pow((to.y + City.SIZE / 2) - (from.y + City.SIZE / 2), 2));
		Polygon polygon = new Polygon();
		
		if (distance >= 0)
		{
			polygon.addPoint(0, -6);
			polygon.addPoint(0, 6);
			polygon.addPoint(distance - 24, 6);
		}
		polygon.addPoint(distance - 24, 18);
		polygon.addPoint(distance, 0);
		polygon.addPoint(distance - 24, -18);
		if (distance >= 0) polygon.addPoint(distance - 24, -6);
		
		int x1 = from.getX() + City.SIZE / 2, y1 = from.getY() + City.SIZE / 2;
		AffineTransform at = new AffineTransform();
		at.rotate(angle, x1, y1);
		at.translate(x1, y1);
		arrow = new Area(polygon);
		arrow.transform(at);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		try
		{
			Stroke s = g.getStroke();
			Color c = g.getColor();
			Composite cs = g.getComposite();
			
			g.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
			g.setColor(Color.black);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, state == 0 ? 0.5f : 1));
			
			g.draw(arrow);
			
			g.setStroke(s);
			g.setColor(type.getColor());
			
			g.fill(arrow);
			
			g.setColor(c);
			g.setComposite(cs);
		}
		catch (NullPointerException e)
		{}
	}
	
	@Override
	public void update(int tick)
	{
		// if (timeleft > 0) timeleft--;
	}
	
	public City getCityFrom()
	{
		return from;
	}
	
	public City getCityTo()
	{
		return to;
	}
	
	@Override
	public boolean contains(int x, int y)
	{
		return arrow.contains(x, y);
	}
}
