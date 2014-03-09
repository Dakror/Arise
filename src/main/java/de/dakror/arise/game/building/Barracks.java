package de.dakror.arise.game.building;

import java.awt.Point;

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
		
		Resources r = new Resources();
		r.add(Resource.GOLD, 20);
		r.add(Resource.WOOD, 10);
		r.add(Resource.STONE, 2);
		addGuiButton(0, 1, new Point(4, 0), "Schwertkämpfer", "Ein starker und gut gepanzerter, jedoch langsamer Nahkämpfer.", r, 0, new ClickEvent()
		{
			@Override
			public void trigger()
			{}
		});
		
		init();
	}
}
