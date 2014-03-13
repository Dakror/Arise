package de.dakror.arise.game.building;

/**
 * @author Dakror
 */
public class Barn extends Building
{
	public Barn(int x, int y, int level)
	{
		super(x, y, 7, 7, level);
		
		typeId = 6;
		name = "Reiterei";
		desc = "Hier können Berittene ausgebildet und verbessert werden.";
		
		tx = 5;
		ty = 0;
		tw = 9;
		th = 9;
		
		by = 3;
		bh -= 3;
		
		init();
	}
}
