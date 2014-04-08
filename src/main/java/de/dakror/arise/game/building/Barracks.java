package de.dakror.arise.game.building;

import java.awt.Point;
import java.sql.Connection;

import de.dakror.arise.game.Game;
import de.dakror.arise.layer.dialog.BuildTroopsDialog;
import de.dakror.arise.net.Server;
import de.dakror.arise.net.User;
import de.dakror.arise.net.packet.Packet16BuildingMeta;
import de.dakror.arise.server.DBManager;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.settings.TroopType;
import de.dakror.arise.ui.BuildButton;
import de.dakror.gamesetup.ui.ClickEvent;

/**
 * @author Dakror
 */
public class Barracks extends Building
{
	public Barracks(int x, int y, int level)
	{
		super(x, y, 8, 7, level);
		
		typeId = 5;
		name = "Kaserne";
		desc = "Hier können Fußsoldaten ausgebildet und verbessert werden.";
		
		tx = 6;
		ty = 9;
		tw = 8;
		th = 7;
		
		by = 4;
		bh -= 4;
		
		if (Game.client != null)
		{
			addGuiButton(0, 1, new Point(4, 0), TroopType.SWORDFIGHTER.getType().getName(), "Starke und gut gepanzerte, jedoch langsame Nahkämpfer.", TroopType.SWORDFIGHTER.getCosts(), 0, new ClickEvent()
			{
				@Override
				public void trigger()
				{
					Game.currentGame.addLayer(new BuildTroopsDialog(Barracks.this, TroopType.SWORDFIGHTER));
				}
			});
			
			addGuiButton(1, 1, new Point(4, 1), TroopType.LANCEBEARER.getType().getName(), "Mäßig starke und gepanzerte, jedoch schnelle Nahkämpfer.", TroopType.LANCEBEARER.getCosts(), 0, new ClickEvent()
			{
				@Override
				public void trigger()
				{
					Game.currentGame.addLayer(new BuildTroopsDialog(Barracks.this, TroopType.LANCEBEARER));
				}
			});
		}
		
		init();
	}
	
	@Override
	public void onSpecificChange(int cityId, User owner, Connection connection)
	{
		if (metadata.length() > 0)
		{
			String[] parts = metadata.split(":");
			if (parts.length != 2) return;
			
			if (DBManager.addCityTroops(cityId, TroopType.values()[Integer.parseInt(parts[0])], Integer.parseInt(parts[1])))
			{
				try
				{
					connection.createStatement().executeUpdate("UPDATE BUILDINGS SET META = '' WHERE ID = " + id);
					if (owner != null) Server.currentServer.sendPacket(new Packet16BuildingMeta(id, ""), owner);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	protected String getFirstPlaceInQueue()
	{
		if (metadata.length() == 0) return "";
		else return metadata.substring(0, 1);
	}
	
	protected Resource getResourceNameForTroop(String troop)
	{
		if (troop.equals("S")) return Resource.SWORDFIGHTER;
		if (troop.equals("L")) return Resource.LANCEBEARER;
		
		return null;
	}
	
	@Override
	public void setMetadata(String s)
	{
		super.setMetadata(s);
		if (Game.client != null) updateQueueDisplay();
	}
	
	protected void updateQueueDisplay()
	{
		for (int i = 0; i < guiContainer.components.size(); i++)
			guiContainer.components.get(i).enabled = metadata.length() == 0;
	}
	
	@Override
	public void updateGuiButtons()
	{
		if (metadata.length() == 0)
		{
			for (int i = 0; i < guiContainer.components.size(); i++)
				guiContainer.components.get(i).enabled = ((BuildButton) guiContainer.components.get(i)).canEffort;
		}
	}
}
