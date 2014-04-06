package de.dakror.arise.layer;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.building.Barracks;
import de.dakror.arise.net.packet.Packet15BarracksBuildTroop;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.settings.TroopType;
import de.dakror.arise.util.Assistant;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.ui.Slider;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class BuildTroopsLayer extends MPLayer
{
	Barracks barracks;
	TroopType type;
	
	Slider slider;
	
	int width, height;
	
	public BuildTroopsLayer(Barracks barracks, TroopType troopType)
	{
		this.barracks = barracks;
		type = troopType;
		modal = true;
		
		width = 20 + TextButton.WIDTH * 2;
		height = 300;
	}
	
	@Override
	public void init()
	{
		int div = (int) Math.floor(Resources.div(CityLayer.resources, type.getCosts()));
		slider = new Slider((Game.getWidth() - width) / 2 + 20, (Game.getHeight() + height) / 2 - 40 - TextButton.HEIGHT, width - 40, 1, div, 1, true);
		slider.setTitle("10er Einheiten");
		components.add(slider);
		
		TextButton abort = new TextButton((Game.getWidth() - width) / 2 + 10, (Game.getHeight() + height) / 2 - TextButton.HEIGHT - 10, "Abbruch");
		abort.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentGame.removeLayer(BuildTroopsLayer.this);
			}
		});
		components.add(abort);
		TextButton build = new TextButton(Game.getWidth() / 2, (Game.getHeight() + height) / 2 - TextButton.HEIGHT - 10, "Bauen");
		build.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				try
				{
					Game.client.sendPacket(new Packet15BarracksBuildTroop(CityHUDLayer.cl.city.getId(), barracks.getId(), type, (int) slider.getValue()));
					Game.currentGame.removeLayer(BuildTroopsLayer.this);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		components.add(build);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Helper.drawContainer((Game.getWidth() - width) / 2, (Game.getHeight() - height) / 2, width, height, false, false, g);
		Helper.drawHorizontallyCenteredString(type.getType().getName(), Game.getWidth(), (Game.getHeight() - height) / 2 + 40, g, 35);
		
		ArrayList<Resource> filled = type.getCosts().getFilled();
		
		for (int i = 0; i < filled.size(); i++)
			Assistant.drawLabelWithIcon(Game.getWidth() / 2 - width / 3, (Game.getHeight() - height) / 2 + 55 + i * 40, 30, new Point(filled.get(i).getIconX(), filled.get(i).getIconY()), "" + type.getCosts().get(filled.get(i)) * (int) slider.getValue(), 30, g);
		
		Helper.drawString("Dauer: " + Assistant.formatSeconds((int) ((type.getBuildTime() / (float) Game.world.getSpeed()) * slider.getValue())), Game.getWidth() / 2, Game.getHeight() / 2 - 30, g, 30);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		for (Component c : barracks.getGuiContainer().components)
			c.state = 0; // to remove tooltips
		
		updateComponents(tick);
	}
}
