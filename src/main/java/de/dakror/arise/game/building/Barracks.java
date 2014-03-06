package de.dakror.arise.game.building;

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
		
		init();
	}
}
