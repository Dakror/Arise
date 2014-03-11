package de.dakror.arise.game.building;

import java.awt.Point;

import de.dakror.arise.game.Game;
import de.dakror.arise.layer.CityHUDLayer;
import de.dakror.arise.layer.CityLayer;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;
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
		addGuiButton(0, 1, new Point(4, 0), "Schwertkämpfer", "Ein starker und gut gepanzerter, jedoch langsamer Nahkämpfer.", r, 0, new ClickEvent()
		{
			@Override
			public void trigger()
			{
				boolean inrange = stage > 3 && stage - 3 < Building.MAX_QUEUE - 1;
				if (inrange)
				{
					CityLayer.resources.add(Resources.mul(r, -1));
					
					if (stageChangeTimestamp == 0) setStageChangeTimestamp(System.currentTimeMillis() / 1000);
					setStage(stage + 1);
					
					CityHUDLayer.cl.saveData();
				}
			}
		});
		
		// final Resources r2 = new Resources();
		// r2.add(Resource.GOLD, 40);
		// r2.add(Resource.WOOD, 20);
		// addGuiButton(1, 1, new Point(4, 1), "Lanzenträger", "Ein mäßig stark und gepanzerter, jedoch schneller Nahkämpfer.", r2, 0, new ClickEvent()
		// {
		// @Override
		// public void trigger()
		// {
		// CityLayer.resources.add(Resources.mul(r2, -1));
		//
		// setStageChangeTimestamp(System.currentTimeMillis() / 1000);
		// setStage(5);
		//
		// CityHUDLayer.cl.saveData();
		// }
		// });
		
		init();
	}
	
	@Override
	protected float getStageChangeDuration()
	{
		return (stage <= 3) ? super.getStageChangeDuration() : Math.round(Building.WARRIOR_BUILDTIME / Game.world.getSpeed());
	}
	
	@Override
	public void handleSpecificStageChange()
	{
		if (stage > 3 && stage - 3 < Building.MAX_QUEUE)
		{
			CityLayer.resources.add(Resource.SWORDFIGHTER, 1);
			setStage(stage - 1);
			if (stage > 3 && stage - 3 < Building.MAX_QUEUE) setStageChangeTimestamp(System.currentTimeMillis() / 1000);
			CityHUDLayer.cl.saveData();
		}
		
		
		// if (stage == 5)
		// {
		// CityLayer.resources.add(Resource.LANCEBEARER, 1);
		// setStage(1);
		// CityHUDLayer.cl.saveData();
		// }
	}
	
	@Override
	public void updateGuiButtons()
	{
		guiContainer.components.get(0).enabled = stage > 3 && stage - 3 < Building.MAX_QUEUE;
	}
}
