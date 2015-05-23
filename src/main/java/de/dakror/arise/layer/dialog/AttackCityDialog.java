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
 

package de.dakror.arise.layer.dialog;

import java.awt.Graphics2D;
import java.io.IOException;

import de.dakror.arise.battlesim.Army;
import de.dakror.arise.game.Game;
import de.dakror.arise.layer.MPLayer;
import de.dakror.arise.net.packet.Packet17CityAttack;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.TroopType;
import de.dakror.arise.util.Assistant;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.ui.Slider;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class AttackCityDialog extends MPLayer {
	int width, height;
	
	int attCityId, defCityId;
	Resources resources;
	
	TextButton attack;
	
	public AttackCityDialog(int attCityId, int defCityId, Resources resources) {
		this.attCityId = attCityId;
		this.defCityId = defCityId;
		this.resources = resources;
		modal = true;
		
		width = 20 + TextButton.WIDTH * 2;
		height = 300;
	}
	
	@Override
	public void init() {
		components.clear();
		
		for (TroopType t : TroopType.values()) {
			int v = resources.get(t.getType());
			Slider slider = new Slider((Game.getWidth() - width) / 2 + 20, (Game.getHeight() - height) / 2 + 90 + t.ordinal() * 50, width - 40, 0, v == 0 ? 1 : v, 0, true);
			if (v == 0) slider.enabled = false;
			slider.setTitle(t.getType().getName());
			components.add(slider);
		}
		
		TextButton abort = new TextButton((Game.getWidth() - width) / 2 + 10, (Game.getHeight() + height) / 2 - TextButton.HEIGHT - 10, "Abbruch");
		abort.addClickEvent(new ClickEvent() {
			@Override
			public void trigger() {
				Game.currentGame.removeLayer(AttackCityDialog.this);
			}
		});
		components.add(abort);
		attack = new TextButton(Game.getWidth() / 2, (Game.getHeight() + height) / 2 - TextButton.HEIGHT - 10, "Angreifen");
		attack.addClickEvent(new ClickEvent() {
			@Override
			public void trigger() {
				try {
					Game.client.sendPacket(new Packet17CityAttack(attCityId, defCityId, getSelectedResources()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				Game.currentGame.removeLayer(AttackCityDialog.this);
			}
		});
		components.add(attack);
	}
	
	public Resources getSelectedResources() {
		Resources res = new Resources();
		for (Component c : components) {
			if (c instanceof Slider) {
				for (TroopType t : TroopType.values()) {
					if (((Slider) c).getTitle().equals(t.getType().getName())) {
						res.set(t.getType(), ((Slider) c).getValue());
						break;
					}
				}
			}
		}
		return res;
	}
	
	@Override
	public void draw(Graphics2D g) {
		Helper.drawContainer((Game.getWidth() - width) / 2, (Game.getHeight() - height) / 2, width, height, false, false, g);
		Helper.drawHorizontallyCenteredString("Stadt angreifen", Game.getWidth(), (Game.getHeight() - height) / 2 + 40, g, 35);
		
		Army army = new Army(true, getSelectedResources());
		Helper.drawHorizontallyCenteredString("Marschdauer: " + Assistant.formatSeconds(army.getMarchDuration() / Game.world.getSpeed()), Game.getWidth(),
																					(Game.getHeight() - height) / 2 + 200, g, 30);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick) {
		updateComponents(tick);
		
		attack.enabled = false;
		for (Component c : components) {
			if (c instanceof Slider) {
				for (TroopType t : TroopType.values()) {
					if (((Slider) c).getTitle().equals(t.getType().getName())) {
						if (((Slider) c).getValue() > 0) attack.enabled = true;
						break;
					}
				}
			}
		}
	}
}
