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
 

package de.dakror.arise.battlesim;

import de.dakror.arise.settings.Resources;

/**
 * @author Dakror
 */
public class BattleResult {
	Resources dead;
	boolean attackers;
	public float seconds;
	
	public BattleResult(boolean attackers, Resources dead) {
		this.attackers = attackers;
		this.dead = dead;
	}
	
	public Resources getDead() {
		return dead;
	}
	
	public boolean isAttackers() {
		return attackers;
	}
	
	@Override
	public String toString() {
		return "The " + (attackers ? "Attackers" : "Defenders") + " are victorious. " + (int) dead.getLength() + " of their fighters died. Simulation took " + seconds + "s.";
	}
	
	public String toString(int attCityId, int defCityId) {
		return "The " + (attackers ? "Attackers (City #" + attCityId : "Defenders (City #" + defCityId) + ") are victorious. " + (int) dead.getLength()
				+ " of their fighters died. Simulation took " + seconds + "s.";
	}
}
