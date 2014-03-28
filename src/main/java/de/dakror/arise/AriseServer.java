package de.dakror.arise;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import de.dakror.arise.net.Server;
import de.dakror.arise.server.CommandHandler;
import de.dakror.arise.settings.CFG;
import de.dakror.arise.ui.MessageConsole;
import de.dakror.dakrorbin.DakrorBin;

/**
 * @author Dakror
 */
public class AriseServer
{
	public static Server server;
	
	public static JTextPane log;
	
	public static void main(String[] args) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		JFrame frame = new JFrame("Arise Server Console v");
		
		DakrorBin.init(frame, "Arise-Server");
		DakrorBin.showDialog = false;
		
		frame.setTitle(frame.getTitle() + new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(DakrorBin.buildTimestamp));
		
		frame.setIconImage(ImageIO.read(AriseServer.class.getResource("/img/system/logo" + (System.getProperty("os.name").contains("win") ? "" : "HD") + ".png")));
		frame.setSize(800, 400);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel(new BorderLayout());
		log = new JTextPane();
		log.setEditable(false);
		MessageConsole mc = new MessageConsole(log);
		mc.redirectOut();
		mc.redirectErr(Color.RED, null);
		mc.setMessageLines(100);
		log.setBorder(BorderFactory.createEmptyBorder());
		log.setBackground(new JLabel().getBackground());
		panel.add(new JScrollPane(log), BorderLayout.CENTER);
		
		final JTextField cmd = new JTextField();
		cmd.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER && cmd.getText().trim().length() > 0)
				{
					CommandHandler.handle(cmd.getText());
					cmd.setText("");
				}
			}
		});
		panel.add(cmd, BorderLayout.PAGE_END);
		frame.setContentPane(panel);
		
		server = new Server(args.length > 1 ? InetAddress.getByName(args[1]) : InetAddress.getLocalHost());
		
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
