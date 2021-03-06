/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


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
public class AriseServer {
	public static Server server;
	public static Properties properties;
	public static JTextPane trafficLog;
	
	public static void main(String[] args) throws Exception {
		new Game();
		
		new File(CFG.DIR, "/Server/config.properties").createNewFile();
		
		properties = new Properties();
		properties.load(new FileReader(new File(CFG.DIR, "/Server/config.properties")));
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		DakrorBin.init(null, "Arise-Server");
		DakrorBin.showDialog = false;
		
		if (isLogging()) {
			cleanupLogs();
			File errors = new File(new File(properties.getProperty("logfile")), "AriseServer " + new SimpleDateFormat("dd.MM.yy, HH-mm-ss").format(new Date()) + ".log");
			System.setErr(new ErrorOutputStream(System.err, errors));
		}
		
		server = new Server(args.length > 1 ? InetAddress.getByName(args[1]) : InetAddress.getLocalHost());
		new CommandHandler();
	}
	
	public static void saveProperties() {
		try {
			properties.store(new FileOutputStream(new File(CFG.DIR, "/Server/config.properties")), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isLogging() {
		return properties.containsKey("logfile") && properties.getProperty("logfile").trim().length() > 0;
	}
	
	public static void cleanupLogs() {
		for (File f : new File(properties.getProperty("logfile")).listFiles()) {
			if (f.getName().contains("AriseServer") && f.length() == 0) f.delete();
		}
	}
	
	public static void createTrafficFrame() throws IOException {
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
