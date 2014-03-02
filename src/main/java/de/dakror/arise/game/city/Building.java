package de.dakror.arise.game.city;

import java.awt.Graphics2D;

import de.dakror.gamesetup.ui.ClickableComponent;

/**
 * @author Dakror
 */
public class Building extends ClickableComponent
{
	public static int SIZE = 128;
	
	public Building(int x, int y)
	{
		super(x, y, SIZE, SIZE);
	}
	
	@Override
	public void draw(Graphics2D g)
	{}
	
	@Override
	public void update(int tick)
	{}
}
