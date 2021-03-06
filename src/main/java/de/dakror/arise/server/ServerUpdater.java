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


package de.dakror.arise.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.dakror.arise.net.Server;
import de.dakror.arise.net.User;
import de.dakror.arise.net.packet.Packet02Disconnect;
import de.dakror.arise.net.packet.Packet02Disconnect.Cause;

/**
 * @author Dakror
 */
public class ServerUpdater extends Thread {
	boolean running;
	long lastCheck;
	
	public ServerUpdater() {
		running = true;
		setName("ServerUpdater Thread");
		start();
	}
	
	@Override
	public void run() {
		try {
			while (running) {
				DBManager.updateTimers();
				DBManager.updateBuildingStage();
				DBManager.updateTransfers();
				
				if (System.currentTimeMillis() - lastCheck >= 60000) {
					DBManager.updateCityResources();
					DBManager.dispatchCityResources();
					kickInactiveUsers();
					
					lastCheck = System.currentTimeMillis();
					Server.currentServer.logWriter.append(new SimpleDateFormat("MM-dd-yy HH:mm").format(new Date()) + ";" + (Runtime.getRuntime().totalMemory() / 1024f / 1024f) + ";"
							+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024f / 1024f) + ";" + Server.currentServer.clients.size() + "\r\n");
					Server.currentServer.logWriter.flush();
					System.gc();
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void kickInactiveUsers() throws Exception {
		for (User u : Server.currentServer.clients) {
			if (System.currentTimeMillis() - u.getLastInteraction() > 2000) {
				Server.currentServer.sendPacket(new Packet02Disconnect(0, Cause.INACTIVE), u);
				Server.out("Kicked user: #" + u.getId() + " (" + Cause.INACTIVE + ")");
				Server.currentServer.clients.remove(u);
			}
		}
	}
}
