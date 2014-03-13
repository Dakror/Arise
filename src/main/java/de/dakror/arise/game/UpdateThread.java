package de.dakror.arise.game;

import de.dakror.gamesetup.Updater;

/**
 * @author Dakror
 */
public class UpdateThread extends Updater
{
	@Override
	public void update()
	{
		if (tick % 60 == 0)
		{
			Game.currentGame.usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			System.gc();
		}
	}
}
