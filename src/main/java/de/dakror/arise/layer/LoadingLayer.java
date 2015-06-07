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

import de.dakror.arise.game.Game;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class LoadingLayer extends MPLayer {
	int frame = 0;
	
	public LoadingLayer() {
		modal = true;
	}
	
	@Override
	public void draw(Graphics2D g) {
		drawModality(g);
		Helper.drawContainer((Game.getWidth() - 220) / 2, (Game.getHeight() - 80) / 2, 220, 80, false, false, g);
		Helper.drawImage2(Game.getImage("system/loader.png"), (Game.getWidth() - 180) / 2, (Game.getHeight() - 40) / 2, 180, 40, frame * 180, 0, 180, 40, g);
	}
	
	@Override
	public void update(int tick) {
		if (tick % 3 == 0) frame = (frame + 1) % 20;
	}
	
	@Override
	public void init() {}
}
