//package de.dakror.arise.layer;
//
//import java.awt.Graphics2D;
//import java.awt.Image;
//import java.awt.Rectangle;
//import java.awt.event.MouseEvent;
//import java.awt.image.BufferedImage;
//
//import de.dakror.arise.game.Game;
//import de.dakror.arise.game.building.Barn;
//import de.dakror.arise.game.building.Building;
//import de.dakror.arise.game.building.Center;
//import de.dakror.arise.game.building.Lumberjack;
//import de.dakror.arise.game.building.Mine;
//import de.dakror.arise.game.building.Quarry;
//import de.dakror.arise.game.world.City;
//import de.dakror.arise.settings.Resources;
//import de.dakror.arise.settings.Resources.Resource;
//import de.dakror.arise.ui.ArmyLabel;
//import de.dakror.arise.ui.BuildButton;
//import de.dakror.arise.ui.ResourceLabel;
//import de.dakror.gamesetup.GameFrame;
//import de.dakror.gamesetup.layer.Confirm;
//import de.dakror.gamesetup.ui.ClickEvent;
//import de.dakror.gamesetup.ui.Component;
//import de.dakror.gamesetup.ui.InputField;
//import de.dakror.gamesetup.ui.button.IconButton;
//import de.dakror.gamesetup.util.Helper;
//
///**
// * @author Dakror
// */
//public class CityHUDLayer extends MPLayer
//{
//	public static Building selectedBuilding;
//	public static BuildButton upgrade, deconstruct;
//	public static CityLayer cl;
//	
//	public boolean anyComponentClicked;
//	
//	BufferedImage cache;
//	
//	boolean goBackToWorld;
//	
//	public CityHUDLayer(CityLayer cityLayer)
//	{
//		cl = cityLayer;
//	}
//	
//	@Override
//	public void init()
//	{
//		try
//		{
//			IconButton map = new IconButton((Game.getWidth() - 64) / 2, 20, 64, 64, "system/map.png");
//			map.addClickEvent(new ClickEvent()
//			{
//				@Override
//				public void trigger()
//				{
//					goBackToWorld = true;
//					Game.currentGame.fadeTo(1, 0.05f);
//				}
//			});
//			map.tooltip = "Weltkarte";
//			map.mode2 = true;
//			components.add(map);
//			
//			final InputField name = new InputField(Game.getWidth() / 2 + 50, 18, Game.getWidth() / 2 - 120, 48);
//			name.setMaxlength(50);
//			name.setAllowed(name.getAllowed() + " '.#~-");
//			name.setText(cl.city.getName());
//			name.drawBG = false;
//			name.onEnter = new ClickEvent()
//			{
//				@Override
//				public void trigger()
//				{
//					if (name.getText().trim().length() > 0)
//					{
//						cl.city.setName(name.getText().trim());
//						cl.saveData();
//					}
//				}
//			};
//			components.add(name);
//			
//			BuildButton lumberjack = new BuildButton(15, Game.getHeight() - 64, 48, 48, Game.getImage("system/icons.png").getSubimage(72, 0, 24, 24), new Lumberjack(0, 0, 0));
//			lumberjack.addClickEvent(new ClickEvent()
//			{
//				@Override
//				public void trigger()
//				{
//					cl.activeBuilding = new Lumberjack(0, 0, 0);
//				}
//			});
//			components.add(lumberjack);
//			
//			BuildButton mine = new BuildButton(15 + 72, Game.getHeight() - 64, 48, 48, Game.getImage("system/icons.png").getSubimage(50, 24, 24, 24), new Mine(0, 0, 0));
//			mine.addClickEvent(new ClickEvent()
//			{
//				@Override
//				public void trigger()
//				{
//					cl.activeBuilding = new Mine(0, 0, 0);
//				}
//			});
//			components.add(mine);
//			
//			BuildButton quarry = new BuildButton(15 + 144, Game.getHeight() - 64, 48, 48, Game.getImage("system/icons.png").getSubimage(24, 24, 24, 24), new Quarry(0, 0, 0));
//			quarry.addClickEvent(new ClickEvent()
//			{
//				@Override
//				public void trigger()
//				{
//					cl.activeBuilding = new Quarry(0, 0, 0);
//				}
//			});
//			components.add(quarry);
//			
//			BuildButton barracks = new BuildButton(15 + 144 + 72, Game.getHeight() - 64, 48, 48, Game.getImage("system/icons.png").getSubimage(0, 96, 24, 24), new Barracks(0, 0, 0));
//			barracks.addClickEvent(new ClickEvent()
//			{
//				@Override
//				public void trigger()
//				{
//					cl.activeBuilding = new Barracks(0, 0, 0);
//				}
//			});
//			components.add(barracks);
//			
//			BuildButton barn = new BuildButton(15 + 144 + 144, Game.getHeight() - 64, 48, 48, Game.getImage("system/icons.png").getSubimage(24, 96, 24, 24), new Barn(0, 0, 0));
//			barn.addClickEvent(new ClickEvent()
//			{
//				@Override
//				public void trigger()
//				{
//					cl.activeBuilding = new Barn(0, 0, 0);
//				}
//			});
//			// components.add(barn);
//			
//			ResourceLabel gold = new ResourceLabel(20, 20, Resource.GOLD);
//			components.add(gold);
//			
//			ResourceLabel wood = new ResourceLabel(190 + gold.getX(), 20, Resource.WOOD);
//			components.add(wood);
//			
//			ResourceLabel stone = new ResourceLabel(400 + gold.getX(), 20, Resource.STONE);
//			components.add(stone);
//			
//			ResourceLabel buildings = new ResourceLabel(gold.getX(), 60, Resource.BUILDINGS);
//			buildings.off = new Center(0, 0, cl.city.getLevel()).getScalingProducts().get(Resource.BUILDINGS);
//			components.add(buildings);
//			
//			ResourceLabel people = new ResourceLabel(wood.getX(), 60, Resource.PEOPLE);
//			people.off = 20;
//			components.add(people);
//			
//			ArmyLabel army = new ArmyLabel(stone.getX(), 60);
//			components.add(army);
//			
//			updateBuildingbar();
//			
//			upgrade = new BuildButton(-1000, -1000, 32, 32, Game.getImage("system/upgrade.png").getScaledInstance(32, 32, Image.SCALE_SMOOTH));
//			upgrade.addClickEvent(new ClickEvent()
//			{
//				@Override
//				public void trigger()
//				{
//					upgrade.state = 0;
//					
//					CityLayer.resources.add(Resources.mul(upgrade.getBuildingCosts(), -1));
//					selectedBuilding.setStageChangeTimestamp(System.currentTimeMillis() / 1000);
//					selectedBuilding.setStage(3);
//					
//					cl.saveData();
//				}
//			});
//			upgrade.mode1 = true;
//			upgrade.enabled = false;
//			components.add(upgrade);
//			
//			deconstruct = new BuildButton(-1000, -1000, 32, 32, Game.getImage("system/bomb.png").getScaledInstance(32, 32, Image.SCALE_SMOOTH));
//			deconstruct.addClickEvent(new ClickEvent()
//			{
//				@Override
//				public void trigger()
//				{
//					deconstruct.state = 0;
//					Game.currentGame.addLayer(new Confirm("Bist du sicher, dass du diese Gebäude entgültig entfernen möchtest?", new ClickEvent()
//					{
//						@Override
//						public void trigger()
//						{
//							selectedBuilding.setStageChangeTimestamp(System.currentTimeMillis() / 1000);
//							selectedBuilding.setStage(2);
//							
//							cl.saveData();
//						}
//					}, null));
//				}
//			});
//			deconstruct.mode1 = true;
//			components.add(deconstruct);
//			
//			cache = new BufferedImage(Game.getWidth(), Game.getHeight(), BufferedImage.TYPE_INT_ARGB);
//			Graphics2D g = (Graphics2D) cache.getGraphics();
//			Helper.setRenderingHints(g, true);
//			
//			Helper.drawShadow(0, 5, Game.getWidth(), 96, g);
//			Helper.drawOutline(0, 5, Game.getWidth(), 96, false, g);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
//	
//	@Override
//	public void draw(Graphics2D g)
//	{
//		g.drawImage(cache, 0, 0, null);
//		
//		if (selectedBuilding != null)
//		{
//			int width = 300, height = 200;
//			Helper.drawContainer(Game.getWidth() - width, Game.getHeight() - height, width, height, true, false, g);
//		}
//		
//		Component hovered = null;
//		for (Component c : components)
//		{
//			c.draw(g);
//			if (c.state == 2) hovered = c;
//		}
//		if (selectedBuilding != null) selectedBuilding.getGuiContainer().draw(g);
//		if (hovered != null && Game.currentGame.getActiveLayer() instanceof CityHUDLayer) hovered.drawTooltip(GameFrame.currentFrame.mouse.x, GameFrame.currentFrame.mouse.y, g);
//		
//	}
//	
//	@Override
//	public void update(int tick)
//	{
//		if (Game.world == null) return;
//		
//		if (Game.currentGame.alpha == 1 && goBackToWorld)
//		{
//			cl.saveData();
//			Game.currentGame.removeLayer(CityHUDLayer.this);
//			Game.currentGame.removeLayer(cl);
//			goBackToWorld = false;
//			Game.currentGame.fadeTo(0, 0.05f);
//		}
//		
//		if (cl.placedBuildings && tick % Game.currentGame.getUPS() == 0)
//		{
//			if (cl.updateBuildingStages()) updateBuildingbar();
//		}
//		
//		updateComponents(tick);
//		
//		upgrade.setX(selectedBuilding == null ? -1000 : Game.getWidth() - 270);
//		upgrade.setY(selectedBuilding == null ? -1000 : Game.getHeight() - 170);
//		deconstruct.setX(selectedBuilding == null ? -1000 : Game.getWidth() - 270 + 56);
//		deconstruct.setY(selectedBuilding == null ? -1000 : Game.getHeight() - 170);
//		if (selectedBuilding != null)
//		{
//			deconstruct.enabled = !(selectedBuilding instanceof Center) && selectedBuilding.getStage() == 1;
//			upgrade.enabled = selectedBuilding.getLevel() < selectedBuilding.getMaxLevel() - 1 && selectedBuilding.getStage() == 1;
//			
//			if (selectedBuilding.getStage() == 1) selectedBuilding.getGuiContainer().update(tick);
//			selectedBuilding.updateGuiButtons();
//			
//			if (selectedBuilding instanceof Center) upgrade.enabled = selectedBuilding.getLevel() < City.levels.length - 1 && selectedBuilding.getStage() == 1;
//		}
//	}
//	
//	public void updateBuildingbar()
//	{
//		Resources products = new Resources();
//		for (Component c : cl.components)
//			if (c instanceof Building && (((Building) c).getStage() == 1 || ((Building) c).getTypeId() == 1/* Centre always active */)) products.add(((Building) c).getScalingProducts());
//		
//		products = Resources.mul(products, Game.world.getSpeed());
//		
//		for (Component c : components)
//		{
//			if (c instanceof ResourceLabel)
//			{
//				if (((ResourceLabel) c).getResource().isUsable()) ((ResourceLabel) c).perHour = products.get(((ResourceLabel) c).getResource());
//				else ((ResourceLabel) c).off = products.get(((ResourceLabel) c).getResource()) / Game.world.getSpeed();
//			}
//		}
//	}
//	
//	public void timerTick()
//	{
//		cl.updateResources();
//		updateBuildingbar();
//		cl.saveData();
//	}
//	
//	@Override
//	public void mousePressed(MouseEvent e)
//	{
//		anyComponentClicked = false;
//		if (selectedBuilding != null)
//		{
//			selectedBuilding.getGuiContainer().mousePressed(e);
//			for (Component c : selectedBuilding.getGuiContainer().components)
//			{
//				if (c.state != 0)
//				{
//					anyComponentClicked = true;
//					break;
//				}
//			}
//			if (new Rectangle(Game.getWidth() - 300, Game.getHeight() - 200, 300, 200).contains(e.getPoint())) anyComponentClicked = true;
//		}
//		super.mousePressed(e);
//		
//		for (Component c : components)
//		{
//			if (c.state != 0)
//			{
//				anyComponentClicked = true;
//				break;
//			}
//		}
//	}
//	
//	@Override
//	public void mouseReleased(MouseEvent e)
//	{
//		if (selectedBuilding != null) selectedBuilding.getGuiContainer().mouseReleased(e);
//		super.mouseReleased(e);
//	}
//	
//	@Override
//	public void mouseMoved(MouseEvent e)
//	{
//		if (selectedBuilding != null) selectedBuilding.getGuiContainer().mouseMoved(e);
//		super.mouseMoved(e);
//	}
// }
