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
		if (tick % 600 == 0) System.gc();
	}
}
