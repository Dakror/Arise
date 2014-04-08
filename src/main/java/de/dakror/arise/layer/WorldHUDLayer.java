package de.dakror.arise.layer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.world.City;
import de.dakror.arise.layer.dialog.AttackCityDialog;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet05Resources;
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
	
	int hovId, draggedOntoId;
	boolean waitingForResources;
	
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
		
		try
		{
			if (showArrow && drag != null && hoveredCity != null)
			{
				Stroke s = g.getStroke();
				Color c = g.getColor();
				g.setStroke(new BasicStroke(12, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
				g.setColor(Color.black);
				
				int x1 = hoveredCity.getX() + Game.world.x + City.SIZE / 2, y1 = hoveredCity.getY() + Game.world.y + City.SIZE / 2;
				
				g.drawLine(x1, y1, drag.x, drag.y);
				boolean aid = draggedOnto == null ? false : draggedOnto.getUserId() == Game.userID;
				
				g.setStroke(new BasicStroke(10, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
				g.setColor(aid ? Color.decode("#0096ba") : Color.decode("#a80000"));
				g.drawLine(x1, y1, drag.x, drag.y);
				g.setColor(c);
				g.setStroke(s);
				
				double angle = Math.atan2(drag.y - y1, drag.x - x1);
				AffineTransform old = g.getTransform();
				AffineTransform at = g.getTransform();
				at.rotate(angle, drag.x, drag.y);
				g.setTransform(at);
				
				g.drawImage(Game.getImage("system/arrow" + (aid ? "Blu" : "Red") + ".png"), drag.x - 10, drag.y - 23, 24, 48, null);
				
				g.setTransform(old);
			}
		}
		catch (NullPointerException e)
		{}
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
		
		boolean ontoAny = false;
		for (Component c : Game.world.components)
		{
			if (!c.equals(hoveredCity) && !c.equals(selectedCity)) c.state = 0;
			if (c.contains(drag.x - Game.world.x, drag.y - Game.world.y) && !c.equals(hoveredCity) && !c.equals(selectedCity))
			{
				drag = new Point(c.getX() + Game.world.x + City.SIZE / 2, c.getY() + Game.world.y + City.SIZE / 2);
				draggedOnto = (City) c;
				draggedOnto.state = 2;
				ontoAny = true;
			}
		}
		
		if (!ontoAny) draggedOnto = null;
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased(e);
		
		if (draggedOnto != null)
		{
			try
			{
				waitingForResources = true;
				hovId = hoveredCity.getId();
				draggedOntoId = draggedOnto.getId();
				Game.client.sendPacket(new Packet05Resources(hoveredCity.getId()));
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			Game.currentGame.addLayer(new LoadingLayer());
		}
		
		drag = null;
		showArrow = false;
		draggedOnto = null;
	}
	
	@Override
	public void onReceivePacket(Packet p)
	{
		super.onReceivePacket(p);
		if (p.getType() == PacketTypes.RESOURCES && waitingForResources)
		{
			Packet05Resources packet = (Packet05Resources) p;
			if (packet.getCityId() == hovId)
			{
				Game.currentGame.removeLoadingLayer();
				Game.currentGame.addLayer(new AttackCityDialog(hovId, draggedOntoId, packet.getResources()));
				
				hovId = 0;
				draggedOntoId = 0;
			}
			
			waitingForResources = false;
		}
	}
}
