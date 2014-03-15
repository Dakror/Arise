package de.dakror.arise;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import de.dakror.arise.net.Server;
import de.dakror.arise.settings.CFG;
import de.dakror.arise.ui.MessageConsole;

/**
 * @author Dakror
 */
public class AriseServer
{
	public static Server server;
	
	public static void main(String[] args) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		JFrame frame = new JFrame("Arise Server Console");
		frame.setIconImage(ImageIO.read(AriseServer.class.getResource("/img/system/logo.png")));
		frame.setSize(800, 400);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		JTextPane jtp = new JTextPane();
		frame.setContentPane(new JScrollPane(jtp));
		MessageConsole mc = new MessageConsole(jtp);
		mc.redirectOut();
		mc.redirectErr(Color.RED, null);
		mc.setMessageLines(100);
		
		server = new Server(args.length > 0 ? InetAddress.getByName(args[0]) : InetAddress.getLocalHost());
		
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				if (server.running)
				{
					CFG.p("Closing server");
					server.shutdown();
				}
				else System.exit(0);
			}
		});
		
		frame.setVisible(true);
	}
}
