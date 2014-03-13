package de.dakror.arise;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;

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
	static Server server;
	
	public static void main(String[] args) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		JFrame frame = new JFrame("Arise Server Console");
		frame.setSize(500, 300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		JTextPane jtp = new JTextPane();
		frame.setContentPane(new JScrollPane(jtp));
		MessageConsole mc = new MessageConsole(jtp);
		mc.redirectOut(null, System.out);
		mc.redirectErr(Color.RED, null);
		mc.setMessageLines(100);
		
		server = new Server(args.length > 0 ? InetAddress.getByName(args[0]) : InetAddress.getLocalHost());
		
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				server.shutdown();
				CFG.p("Server closed");
			}
		});
		
		frame.setVisible(true);
	}
}
