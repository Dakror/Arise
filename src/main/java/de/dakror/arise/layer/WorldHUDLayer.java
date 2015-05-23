/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
 

package de.dakror.arise.layer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.world.City;
import de.dakror.arise.game.world.Transfer;
import de.dakror.arise.layer.dialog.AttackCityDialog;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet05Resources;
import de.dakror.arise.settings.TransferType;
import de.dakror.gamesetup.ui.Component;

/**
 * @author Dakror
 */
public class WorldHUDLayer extends MPLayer {
	public static City selectedCity;
	public static City hoveredCity;
	
	boolean showArrow;
	Point drag;
	City draggedOnto;
	TransferType dragType;
	
	int hovId, draggedOntoId;
	boolean waitingForResources;
	
	@Override
	public void init() {
		dragType = TransferType.TROOPS_ATTACK;
	}
	
	@Override
	public void draw(Graphics2D g) {
		// if (selectedCity != null)
		// {
		// int width = 300, height = 200;
		// Helper.drawContainer(Game.getWidth() - width, Game.getHeight() - height, width, height, true, false, g);
		// }
		
		try {
			if (showArrow && drag != null && hoveredCity != null) {
				Stroke s = g.getStroke();
				Color c = g.getColor();
				g.setStroke(new BasicStroke(12, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
				g.setColor(Color.black);
				
				int x1 = hoveredCity.getX() + Game.world.x + City.SIZE / 2, y1 = hoveredCity.getY() + Game.world.y + City.SIZE / 2;
				
				// g.drawLine(x1, y1, drag.x, drag.y);
				//
				// g.setStroke(new BasicStroke(10, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
				// g.setColor(dragType.getColor());
				// g.drawLine(x1, y1, drag.x, drag.y);
				
				g.setStroke(s);
				
				double angle = Math.atan2(drag.y - y1, drag.x - x1);
				int length = (int) (Math.sqrt(Math.pow((drag.x - x1), 2) + Math.pow((drag.y - y1), 2))) - 24;
				AffineTransform old = g.getTransform();
				AffineTransform at = g.getTransform();
				at.rotate(angle, x1, y1);
				at.translate(x1, y1);
				
				g.setTransform(at);
				Polygon arrow = new Polygon();
				if (length >= 0) {
					arrow.addPoint(0, -6);
					arrow.addPoint(0, 6);
					arrow.addPoint(length, 6);
				}
				arrow.addPoint(length, 18);
				arrow.addPoint(length + 24, 0);
				arrow.addPoint(length, -18);
				if (length >= 0) arrow.addPoint(length, -6);
				g.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
				g.setColor(Color.black);
				g.draw(arrow);
				g.setStroke(s);
				g.setColor(dragType.getColor());
				g.fill(arrow);
				
				g.setColor(c);
				g.setTransform(old);
			}
		} catch (NullPointerException e) {}
	}
	
	@Override
	public void update(int tick) {
		if (!Game.world.anyCityActive) selectedCity = null;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		
		selectedCity = null;
		
		showArrow = hoveredCity != null && hoveredCity.getUserId() == Game.userID && e.getModifiers() == 16; // LMB
		if (showArrow) {
			drag = e.getPoint();
			
			boolean ontoAny = false;
			for (Component c : Game.world.components) {
				if (c instanceof City) {
					if (!c.equals(hoveredCity) && !c.equals(selectedCity)) c.state = 0;
					if (c.contains(drag.x - Game.world.x, drag.y - Game.world.y) && !c.equals(hoveredCity) && !((City) c).isInTakeoverCooldown()) {
						boolean canTarget = true;
						
						for (Component c1 : Game.world.components) {
							if (c1 instanceof Transfer && ((Transfer) c1).getCityFrom().equals(hoveredCity) && ((Transfer) c1).getCityTo().equals(c)) {
								canTarget = false;
								break;
							}
						}
						
						if (!canTarget) continue;
						
						drag = new Point(c.getX() + Game.world.x + City.SIZE / 2, c.getY() + Game.world.y + City.SIZE / 2);
						draggedOnto = (City) c;
						draggedOnto.state = 2;
						ontoAny = true;
					}
				}
			}
			
			if (!ontoAny) draggedOnto = null;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		
		if (draggedOnto != null) {
			try {
				waitingForResources = true;
				hovId = hoveredCity.getId();
				draggedOntoId = draggedOnto.getId();
				Game.client.sendPacket(new Packet05Resources(hoveredCity.getId()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Game.currentGame.addLayer(new LoadingLayer());
		}
		
		drag = null;
		showArrow = false;
		draggedOnto = null;
	}
	
	@Override
	public void onReceivePacket(Packet p) {
		super.onReceivePacket(p);
		if (p.getType() == PacketTypes.RESOURCES && waitingForResources) {
			Packet05Resources packet = (Packet05Resources) p;
			if (packet.getCityId() == hovId) {
				Game.currentGame.removeLoadingLayer();
				Game.currentGame.addLayer(new AttackCityDialog(hovId, draggedOntoId, packet.getResources()));
				
				hovId = 0;
				draggedOntoId = 0;
			}
			
			waitingForResources = false;
		}
	}
}
