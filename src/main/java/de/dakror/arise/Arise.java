package de.dakror.arise;

import javax.swing.JApplet;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.UpdateThread;

/**
 * @author Dakror
 */
public class Arise extends JApplet
{
	private static final long serialVersionUID = 1L;
	
	public static boolean running;
	
	@Override
	public void init()
	{
		super.init();
		
		running = true;
		
		new Game();
		Game.currentApplet.init(this);
		Game.currentApplet.updater = new UpdateThread();
		
		setIgnoreRepaint(true);
		
		new Thread()
		{
			@Override
			public void run()
			{
				while (running)
				{
					Game.currentApplet.main();
				}
			}
		}.start();
	}
	
	@Override
	public void stop()
	{
		running = false;
		Game.currentGame.updater.closeRequested = true;
	}
	
	public static void main(String[] args)
	{}
}
