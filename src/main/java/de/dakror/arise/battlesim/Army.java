package de.dakror.arise.battlesim;

import java.util.HashMap;

import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.Resources.Resource;


/**
 * @author Dakror
 */
public class Army
{
	boolean attacking;
	
	HashMap<TroopType, Troop> troops;
	
	public Army(boolean att)
	{
		attacking = att;
		troops = new HashMap<>();
		for (TroopType t : TroopType.values())
		{
			troops.put(t, new Troop(t, t.ordinal()));
		}
	}
	
	public void setTroop(TroopType r, Troop troop)
	{
		troops.put(r, troop);
	}
	
	public void initTroop(Resource r, int amount)
	{
		if (troops.get(r) == null) return;
		
		troops.get(r).setFighters(amount);
	}
	
	public Troop getTroop(Resource r)
	{
		return troops.get(r);
	}
	
	public int size()
	{
		return (int) getResources().getLength();
	}
	
	public Resources getResources()
	{
		Resources res = new Resources();
		
		for (Resource r : Resources.armyResources)
			res.set(r, troops.get(r).size());
		
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
}
