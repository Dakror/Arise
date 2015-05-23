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

import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.arise.settings.Const;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.TroopType;


/**
 * @author Dakror
 */
public class Army {
	private boolean attacking;
	
	private CopyOnWriteArrayList<Troop> troops;
	
	public Army(boolean att) {
		attacking = att;
		troops = new CopyOnWriteArrayList<>();
	}
	
	public Army(boolean att, Resources army) {
		this(att);
		for (TroopType t : TroopType.values())
			if (army.get(t.getType()) > 0) initTroop(t, army.get(t.getType()));
	}
	
	public void setTroop(Troop troop) {
		troops.add(troop);
	}
	
	public void initTroop(TroopType r, int amount) {
		if (getTroop(r) == null) setTroop(new Troop(r, amount, troops.size()));
		else getTroop(r).setFighters(amount);
	}
	
	public Troop getTroop(TroopType r) {
		for (Troop t : troops)
			if (t.getType() == r) return t;
		return null;
	}
	
	public int size() {
		return (int) getResources().getLength();
	}
	
	public int troops() {
		return getTroops().length;
	}
	
	public Troop[] getTroops() {
		return troops.toArray(new Troop[] {});
	}
	
	public Resources getResources() {
		Resources res = new Resources();
		
		for (Troop t : troops)
			res.set(t.getType().getType(), t.size());
		
		return res;
	}
	
	public boolean isAttacking() {
		return attacking;
	}
	
	public void setAttacking(boolean att) {
		attacking = att;
	}
	
	public boolean isDead() {
		return troops.size() == 0;
	}
	
	public long getArmyLife() {
		int life = 0;
		
		for (Troop t : troops)
			life += t.getLife();
		
		return life;
	}
	
	public int getArmyMaxLife() {
		int life = 0;
		
		for (Troop t : troops)
			life += t.getTroopMaxLife();
		
		return life;
	}
	
	public int getMarchDuration() {
		float duration = 0;
		
		for (Troop t : troops)
			duration += t.size() * t.getType().getSpeed();
		
		return (int) (duration * 10 * Const.MARCH_SECONDS);
	}
	
	public void tick(Army enemy) {
		if (enemy.isDead()) return;
		
		for (Troop t : troops) {
			if (t.isDead()) troops.remove(t);
			else t.tick(enemy);
		}
	}
}
