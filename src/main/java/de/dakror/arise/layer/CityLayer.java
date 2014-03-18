package de.dakror.arise.layer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.building.Building;
import de.dakror.arise.game.world.City;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet06Building;
import de.dakror.arise.settings.Resources;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class CityLayer extends MPLayer
{
	public static Resources resources;
	public City city;
	
	Building activeBuilding;
	
	boolean placedBuildings;
	
	public CityLayer(City city)
	{
		modal = true;
		this.city = city;
		resources = city.resourcePacket.getResources();
	}
	
	@Override
	public void init()
	{
		placedBuildings = false;
		
		try
		{
			// data = new JSONObject(Helper.getURLContent(new URL("http://dakror.de/arise/city?userid=" + Game.userID + "&worldid=" + Game.worldID + "&id=" + city.getId())));
			// resources = new Resources();
			// resources.set(Resource.WOOD, (float) data.getDouble("WOOD"));
			// resources.set(Resource.GOLD, (float) data.getDouble("GOLD"));
			// resources.set(Resource.STONE, (float) data.getDouble("STONE"));
			//
			// String a = data.getString("ARMY");
			// if (a.trim().length() > 0)
			// {
			// String[] army = a.split(":");
			// for (int i = 0; i < army.length; i++)
			// resources.set(ArmyLabel.ARMY[i], Integer.parseInt(army[i]));
			// }
			//
			// placeBuildings();
			// updateBuildingStages();
			// saveData();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("system/city.png"), 0, 0, null);
		
		Component hovered = null;
		for (Component c : components)
		{
			c.draw(g);
			if (c.state == 2) hovered = c;
		}
		
		if (activeBuilding != null)
		{
			int x = Helper.round(Game.currentGame.mouse.x - activeBuilding.getWidth() / 2, Building.GRID);
			int y = Helper.round(Game.currentGame.mouse.y - activeBuilding.getHeight() / 2, Building.GRID);
			
			AffineTransform old = g.getTransform();
			AffineTransform at = g.getTransform();
			at.translate(x, y);
			g.setTransform(at);
			
			activeBuilding.setStage(1);
			activeBuilding.draw(g);
			
			g.setTransform(old);
			
			Composite c = g.getComposite();
			Color cl = g.getColor();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
			
			boolean free = true;
			
			for (int i = activeBuilding.bx * Building.GRID; i < activeBuilding.bx * Building.GRID + activeBuilding.bw * Building.GRID; i += Building.GRID)
			{
				for (int j = activeBuilding.by * Building.GRID; j < activeBuilding.by * Building.GRID + activeBuilding.bh * Building.GRID; j += Building.GRID)
				{
					boolean green = new Rectangle(96, 96, 1088, 544).contains(new Rectangle(i + x, j + y, Building.GRID, Building.GRID)) && !intersectsBuildings(new Rectangle(i + x, j + y, Building.GRID, Building.GRID));
					g.setColor(green ? Color.decode("#5fff5b") : Color.red);
					g.fillRect(i + x, j + y, Building.GRID, Building.GRID);
					
					if (!green) free = false;
				}
			}
			
			if (free) Game.applet.setCursor(Cursor.getDefaultCursor());
			else Game.applet.setCursor(DragSource.DefaultMoveNoDrop);
			
			g.setColor(Color.black);
			for (int i = 0; i < 1088; i += Building.GRID)
				for (int j = 0; j < 544; j += Building.GRID)
					g.drawRect(i + 96, j + 96, Building.GRID, Building.GRID);
			
			g.setComposite(c);
			g.setColor(cl);
		}
		
		if (hovered != null) hovered.drawTooltip(GameFrame.currentFrame.mouse.x, GameFrame.currentFrame.mouse.y, g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
	}
	
	@Override
	public void onReceivePacket(Packet p)
	{
		super.onReceivePacket(p);
		
		if (p.getType() == PacketTypes.BUILDING)
		{
			Packet06Building packet = (Packet06Building) p;
			Building b = Building.getBuildingByTypeId(packet.getX(), packet.getY(), packet.getLevel(), packet.getBuildingType());
			b.setStage(packet.getStage());
			b.setStageChangeSecondsLeft(packet.getTimeleft());
			b.setMetadata(packet.getMeta());
			
			components.add(b);
			
			sortComponents();
		}
	}
	
	public boolean intersectsBuildings(Rectangle r)
	{
		for (Component c : components)
			if (c instanceof Building && new Rectangle(c.getX() + ((Building) c).bx * Building.GRID, c.getY() + ((Building) c).by * Building.GRID, ((Building) c).bw * Building.GRID, ((Building) c).bh * Building.GRID).intersects(r)) return true;
		
		return false;
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		
		// boolean anyBuildingActive = false;
		// for (Component c : components)
		// {
		// if (c instanceof Building && c.state == 1)
		// {
		// anyBuildingActive = true;
		// break;
		// }
		// }
		//
		// CityHUDLayer chl = null;
		//
		// if (!anyBuildingActive)
		// {
		// for (Layer l : Game.currentGame.layers)
		// if (l instanceof CityHUDLayer)
		// {
		// chl = (CityHUDLayer) l;
		// break;
		// }
		// if (!chl.anyComponentClicked) CityHUDLayer.selectedBuilding = null;
		// }
		//
		// if (activeBuilding != null)
		// {
		// if (e.getButton() == MouseEvent.BUTTON1 && Game.applet.getCursor().equals(Cursor.getDefaultCursor()))
		// {
		// int x = Helper.round(Game.currentGame.mouse.x - activeBuilding.getWidth() / 2, Building.GRID);
		// int y = Helper.round(Game.currentGame.mouse.y - activeBuilding.getHeight() / 2, Building.GRID);
		//
		// Building b = Building.getBuildingByTypeId(x / 32, y / 32, 0, activeBuilding.getTypeId());
		// b.setStageChangeTimestamp(System.currentTimeMillis() / 1000);
		// components.add(b);
		// resources.add(Resource.BUILDINGS, 1);
		// resources.add(Resources.mul(activeBuilding.getBuildingCosts(), -1));
		// activeBuilding = null;
		// chl.updateBuildingbar();
		// sortComponents();
		// saveData();
		// }
		// if (e.getButton() == MouseEvent.BUTTON3)
		// {
		// activeBuilding = null;
		// Game.applet.setCursor(Cursor.getDefaultCursor());
		// }
		// }
	}
	
	public void sortComponents()
	{
		ArrayList<Component> c = new ArrayList<>(components);
		Collections.sort(c, new Comparator<Component>()
		{
			@Override
			public int compare(Component o1, Component o2)
			{
				return Integer.compare(o1.getY() + ((Building) o1).by * Building.GRID, o2.getY() + ((Building) o2).by * Building.GRID);
			}
		});
		
		components = new CopyOnWriteArrayList<>(c);
	}
}
