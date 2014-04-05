package de.dakror.arise.game.building;

import java.awt.Point;

import de.dakror.arise.game.Game;
import de.dakror.arise.layer.BuildTroopsLayer;
import de.dakror.arise.settings.Resources;
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
			addGuiButton(0, 1, new Point(4, 0), TroopType.SWORDFIGHTER.getType().getName(), "Starke und gut gepanzerte, jedoch langsame Nahkämpfer.", new Resources(), 0, new ClickEvent()
			{
				@Override
				public void trigger()
				{
					Game.currentGame.addLayer(new BuildTroopsLayer(Barracks.this, TroopType.SWORDFIGHTER));
				}
			});
			
			addGuiButton(1, 1, new Point(4, 1), TroopType.LANCEBEARER.getType().getName(), "Mäßig starke und gepanzerte, jedoch schnelle Nahkämpfer.", new Resources(), 0, new ClickEvent()
			{
				@Override
				public void trigger()
				{
					Game.currentGame.addLayer(new BuildTroopsLayer(Barracks.this, TroopType.LANCEBEARER));
				}
			});
		}
		
		init();
	}
	
	@Override
	public void onSpecificChange()
	{
		// if (stage == 1)
		// {
		// Resource r = getResourceNameForTroop(getFirstPlaceInQueue());
		// if (r != null)
		// {
		// metadata = (metadata.length() > 1 ? metadata.substring(1) : "");
		// CityLayer.resources.add(r, 10);
		//
		// if (metadata.length() > 0) setStageChangeTimestamp(System.currentTimeMillis() / 1000);
		//
		// CityHUDLayer.cl.saveData();
		//
		// updateQueueDisplay();
		// }
		// }
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
		int S = 0, L = 0;
		for (int i = 0; i < metadata.length(); i++)
		{
			if (metadata.charAt(i) == 'S') S++;
			if (metadata.charAt(i) == 'L') L++;
		}
		((BuildButton) guiContainer.components.get(0)).number = S;
		((BuildButton) guiContainer.components.get(1)).number = L;
	}
}
