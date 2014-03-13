package de.dakror.arisewebsite.game.building;

/**
 * @author Dakror
 */
public class Mine extends Building
{
	public Mine(int x, int y, int level)
	{
		super(x, y, 6, 4, level);
		typeId = 3;
		
		name = "Goldmine";
		desc = "Baut wertvolles Golderz ab, das als Währung benutzt werden kann.";
		tx = 0;
		ty = 11;
		tw = 6;
		th = 4;
		
		init();
	}
}
