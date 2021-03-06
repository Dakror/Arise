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
import de.dakror.arise.util.Assistant;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class ResourceLabel extends Component {
	Resource r;
	
	public int off, perHour;
	
	public ResourceLabel(int x, int y, Resource r) {
		super(x, y, 0, 25);
		
		this.r = r;
		off = -1;
		perHour = 0;
	}
	
	@Override
	public void draw(Graphics2D g) {
		String string = Assistant.formatNumber(CityLayer.resources.get(r), 0) + "" + (off > -1 ? " / " + off : "")
				+ (perHour != 0 ? " (" + (perHour > 0 ? "+" : "") + Assistant.formatNumber(perHour, 0) + "/h)" : "");
		
		if (width == 0) width = 25 + g.getFontMetrics(g.getFont().deriveFont(25f)).stringWidth(string);
		
		Assistant.drawLabelWithIcon(x, y, 25, new Point(r.getIconX(), r.getIconY()), string, 25, g);
	}
	
	public Resource getResource() {
		return r;
	}
	
	@Override
	public void update(int tick) {}
	
	@Override
	public void drawTooltip(int x, int y, Graphics2D g) {
		int width = g.getFontMetrics(g.getFont().deriveFont(30f)).stringWidth(r.getName()) + 30;
		int height = 64;
		int x1 = x;
		int y1 = y;
		
		if (x1 + width > GameFrame.getWidth()) x1 -= (x1 + width) - GameFrame.getWidth();
		if (y1 + height > GameFrame.getHeight()) y1 -= (y1 + height) - GameFrame.getHeight();
		
		Helper.drawShadow(x1, y1, width, height, g);
		Helper.drawString(r.getName(), x1 + 15, y1 + 40, g, 30);
	}
}
