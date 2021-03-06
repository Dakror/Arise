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

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.UIManager;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.UpdateThread;
import de.dakror.arise.layer.LoadingLayer;
import de.dakror.arise.layer.PauseLayer;
import de.dakror.arise.net.packet.Packet02Disconnect;
import de.dakror.arise.net.packet.Packet02Disconnect.Cause;
import de.dakror.dakrorbin.DakrorBin;
import de.dakror.dakrorbin.Launch;

/**
 * @author Dakror
 */
public class Arise extends JApplet {
	private static final long serialVersionUID = 1L;
	
	public static boolean wrapper = false;
	
	public static boolean localServer = false;
	
	public static boolean running;
	
	@Override
	public void init() {
		if (!wrapper) DakrorBin.init(null, "Arise");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		running = true;
		
		new Game();
		Game.currentApplet.init(this);
		Game.currentApplet.updater = new UpdateThread();
		
		setIgnoreRepaint(true);
		
		new Thread() {
			@Override
			public void run() {
				while (running) {
					Game.currentApplet.main();
				}
			}
		}.start();
	}
	
	@Override
	public void stop() {
		try {
			if (Game.userID != 0) {
				Game.client.sendPacket(new Packet02Disconnect(Game.userID, Cause.USER_DISCONNECT));
				Game.currentGame.addLayer(new LoadingLayer());
			} else Game.exit();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			Launch.init(args);
			
			if (args.length > 0) {
				for (String s : args) {
					if (s.equals("-local")) {
						localServer = true;
						break;
					}
				}
			}
			
			wrapper = true;
			
			final JFrame frame = new JFrame("Arise Standalone v");
			frame.setIconImage(ImageIO.read(Arise.class.getResource("/img/system/logo.png")));
			frame.setSize(1280, 720);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					try {
						if (Game.userID != 0) {
							if (!(Game.currentGame.getActiveLayer() instanceof PauseLayer) && !(Game.currentGame.getActiveLayer() instanceof LoadingLayer))
								Game.currentGame.addLayer(new PauseLayer());
							Game.client.sendPacket(new Packet02Disconnect(Game.userID, Cause.USER_DISCONNECT));
							if (!(Game.currentGame.getActiveLayer() instanceof LoadingLayer)) Game.currentGame.addLayer(new LoadingLayer());
						} else Game.exit();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
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
			
			DakrorBin.init(frame, "Arise");
			
			frame.setTitle(frame.getTitle() + new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(DakrorBin.buildTimestamp));
			
			arise.init();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
