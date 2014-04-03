package de.dakror.arise.battlesim;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Dakror
 */
public class Troop
{
	private TroopType type;
	private CopyOnWriteArrayList<Fighter> fighters;
	private int y, cooldown, initialSize;
	
	public Troop(TroopType r, int y)
	{
		type = r;
		this.y = y;
		fighters = new CopyOnWriteArrayList<>();
		
		cooldown = 0;
	}
	
	public Troop(TroopType r, int initialSize, int y)
	{
		this(r, y);
		this.initialSize = initialSize;
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
	
	public void negateY()
	{
		y = -y;
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
			fighters.add(new Fighter(i, this));
	}
	
	public void removeTheDead()
	{
		for (Fighter f : fighters)
			if (f.isDead()) fighters.remove(f);
	}
	
	public int size()
	{
		removeTheDead();
		return fighters.size();
	}
	
	public void translateY(int y)
	{
		this.y += y;
	}
	
	public boolean isDead()
	{
		for (Fighter f : fighters)
			if (!f.isDead()) return false;
		
		return true;
	}
	
	@Override
	public String toString()
	{
		String s = "[";
		
		if (!isDead())
		{
			for (Fighter f : fighters)
				s += type.name().substring(0, 1) + "(" + Math.round(f.getLife() / (float) type.getLife() * 100) + "%) ";
		}
		
		return s + "]";
	}
	
	public int getTroopLife()
	{
		int life = 0;
		
		for (Fighter f : fighters)
			life += f.getLife();
		
		return life;
	}
	
	public int getTroopMaxLife()
	{
		return type.getLife() * initialSize;
	}
	
	public void tick(Army enemy)
	{
		if (cooldown > 0) cooldown--;
		else
		{
			for (Fighter f : fighters)
			{
				if (enemy.isDead()) return;
				f.tick(enemy);
			}
			
			cooldown = type.getSpeed();
		}
	}
}
