package de.dakror.arise;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.UpdateThread;
import de.dakror.gamesetup.applet.DoubleBufferApplet;

/**
 * @author Dakror
 */
public class Arise extends DoubleBufferApplet
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init()
	{
		super.init();
		
		new Game();
		Game.currentApplet.init(this);
		Game.currentApplet.updater = new UpdateThread();
		
		new Thread()
		{
			@Override
			public void run()
			{
				while (true)
				{
					repaint();
				}
			}
		}.start();
	}
	
	public static void main(String[] args)
	{}
}
