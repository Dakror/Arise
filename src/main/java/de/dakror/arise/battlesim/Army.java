package de.dakror.arise.battlesim;

import java.util.ArrayList;
import java.util.HashMap;

import de.dakror.arise.settings.Resources;


/**
 * @author Dakror
 */
public class Army
{
	private boolean attacking;
	
	private HashMap<TroopType, Troop> troops;
	
	public Army(boolean att)
	{
		attacking = att;
		troops = new HashMap<>();
	}
	
	public void setTroop(TroopType r, Troop troop)
	{
		troops.put(r, troop);
	}
	
	public void initTroop(TroopType r, int amount)
	{
		if (troops.get(r) == null) setTroop(r, new Troop(r, amount, troops.size()));
		else troops.get(r).setFighters(amount);
	}
	
	public Troop getTroop(TroopType r)
	{
		return troops.get(r);
	}
	
	public Troop getTroop(int y)
	{
		for (Troop t : troops.values())
			if (t.getY() == y) return t;
		return null;
	}
	
	public Troop getTroopByIndex(int i)
	{
		ArrayList<Troop> al = new ArrayList<>(troops.values());
		return al.get(i);
	}
	
	public int size()
	{
		return (int) getResources().getLength();
	}
	
	public int troops()
	{
		return getTroops().length;
	}
	
	public Troop[] getTroops()
	{
		return troops.values().toArray(new Troop[] {});
	}
	
	public Resources getResources()
	{
		Resources res = new Resources();
		
		for (TroopType r : TroopType.values())
			res.set(r.getType(), troops.get(r).size());
		
		return res;
	}
	
	public boolean isAttacking()
	{
		return attacking;
	}
	
	public void setAttacking(boolean att)
	{
		attacking = att;
	}
	
	public boolean isDead()
	{
		return troops.size() == 0;
	}
	
	@Override
	public String toString()
	{
		String s = "";
		for (Troop t : troops.values())
			s += t.toString() + "\r\n";
		
		return s;
	}
	
	public long getArmyLife()
	{
		int life = 0;
		
		for (Troop t : troops.values())
			life += t.getLife();
		
		return life;
	}
	
	public int getArmyMaxLife()
	{
		int life = 0;
		
		for (Troop t : troops.values())
			life += t.getTroopMaxLife();
		
		return life;
	}
	
	public void tick(Army enemy)
	{
		if (enemy.isDead()) return;
		
		for (Troop t : troops.values())
		{
			if (t.isDead()) troops.remove(t.getType());
			else t.tick(enemy);
		}
	}
}
