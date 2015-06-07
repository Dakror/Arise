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


package de.dakror.arise.layer;

import de.dakror.arise.Arise;
import de.dakror.arise.game.Game;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet02Disconnect;
import de.dakror.arise.net.packet.Packet02Disconnect.Cause;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.ClickEvent;

public abstract class MPLayer extends Layer {
	public void onReceivePacket(Packet p) {
		if (p.getType() == PacketTypes.DISCONNECT) {
			if (((Packet02Disconnect) p).getCause() == Cause.SERVER_CONFIRMED) {
				if (Arise.wrapper) {
					System.exit(0);
				} else {
					Game.world = null;
					Game.currentGame.setLayer(new LoginLayer());
					Game.userID = 0;
				}
			} else {
				if (Arise.wrapper) {
					Game.currentGame.addLayer(new Alert(((Packet02Disconnect) p).getCause().getDescription(), new ClickEvent() {
						@Override
						public void trigger() {
							System.exit(0);
						}
					}));
				} else {
					Game.world = null;
					Game.currentGame.setLayer(new LoginLayer());
					Game.currentGame.addLayer(new Alert(((Packet02Disconnect) p).getCause().getDescription(), null));
					Game.userID = 0;
				}
			}
		}
		
		if (p.getType() == PacketTypes.BATTLERESULT && Game.userID != 0 && !(Game.currentGame.getActiveLayer() instanceof Alert)) {
			Game.currentGame.addLayer(new Alert(p.toString(), null));
		}
	}
}
