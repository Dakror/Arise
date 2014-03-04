package de.dakror.arise;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.UIManager;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.UpdateThread;

/**
 * @author Dakror
 */
public class Arise extends JApplet
{
	private static final long serialVersionUID = 1L;
	
	public static boolean wrapper = false;
	
	public static boolean running;
	
	@Override
	public void init()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
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
		
		System.gc();
	}
	
	public static void main(String[] args)
	{
		try
		{
			wrapper = true;
			
			JFrame frame = new JFrame("Arise");
			frame.setSize(1280, 720);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					Game.applet.stop();
				}
			});
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			frame.setResizable(false);
			Arise arise = new Arise();
			frame.add(arise);
			arise.setSize(1280, 720);
			frame.setSize(frame.getWidth() + (1280 - arise.getWidth()), frame.getHeight() + (720 - arise.getHeight()));
			Game.size = new Dimension(1280, 720);
			arise.init();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}
}
