package de.dakror.arise;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import de.dakror.arise.game.Game;
import de.dakror.arise.net.Server;
import de.dakror.arise.server.CommandHandler;
import de.dakror.arise.settings.CFG;
import de.dakror.arise.ui.LimitLinesDocumentListener;
import de.dakror.arise.util.ErrorOutputStream;
import de.dakror.dakrorbin.DakrorBin;

/**
 * @author Dakror
 */
public class AriseServer
{
	public static Server server;
	public static Properties properties;
	public static JTextPane trafficLog;
	
	public static void main(String[] args) throws Exception
	{
		new Game();
		
		new File(CFG.DIR, "/Server/config.properties").createNewFile();
		
		properties = new Properties();
		properties.load(new FileReader(new File(CFG.DIR, "/Server/config.properties")));
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		DakrorBin.init(null, "Arise-Server");
		DakrorBin.showDialog = false;
		
		if (isLogging())
		{
			File logFile = new File(new File(properties.getProperty("logfile")), "AriseServer " + new SimpleDateFormat("dd.MM.yy, HH-mm-ss").format(new Date()) + ".log");
			System.setErr(new ErrorOutputStream(System.err, logFile));
		}
		
		server = new Server(args.length > 1 ? InetAddress.getByName(args[1]) : InetAddress.getLocalHost());
		new CommandHandler();
	}
	
	public static void saveProperties()
	{
		try
		{
			properties.store(new FileOutputStream(new File(CFG.DIR, "/Server/config.properties")), "");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean isLogging()
	{
		return properties.containsKey("logfile") && properties.getProperty("logfile").trim().length() > 0;
	}
	
	public static void createTrafficFrame() throws IOException
	{
		JFrame frame = new JFrame("Arise Server Traffic Console");
		
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
