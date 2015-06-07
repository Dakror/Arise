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


package de.dakror.arise.game.world;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;

import de.dakror.arise.game.Game;
import de.dakror.arise.layer.WorldHUDLayer;
import de.dakror.arise.net.packet.Packet19Transfer;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.settings.TransferType;
import de.dakror.arise.util.Assistant;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class Transfer extends ClickableComponent {
	Area arrow;
	TransferType type;
	Resources value;
	int id, timeleft, distance;
	double angle;
	City from, to;
	
	public Transfer(City from, City to, Packet19Transfer data) {
		super(from.x + City.SIZE / 2, from.y + City.SIZE / 2, Math.abs(to.x - from.x), Math.abs(to.y - from.y));
		
		this.from = from;
		this.to = to;
		id = data.getId();
		type = data.getTransferType();
		value = data.getValue();
		timeleft = data.getTimeleft();
		
		angle = Math.atan2(to.y - from.y, to.x - from.x);
		distance = (int) (Math.sqrt(Math.pow((to.x + City.SIZE / 2) - (from.x + City.SIZE / 2), 2) + Math.pow((to.y + City.SIZE / 2) - (from.y + City.SIZE / 2), 2)) - City.SIZE / 4
				* Math.sqrt(2));
		Polygon polygon = new Polygon();
		
		if (distance >= 0) {
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
		arrow.subtract(new Area(new Rectangle(x1 - 32, y1 - 32, 64, 64)));
	}
	
	@Override
	public void draw(Graphics2D g) {
		try {
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
		} catch (NullPointerException e) {}
	}
	
	@Override
	public void update(int tick) {
		if (timeleft > 0 && tick % Game.currentGame.getUPS() == 0) timeleft--;
	}
	
	public City getCityFrom() {
		return from;
	}
	
	public City getCityTo() {
		return to;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g) {
		if (!(Game.currentGame.getActiveLayer() instanceof WorldHUDLayer)) return;
		
		String tooltip = type.getDescription();
		String timer = "Dauer: " + Assistant.formatSeconds(timeleft);
		int width = g.getFontMetrics(g.getFont().deriveFont(30f)).stringWidth(tooltip) + 35;
		int w2 = g.getFontMetrics(g.getFont().deriveFont(27f)).stringWidth(timer) + 35;
		width = w2 > width ? w2 : width;
		
		ArrayList<Resource> filled = value.getFilled();
		int height = 90 + filled.size() * 30;
		int x1 = x;
		int y1 = y - 80;
		
		if (x1 + width > Game.getWidth()) x1 -= (x1 + width) - GameFrame.getWidth();
		if (y1 + height > Game.getHeight()) y1 -= (y1 + height) - GameFrame.getHeight();
		
		Helper.drawShadow(x1, y1, width, height, g);
		Helper.drawString(tooltip, x1 + 15, y1 + 40, g, 30);
		Helper.drawString(timer, x1 + 20, y1 + 70, g, 27);
		for (int i = 0; i < filled.size(); i++) {
			Assistant.drawResource(value, filled.get(i), x1 + 20, y1 + 80 + i * 30, 25, 30, g);
		}
	}
	
	@Override
	public boolean contains(int x, int y) {
		return arrow.contains(x, y);
	}
}
