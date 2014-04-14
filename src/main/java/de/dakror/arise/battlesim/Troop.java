package de.dakror.arise.battlesim;

import de.dakror.arise.settings.TroopType;

/**
 * @author Dakror
 */
public class Troop
{
	private TroopType type;
	private int life;
	private int y, cooldown, size, initialSize;
	
	protected Troop(TroopType r, int y)
	{
		type = r;
		this.y = y;
		cooldown = 0;
	}
	
	public Troop(TroopType r, int initialSize, int y)
	{
		this(r, y);
		size = initialSize;
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
	
	public void setFighters(int f)
	{
		life = 0;
		for (int i = 0; i < f; i++)
			life += type.getLife();
	}
	
	public int size()
	{
		return size;
	}
	
	public int getInitialSize()
	{
		return initialSize;
	}
	
	public void translateY(int y)
	{
		this.y += y;
	}
	
	public boolean isDead()
	{
		return life <= 0;
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
			long dmg = 0;
			for (int i = 0; i < size; i++)
			{
				int att = type.getAttack().roll();
				int def = enemy.getTroops()[0].getType().getDefense().roll();
				if (def < att) dmg += att - def;
			}
			
			enemy.getTroops()[0].attack(dmg);
			
			cooldown = type.getSpeed();
		}
	}
	
	
	public void attack(long dmg)
	{
		life -= dmg;
		if (life < 0) life = 0;
		
		size -= Math.floor(dmg / (float) type.getLife());
		if (size < 0) size = 0;
	}
	
	public int getLife()
	{
		return life;
	}
}
