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


package de.dakror.arise.settings;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.arise.battlesim.Dice;
import de.dakror.arise.game.Game;
import de.dakror.arise.settings.Resources.Resource;

/**
 * @author Dakror
 */
public enum TroopType {
	SWORDFIGHTER(Resource.SWORDFIGHTER),
	LANCEBEARER(Resource.LANCEBEARER),
	
	;
	
	
	private Resource type;
	private Resources costs;
	private Dice attack, defense, evadeChance, hitChance;
	private boolean ranged;
	private int speed, life, buildTime;
	
	private TroopType(Resource r) {
		type = r;
		
		try {
			if (Game.config.getJSONObject("fighters").has(r.name())) {
				JSONObject o = Game.config.getJSONObject("fighters").getJSONObject(r.name());
				
				ranged = o.getBoolean("ranged");
				speed = o.getInt("speed");
				life = o.getInt("life");
				attack = new Dice(o.getString("attack"));
				defense = new Dice(o.getString("defense"));
				costs = new Resources(o.getJSONObject("costs"));
				buildTime = o.getInt("time");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Resource getType() {
		return type;
	}
	
	public void setType(Resource type) {
		this.type = type;
	}
	
	public Dice getAttack() {
		return attack;
	}
	
	public Dice getDefense() {
		return defense;
	}
	
	public Dice getEvadeChance() {
		return evadeChance;
	}
	
	public Dice getHitChance() {
		return hitChance;
	}
	
	public Resources getCosts() {
		return costs;
	}
	
	public boolean isRanged() {
		return ranged;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public int getLife() {
		return life;
	}
	
	public int getBuildTime() {
		return buildTime;
	}
}
