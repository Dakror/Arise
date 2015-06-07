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


package de.dakror.arise.game.building;


/**
 * @author Dakror
 */
public class Lumberjack extends Building {
	public Lumberjack(int x, int y, int level) {
		super(x, y, 5, 6, level);
		
		typeId = 2;
		name = "Holzfäller";
		desc = "Fällt Bäume, um den Holzvorrat der Stadt zu erhöhen.";
		tx = 0;
		ty = 5;
		tw = 5;
		th = 6;
		
		by = 1;
		bh = 5;
		
		init();
	}
}
