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

import java.awt.Graphics2D;
import java.io.IOException;

import javax.swing.JOptionPane;

import de.dakror.arise.game.Game;
import de.dakror.arise.game.world.World;
import de.dakror.arise.net.packet.Packet;
import de.dakror.arise.net.packet.Packet.PacketTypes;
import de.dakror.arise.net.packet.Packet01Login;
import de.dakror.arise.net.packet.Packet01Login.Response;
import de.dakror.arise.net.packet.Packet03World;
import de.dakror.arise.net.packet.Packet10Attribute;
import de.dakror.arise.net.packet.Packet10Attribute.Key;
import de.dakror.dakrorbin.Launch;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.ui.ClickEvent;

/**
 * @author Dakror
 */
public class LoginLayerDakrorLauncher extends MPLayer {
	
	@Override
	public void draw(Graphics2D g) {}
	
	@Override
	public void update(int tick) {}
	
	@Override
	public void init() {
		String id = JOptionPane.showInputDialog("ID der gewünschten Welt: ", Game.worldID);
		try {
			int i = Integer.parseInt(id);
			Game.worldID = i;
			Game.client.sendPacket(new Packet01Login(Launch.username, Launch.pwdMd5, Game.worldID));
			Game.currentGame.addLayer(new LoadingLayer());
		} catch (Exception e) {}
	}
	
	@Override
	public void onReceivePacket(Packet p) {
		super.onReceivePacket(p);
		
		if (p.getType() == PacketTypes.LOGIN) {
			if (((Packet01Login) p).getResponse() != Response.LOGIN_OK) {
				Game.currentGame.removeLoadingLayer();
				Game.currentGame.addLayer(new Alert(((Packet01Login) p).getResponse().text, new ClickEvent() {
					@Override
					public void trigger() {
						System.exit(0);
					}
				}));
			} else {
				Game.username = ((Packet01Login) p).getUsername();
				Game.userID = ((Packet01Login) p).getUserId();
				try {
					Game.client.sendPacket(new Packet03World(Game.worldID));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (p.getType() == PacketTypes.WORLD) {
			try {
				Game.world = new World((Packet03World) p);
				Game.client.sendPacket(new Packet10Attribute(Key.world_data, Game.worldID));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (p.getType() == PacketTypes.CITY || p.getType() == PacketTypes.TRANSFER) {
			Game.world.onReceivePacket(p);
		}
		
		if (p.getType() == PacketTypes.ATTRIBUTE) {
			if (((Packet10Attribute) p).getKey() == Key.loading_complete) Game.currentGame.startGame();
		}
	}
}
