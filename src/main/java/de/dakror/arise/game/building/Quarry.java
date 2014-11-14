package de.dakror.arise.game.building;

/**
 * @author Dakror
 */
public class Quarry extends Building {
	public Quarry(int x, int y, int level) {
		super(x, y, 5, 6, level);
		
		typeId = 4;
		name = "Steinbruch";
		desc = "Baut Stein ab, welcher als Baumaterial benutzt werden kann.";
		
		tx = 5;
		ty = 23;
		tw = 5;
		th = 6;
		
		by = 3;
		bh -= 3;
		
		init();
	}
}
