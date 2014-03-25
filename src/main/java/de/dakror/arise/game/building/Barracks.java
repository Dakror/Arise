package de.dakror.arise.game.building;

import java.awt.Point;

import org.json.JSONException;

import de.dakror.arise.game.Game;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
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
		
		final Resources r = new Resources();
		r.add(Resource.GOLD, 20);
		r.add(Resource.WOOD, 10);
		r.add(Resource.STONE, 2);
		addGuiButton(0, 1, new Point(4, 0), "10 Schwertkämpfer", "Eine Einheit aus 10 starken und gut gepanzerten, jedoch langsamen Nahkämpfern.", r, 0, new ClickEvent()
		{
			@Override
			public void trigger()
			{
				queue(r, "S");
			}
		});
		
		final Resources r2 = new Resources();
		r2.add(Resource.GOLD, 40);
		r2.add(Resource.WOOD, 20);
		addGuiButton(1, 1, new Point(4, 1), "10 Lanzenträger", "Eine Einheit aus 10 mäßig starken und gepanzerten, jedoch schnellen Nahkämpfern.", r2, 0, new ClickEvent()
		{
			@Override
			public void trigger()
			{
				queue(r2, "L");
			}
		});
		
		init();
	}
	
	protected void queue(Resources r, String n)
	{
		// if (metadata.length() < Building.MAX_QUEUE)
		// {
		// CityLayer.resources.add(Resources.mul(r, -1));
		// if (metadata.length() == 0) setStageChangeTimestamp(System.currentTimeMillis() / 1000);
		// metadata += n;
		// CityHUDLayer.cl.saveData();
		//
		// updateQueueDisplay();
		// }
	}
	
	@Override
	protected float getStageChangeDuration()
	{
		try
		{
			return (stage != 1) ? super.getStageChangeDuration() : Math.round(Building.TROOPS.getInt(getResourceNameForTroop(getFirstPlaceInQueue()).name()) / Game.world.getSpeed());
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return 0;
		}
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
		updateQueueDisplay();
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
	
	@Override
	public void updateGuiButtons()
	{
		for (int i = 0; i < guiContainer.components.size(); i++)
			guiContainer.components.get(i).enabled = metadata.length() < Building.MAX_QUEUE;
	}
}
