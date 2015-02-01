package de.dakror.arise.game.world;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;

import de.dakror.arise.game.Game;
import de.dakror.arise.layer.MPLayer;
import de.dakror.arise.layer.WorldHUDLayer;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet03World;
import de.dakror.arise.net.packet.Packet04City;
import de.dakror.arise.net.packet.Packet05Resources;
import de.dakror.arise.net.packet.Packet07RenameCity;
import de.dakror.arise.net.packet.Packet19Transfer;
import de.dakror.arise.net.packet.Packet20Takeover;
import de.dakror.arise.settings.CFG;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class World extends MPLayer {
	public static int CHUNKSIZE = 256;
	
	String name;
	
	int speed, id, width, height, tick, minX, minY;
	public int x, y;
	public boolean anyCityActive;
	
	long lastCheck;
	
	BufferedImage chunk;
	JSONArray citiesData;
	
	City gotoCity;
	
	Point dragStart, worldDragStart;
	
	public World(Packet03World packet) {
		try {
			id = packet.getId();
			name = packet.getName();
			speed = packet.getSpeed();
			
			minX = minY = 0;
			
			chunk = new BufferedImage(CHUNKSIZE, CHUNKSIZE, BufferedImage.TYPE_INT_ARGB);
			for (int i = 0; i < chunk.getWidth(); i += 32)
				for (int j = 0; j < chunk.getHeight(); j += 32)
					Helper.drawImage2(Game.getImage("world/ground.png"), i, j, 32, 32, 32, 0, 32, 32, (Graphics2D) chunk.getGraphics());
			
			updateSize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() {}
	
	@Override
	public void draw(Graphics2D g) {
		AffineTransform old = g.getTransform();
		AffineTransform at = g.getTransform();
		at.translate(x, y);
		g.setTransform(at);
		
		for (int i = 0; i < Math.ceil(width / (float) CHUNKSIZE); i++)
			for (int j = 0; j < Math.ceil(height / (float) CHUNKSIZE); j++)
				if (new Rectangle(0, 0, Game.getWidth(), Game.getHeight()).intersects(new Rectangle(minX + i * CHUNKSIZE + x, minY + j * CHUNKSIZE + y, CHUNKSIZE, CHUNKSIZE)))
					g.drawImage(chunk, minX + i * CHUNKSIZE, minY + j * CHUNKSIZE, null);
		
		Component hovered = null;
		
		for (Component c : components) {
			if (!new Rectangle(0, 0, Game.getWidth(), Game.getHeight()).intersects(new Rectangle(c.getX() + x, c.getY() + y, c.getWidth(), c.getHeight()))) continue;
			c.draw(g);
			if (c.state == 2) hovered = c;
		}
		
		if (hovered != null) hovered.drawTooltip(GameFrame.currentFrame.mouse.x, GameFrame.currentFrame.mouse.y, g);
		
		
		g.setTransform(old);
	}
	
	@Override
	public void update(int tick) {
		this.tick = tick;
		if (tick % 120 == 0) sortComponents();
		updateComponents(tick);
	}
	
	public void updateSize() {
		int minX = -65536, minY = -65536, maxX = -65536, maxY = -65536;
		for (Component c : components) {
			if (c.getX() < minX || minX == -65536) minX = c.getX();
			if (c.getY() < minY || minY == -65536) minY = c.getY();
			if (c.getX() + c.getWidth() > maxX || maxX == -65536) maxX = c.getX() + c.getWidth();
			if (c.getY() + c.getHeight() > maxY || maxY == -65536) maxY = c.getY() + c.getHeight();
		}
		
		minX = minX > 0 ? 0 : minX;
		minY = minY > 0 ? 0 : minY;
		
		this.minX = minX;
		this.minY = minY;
		
		width = maxX - minX;
		width = width < Game.getWidth() ? Game.getWidth() : width;
		height = maxY - minY;
		height = height < Game.getHeight() ? Game.getHeight() : height;
		
		x = x < -(width - Game.getWidth() + minX) ? -(width - Game.getWidth() + minX) : x;
		y = y < -(height - Game.getHeight() + minY) ? -(height - Game.getHeight() + minY) : y;
		x = x > -minX ? -minX : x;
		y = y > -minY ? -minY : y;
	}
	
	public void sortComponents() {
		ArrayList<Component> c = new ArrayList<>(components);
		Collections.sort(c, new Comparator<Component>() {
			@Override
			public int compare(Component o1, Component o2) {
				if (o1.getClass().equals(o2.getClass())) return 1;
				if (o1 instanceof Transfer) return -1;
				return 1;
			}
		});
		
		components = new CopyOnWriteArrayList<>(c);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getModifiers() == 4) // RMB
		{
			if (dragStart == null) {
				dragStart = e.getPoint();
				worldDragStart = new Point(x, y);
			}
			
			int x = worldDragStart.x + e.getX() - dragStart.x;
			int y = worldDragStart.y + e.getY() - dragStart.y;
			x = x < -(width - Game.getWidth() + minX) ? -(width - Game.getWidth() + minX) : x;
			y = y < -(height - Game.getHeight() + minY) ? -(height - Game.getHeight() + minY) : y;
			this.x = x > -minX ? -minX : x;
			this.y = y > -minY ? -minY : y;
			
			e.translatePoint(-x, -y);
		}
		super.mouseDragged(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		e.translatePoint(-x, -y);
		super.mousePressed(e);
		
		anyCityActive = false;
		for (Component c : components) {
			if (c instanceof City && c.state == 1) {
				anyCityActive = true;
				break;
			}
		}
		
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		e.translatePoint(-x, -y);
		super.mouseMoved(e);
		for (Component c : components) {
			if (c instanceof City && c.state == 2) {
				WorldHUDLayer.hoveredCity = (City) c;
				return;
			}
		}
		WorldHUDLayer.hoveredCity = null;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		worldDragStart = dragStart = null;
		
		e.translatePoint(-x, -y);
		super.mouseReleased(e);
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public int getId() {
		return id;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getCityCount() {
		int i = 0;
		for (Component c : components)
			if (c instanceof City) i++;
		
		return i;
	}
	
	public City getCityForId(int id) {
		for (Component c : components)
			if (c instanceof City && ((City) c).getId() == id) return (City) c;
		
		return null;
	}
	
	@Override
	public void onReceivePacket(Packet p) {
		super.onReceivePacket(p);
		
		if (p.getType() == PacketTypes.CITY) {
			Packet04City packet = (Packet04City) p;
			int middleX = (Game.getWidth() - City.SIZE) / 2;
			int middleY = (Game.getHeight() - City.SIZE) / 2;
			
			int x = middleX + packet.getX() * City.SIZE;
			int y = middleY + packet.getY() * City.SIZE;
			
			boolean found = false;
			
			for (Component c : components) {
				if (c instanceof City && c.getX() == x && c.getY() == y) {
					((City) c).setName(packet.getCityName());
					((City) c).setLevel(packet.getLevel());
					found = true;
					break;
				}
			}
			
			if (!found) {
				City c = new City(x, y, packet);
				components.add(c);
				updateSize();
				sortComponents();
			}
		}
		if (p.getType() == PacketTypes.RENAMECITY) {
			Packet07RenameCity packet = (Packet07RenameCity) p;
			for (Component c : components) {
				if (c instanceof City && ((City) c).getId() == packet.getCityId()) {
					((City) c).setName(packet.getNewName());
					break;
				}
			}
		}
		if (p.getType() == PacketTypes.RESOURCES && gotoCity != null) {
			if (gotoCity.getId() == ((Packet05Resources) p).getCityId()) {
				gotoCity.resourcePacket = (Packet05Resources) p;
				Game.currentGame.fadeTo(1, 0.05f);
			} else CFG.e("Received invalid packet05resources: current gotoCity.id=" + gotoCity.getId() + ", packet.id=" + ((Packet05Resources) p).getCityId());
		}
		
		if (p.getType() == PacketTypes.TRANSFER) {
			Packet19Transfer packet = (Packet19Transfer) p;
			
			if (packet.isMarkedForRemoval()) {
				for (Component c : components)
					if (c instanceof Transfer && ((Transfer) c).getId() == packet.getId()) components.remove(c);
				
				sortComponents();
			} else {
				components.add(new Transfer(getCityForId(packet.getCityFrom()), getCityForId(packet.getCityTo()), packet));
				sortComponents();
			}
		}
		if (p.getType() == PacketTypes.TAKEOVER) {
			Packet20Takeover packet = (Packet20Takeover) p;
			
			for (Component c : components) {
				if (c instanceof City && ((City) c).getId() == packet.getCityTakenOverId()) {
					((City) c).updateTakeoverStage(packet.getStage(), packet.getTimeleft());
					if (packet.getNewUserId() != 0) ((City) c).setUser(packet.getNewUserId(), packet.getNewUsername());
				}
			}
		}
	}
}
