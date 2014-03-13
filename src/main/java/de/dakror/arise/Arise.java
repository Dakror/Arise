package de.dakror.arise;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.UpdateThread;
import de.dakror.gamesetup.util.Helper;

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
			if (args.length > 0 && args[0].equals("-lan")) Game.inLan = true;
			
			File jar = new File(Arise.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			
			long time = Long.parseLong(Helper.getURLContent(new URL("http://dakror.de/arise/bin/version")).trim());
			
			wrapper = true;
			
			JFrame frame = new JFrame("Arise Standalone v");
			frame.setIconImage(ImageIO.read(Arise.class.getResource("/img/system/logo.png")));
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
			
			frame.setTitle(frame.getTitle() + new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(Game.buildTimestamp));
			
			if (Game.buildTimestamp > 0 && time - Game.buildTimestamp > 60000)
			{
				JOptionPane.showMessageDialog(frame, "Es ist eine neue Version von Arise verfügbar.\nDiese wird nun heruntergeladen.", "Update", JOptionPane.INFORMATION_MESSAGE);
				File updater = new File(System.getProperty("user.home") + "/.dakror/SelfUpdate/SelfUpdate.jar");
				updater.getParentFile().mkdirs();
				if (!updater.exists()) Helper.copyInputStream(Arise.class.getResourceAsStream("/SelfUpdate.jar"), new FileOutputStream(updater));
				Runtime.getRuntime().exec("javaw -jar \"" + updater.getPath() + "\" \"" + jar.getPath() + "\" \"http://dakror.de/arise/bin/Arise.jar\"");
				System.exit(0);
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}
}
