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
 

package de.dakror.arise.game;

import java.io.IOException;

import de.dakror.arise.net.packet.Packet00Handshake;
import de.dakror.gamesetup.Updater;

/**
 * @author Dakror
 */
public class UpdateThread extends Updater {
	long lastPing = 0;
	
	@Override
	public void update() {
		if (tick % 60 == 0) {
			Game.currentGame.usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			try {
				Game.client.sendPacket(new Packet00Handshake());
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.gc();
		}
	}
}
