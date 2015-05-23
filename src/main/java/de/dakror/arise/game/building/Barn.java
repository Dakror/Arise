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
public class Barn extends Building {
	public Barn(int x, int y, int level) {
		super(x, y, 7, 7, level);
		
		typeId = 6;
		name = "Reiterei";
		desc = "Hier können Berittene ausgebildet und verbessert werden.";
		
		tx = 5;
		ty = 0;
		tw = 9;
		th = 9;
		
		by = 3;
		bh -= 3;
		
		init();
	}
}
