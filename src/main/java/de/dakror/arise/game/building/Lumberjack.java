package de.dakror.arise.game.building;


/**
 * @author Dakror
 */
public class Lumberjack extends Building
{
	public Lumberjack(int x, int y, int level)
	{
		super(x, y, 5, 6, level);
		
		typeId = 2;
		name = "Holzfäller";
		desc = "Fällt Bäume, um den Holzvorrat der Stadt zu erhöhen.";
		tx = 0;
		ty = 5;
		tw = 5;
		th = 6;
		
		by = 1;
		bh = 5;
		
		init();
	}
}
