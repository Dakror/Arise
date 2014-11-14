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
		return "The " + (attackers ? "Attackers (City #" + attCityId : "Defenders (City #" + defCityId) + ") are victorious. " + (int) dead.getLength() + " of their fighters died. Simulation took " + seconds + "s.";
	}
}
