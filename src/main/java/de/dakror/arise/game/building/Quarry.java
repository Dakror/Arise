package de.dakror.arise.game.building;

/**
 * @author Dakror
 */
public class Quarry extends Building
{
	public Quarry(int x, int y, int level)
	{
		super(x, y, 5, 6, level);
		
		typeId = 4;
		name = "Steinbruch";
		desc = "Baut Stein ab, um ihn als Baumaterial zu benutzen.";
		
		tx = 7;
		ty = 21;
		tw = 5;
		th = 6;
		
		by = 3;
		bh -= 3;
		
		init();
	}
}
