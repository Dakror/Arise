package de.dakror.arise;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
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
import de.dakror.arise.ui.LimitLinesDocumentListener;
import de.dakror.arise.ui.MessageConsole;
import de.dakror.dakrorbin.DakrorBin;

/**
 * @author Dakror
 */
public class AriseServer
{
	public static Server server;
	
	public static JTextPane log, trafficLog;
	
	static JFrame mainFrame;
	
	public static void main(String[] args) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		mainFrame = new JFrame("Arise Server Console v");
		createTrafficFrame();
		
		DakrorBin.init(mainFrame, "Arise-Server");
		DakrorBin.showDialog = false;
		
		mainFrame.setTitle(mainFrame.getTitle() + new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(DakrorBin.buildTimestamp));
		
		mainFrame.setIconImage(ImageIO.read(AriseServer.class.getResource("/img/system/logo.png")));
		mainFrame.setSize(800, 400);
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		
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
		mainFrame.setContentPane(panel);
		
		server = new Server(args.length > 1 ? InetAddress.getByName(args[1]) : InetAddress.getLocalHost());
		
		mainFrame.addWindowListener(new WindowAdapter()
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
		
		mainFrame.setVisible(true);
	}
	
	public static void createTrafficFrame() throws IOException
	{
		JDialog frame = new JDialog(mainFrame, "Arise Server Traffic Console");
		
		frame.setIconImage(ImageIO.read(AriseServer.class.getResource("/img/system/logo.png")));
		frame.setSize(400, 400);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		trafficLog = new JTextPane();
		trafficLog.setText("");
		trafficLog.getDocument().addDocumentListener(new LimitLinesDocumentListener(100, true));
		trafficLog.setEditable(false);
		
		trafficLog.setBorder(BorderFactory.createEmptyBorder());
		trafficLog.setBackground(new JLabel().getBackground());
		frame.setContentPane(new JScrollPane(trafficLog));
		
		frame.setVisible(true);
	}
}
