package de.dakror.arise.game.world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import de.dakror.arise.game.Game;
import de.dakror.arise.net.packet.Packet19Transfer;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.TransferType;
import de.dakror.gamesetup.ui.ClickableComponent;

/**
 * @author Dakror
 */
public class Transfer extends ClickableComponent
{
	Polygon polygon;
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
		distance = (int) Math.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.y - from.y, 2));
		
		polygon = new Polygon();
		
		if (distance >= 0)
		{
			polygon.addPoint(0, -6);
			polygon.addPoint(0, 6);
			polygon.addPoint(distance, 6);
		}
		polygon.addPoint(distance, 18);
		polygon.addPoint(distance + 24, 0);
		polygon.addPoint(distance, -18);
		if (distance >= 0) polygon.addPoint(distance, -6);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		int x1 = from.getX() + City.SIZE / 2, y1 = from.getY() + City.SIZE / 2;
		
		AffineTransform old = g.getTransform();
		AffineTransform at = g.getTransform();
		at.rotate(angle, x1, y1);
		at.translate(x1 + Game.world.x, y1 + Game.world.y);
		
		Stroke s = g.getStroke();
		Color c = g.getColor();
		
		g.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
		g.setColor(Color.black);
		
		g.draw(polygon);
		
		g.setStroke(s);
		g.setColor(type.getColor());
		
		g.fill(polygon);
		
		g.setColor(c);
		g.setTransform(at);
		g.setTransform(old);
	}
	
	@Override
	public void update(int tick)
	{
		// if (timeleft > 0) timeleft--;
	}
	
	@Override
	public boolean contains(int x, int y)
	{
		return polygon.contains(x, y);
	}
}
