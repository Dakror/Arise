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


package de.dakror.arise.ui;

import java.awt.Graphics2D;
import java.awt.Point;

import de.dakror.arise.layer.CityLayer;
import de.dakror.arise.settings.Resources.Resource;
import de.dakror.arise.settings.TroopType;
import de.dakror.arise.util.Assistant;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class ArmyLabel extends Component {
	public ArmyLabel(int x, int y) {
		super(x, y, 0, 25);
	}
	
	@Override
	public void draw(Graphics2D g) {
		int army = 0;
		for (TroopType r : TroopType.values())
			army += CityLayer.resources.get(r.getType());
		
		String string = army + "";
		if (string.length() > 3) string = string.substring(0, string.length() - 3) + "k";
		if (string.length() > 5) string = string.substring(0, string.length() - 5) + "m";
		
		if (width == 0) width = 25 + g.getFontMetrics(g.getFont().deriveFont(25f)).stringWidth(string);
		Assistant.drawLabelWithIcon(x, y, 25, new Point(Resource.ARMY.getIconX(), Resource.ARMY.getIconY()), string, 25, g);
	}
	
	@Override
	public void update(int tick) {}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g) {
		int width = 150;
		int height = TroopType.values().length * 30 + 70;
		int x1 = x;
		int y1 = y;
		
		if (x1 + width > GameFrame.getWidth()) x1 -= (x1 + width) - GameFrame.getWidth();
		if (y1 + height > GameFrame.getHeight()) y1 -= (y1 + height) - GameFrame.getHeight();
		
		Helper.drawShadow(x1, y1, width, height, g);
		Helper.drawString("Truppen", x1 + 20, y1 + 40, g, 35);
		for (int i = 0; i < TroopType.values().length; i++) {
			Resource r = TroopType.values()[i].getType();
			Assistant.drawResource(CityLayer.resources, r, x1 + 20, y1 + i * 30 + 50, 30, 30, g);
		}
	}
}
