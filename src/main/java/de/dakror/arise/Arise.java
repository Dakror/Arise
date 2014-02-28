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
	
	@Override
	public void init()
	{
		super.init();
		
		new Game();
		Game.currentApplet.init(this);
		Game.currentApplet.updater = new UpdateThread();
		
		setIgnoreRepaint(true);
		
		new Thread()
		{
			@Override
			public void run()
			{
				while (true)
				{
					Game.currentApplet.main();
				}
			}
		}.start();
	}
	
	public static void main(String[] args)
	{}
}
