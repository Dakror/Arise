package de.dakror.arise.layer;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import org.json.JSONException;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.building.Building;
import de.dakror.arise.game.building.Centre;
import de.dakror.arise.game.building.Lumberjack;
import de.dakror.arise.game.building.Mine;
import de.dakror.arise.game.building.Quarry;
import de.dakror.arise.game.world.City;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.ui.BuildingButton;
import de.dakror.arise.ui.ResourceLabel;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.layer.Confirm;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.ui.InputField;
import de.dakror.gamesetup.ui.button.IconButton;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class CityHUDLayer extends Layer
{
	public static boolean allBuildingsEnabled;
	
	public boolean first;
	public boolean anyComponentClicked;
	public static Building selectedBuilding;
	
	IconButton upgrade, deconstruct;
	BufferedImage cache;
	CityLayer cl;
	
	public CityHUDLayer(CityLayer cityLayer)
	{
		first = true;
		cl = cityLayer;
	}
	
	@Override
	public void init()
	{
		try
		{
			IconButton map = new IconButton((Game.getWidth() - 64) / 2, 20, 64, 64, "system/map.png");
			map.addClickEvent(new ClickEvent()
			{
				@Override
				public void trigger()
				{
					cl.saveData();
					Game.currentGame.removeLayer(CityHUDLayer.this);
					Game.currentGame.removeLayer(cl);
					Game.world.updateWorld();
				}
			});
			map.tooltip = "Weltkarte";
			map.mode2 = true;
			components.add(map);
			
			final InputField name = new InputField(Game.getWidth() / 2 + 50, 18, Game.getWidth() / 2 - 120, 48);
			name.setMaxlength(50);
			name.setAllowed(name.getAllowed() + " '.#~-");
			name.setText(cl.city.getName());
			name.drawBG = false;
			name.onEnter = new ClickEvent()
			{
				@Override
				public void trigger()
				{
					if (name.getText().trim().length() > 0)
					{
						cl.city.setName(name.getText().trim());
						cl.saveData();
					}
				}
			};
			components.add(name);
			
			BuildingButton lumberjack = new BuildingButton(15, Game.getHeight() - 64, 48, 48, Game.getImage("system/icons.png").getSubimage(72, 0, 24, 24), new Lumberjack(0, 0, 0));
			lumberjack.addClickEvent(new ClickEvent()
			{
				@Override
				public void trigger()
				{
					cl.activeBuilding = new Lumberjack(0, 0, 0);
				}
			});
			components.add(lumberjack);
			
			BuildingButton mine = new BuildingButton(15 + 72, Game.getHeight() - 64, 48, 48, Game.getImage("system/icons.png").getSubimage(50, 24, 24, 24), new Mine(0, 0, 0));
			mine.addClickEvent(new ClickEvent()
			{
				@Override
				public void trigger()
				{
					cl.activeBuilding = new Mine(0, 0, 0);
				}
			});
			components.add(mine);
			
			BuildingButton quarry = new BuildingButton(15 + 144, Game.getHeight() - 64, 48, 48, Game.getImage("system/icons.png").getSubimage(24, 24, 24, 24), new Quarry(0, 0, 0));
			quarry.addClickEvent(new ClickEvent()
			{
				@Override
				public void trigger()
				{
					cl.activeBuilding = new Quarry(0, 0, 0);
				}
			});
			components.add(quarry);
			
			ResourceLabel wood = new ResourceLabel(20, 20, CityLayer.resources, Resource.WOOD);
			components.add(wood);
			
			ResourceLabel stone = new ResourceLabel(190 + wood.getX(), 20, CityLayer.resources, Resource.STONE);
			components.add(stone);
			
			ResourceLabel gold = new ResourceLabel(400 + wood.getX(), 20, CityLayer.resources, Resource.GOLD);
			components.add(gold);
			
			ResourceLabel buildings = new ResourceLabel(70 + wood.getX(), 60, CityLayer.resources, Resource.BUILDINGS);
			buildings.off = (cl.data.getInt("LEVEL") + 1) * City.BUILDINGS_SCALE;
			components.add(buildings);
			
			ResourceLabel people = new ResourceLabel(270 + wood.getX(), 60, CityLayer.resources, Resource.PEOPLE);
			people.off = 20;
			components.add(people);
			
			updateBuildingbar();
			
			upgrade = new IconButton(-1000, -1000, 48, 48, Game.getImage("system/upgrade.png").getScaledInstance(48, 48, Image.SCALE_SMOOTH));
			upgrade.tooltip = "Gebäude ausbauen";
			upgrade.mode2 = true;
			upgrade.enabled = false; // TODO
			components.add(upgrade);
			
			deconstruct = new IconButton(-1000, -1000, 48, 48, Game.getImage("system/bomb.png").getScaledInstance(48, 48, Image.SCALE_SMOOTH));
			deconstruct.addClickEvent(new ClickEvent()
			{
				@Override
				public void trigger()
				{
					deconstruct.state = 0;
					Game.currentGame.addLayer(new Confirm("Bist du sicher, dass du diese Gebäude entgültig entfernen möchtest?", new ClickEvent()
					{
						@Override
						public void trigger()
						{
							selectedBuilding.setStageChangeTimestamp(System.currentTimeMillis() / 1000);
							selectedBuilding.setStage(2);
							
							cl.saveData();
						}
					}, null));
				}
			});
			deconstruct.tooltip = "Gebäude abreißen";
			deconstruct.mode2 = true;
			components.add(deconstruct);
			
			cache = new BufferedImage(Game.getWidth(), Game.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) cache.getGraphics();
			Helper.setRenderingHints(g, true);
			
			Helper.drawShadow(0, 5, Game.getWidth(), 96, g);
			Helper.drawOutline(0, 5, Game.getWidth(), 96, false, g);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(cache, 0, 0, null);
		
		Component hovered = null;
		for (Component c : components)
		{
			c.draw(g);
			if (c.state == 2) hovered = c;
		}
		if (hovered != null && Game.currentGame.getActiveLayer().equals(this)) hovered.drawTooltip(GameFrame.currentFrame.mouse.x, GameFrame.currentFrame.mouse.y, g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
		
		upgrade.setX(selectedBuilding == null ? -1000 : selectedBuilding.getX() + selectedBuilding.getWidth() / 2 - 68);
		upgrade.setY(selectedBuilding == null ? -1000 : selectedBuilding.getY() + selectedBuilding.by * Building.GRID - 48);
		deconstruct.setX(selectedBuilding == null ? -1000 : selectedBuilding.getX() + selectedBuilding.getWidth() / 2 + 20);
		deconstruct.setY(selectedBuilding == null ? -1000 : selectedBuilding.getY() + selectedBuilding.by * Building.GRID - 48);
		if (selectedBuilding != null) deconstruct.enabled = !(selectedBuilding instanceof Centre) && selectedBuilding.getStage() < 3;
	}
	
	public void updateBuildingbar()
	{
		try
		{
			Resources products = new Resources();
			for (Component c : cl.components)
				if (c instanceof Building && ((Building) c).getStage() == 1) products.add(((Building) c).getProducts());
			
			for (Component c : components)
				if (c instanceof IconButton && c.getY() == Game.getHeight() - 64) c.enabled = CityLayer.resources.get(Resource.BUILDINGS) < (cl.data.getInt("LEVEL") + 1) * City.BUILDINGS_SCALE;
			
			products = Resources.mul(products, Game.world.getSpeed());
			
			for (Component c : components)
				if (c instanceof ResourceLabel) ((ResourceLabel) c).perHour = products.get(((ResourceLabel) c).getResource());
			
			allBuildingsEnabled = CityLayer.resources.get(Resource.BUILDINGS) < (cl.data.getInt("LEVEL") + 1) * City.BUILDINGS_SCALE;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		
		
		anyComponentClicked = false;
		for (Component c : components)
		{
			if (c.state != 0)
			{
				anyComponentClicked = true;
				break;
			}
		}
	}
	
}
