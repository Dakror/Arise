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
		tx = 0;
		ty = 5;
		tw = 5;
		th = 6;
	}
}
