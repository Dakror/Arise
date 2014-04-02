package de.dakror.arise.battlesim;

import java.util.ArrayList;

/**
 * @author Dakror
 */
public class Troop
{
	private TroopType type;
	private ArrayList<Fighter> fighters;
	private int y;
	
	public Troop(TroopType r, int y)
	{
		type = r;
		this.y = y;
		fighters = new ArrayList<>();
	}
	
	public Troop(TroopType r, int initialSize, int y)
	{
		this(r, y);
		setFighters(initialSize);
	}
	
	public TroopType getType()
	{
		return type;
	}
	
	public int getY()
	{
		return y;
	}
	
	public Fighter getFighter(int i)
	{
		return fighters.get(i);
	}
	
	public Fighter getFighterByCoord(int x)
	{
		for (Fighter f : fighters)
			if (f.getX() == x) return f;
		return null;
	}
	
	public void setFighters(int f)
	{
		fighters.clear();
		
		for (int i = 0; i < f; i++)
			fighters.add(new Fighter(i));
	}
	
	public synchronized void removeTheDead()
	{
		for (Fighter f : fighters)
			if (f.isDead()) fighters.remove(f);
	}
	
	public synchronized int size()
	{
		removeTheDead();
		return fighters.size();
	}
	
	public void translateX(int x)
	{
		for (int i = 0; i < fighters.size(); i++)
			fighters.get(i).translateX(x);
	}
}
