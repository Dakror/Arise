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
