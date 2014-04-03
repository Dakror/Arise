package de.dakror.arise.battlesim;


/**
 * @author Dakror
 */
public class Troop
{
	private TroopType type;
	private int[] fighters;
	private int y, cooldown;
	
	protected Troop(TroopType r, int y)
	{
		type = r;
		this.y = y;
		
		cooldown = 0;
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
	
	public void negateY()
	{
		y = -y;
	}
	
	public void setFighters(int f)
	{
		fighters = new int[f];
		
		for (int i = 0; i < f; i++)
			fighters[i] = type.getLife();
	}
	
	public int length()
	{
		return fighters.length;
	}
	
	public int size()
	{
		int s = 0;
		for (int i : fighters)
			if (i > 0) s++;
		return s;
	}
	
	public void translateY(int y)
	{
		this.y += y;
	}
	
	public boolean isDead()
	{
		for (int f : fighters)
			if (f > 0) return false;
		
		return true;
	}
	
	@Override
	public String toString()
	{
		String s = "[";
		
		if (!isDead())
		{
			for (int f : fighters)
				s += type.name().substring(0, 1) + "(" + Math.round(f / (float) type.getLife() * 100) + "%) ";
		}
		
		return s + "]";
	}
	
	public int getTroopLife()
	{
		int life = 0;
		
		for (int f : fighters)
			life += f;
		
		return life;
	}
	
	public int getTroopMaxLife()
	{
		return type.getLife() * fighters.length;
	}
	
	public void tick(Army enemy)
	{
		if (cooldown > 0) cooldown--;
		else
		{
			for (int f : fighters)
			{
				if (enemy.isDead()) return;
				
				if (f <= 0) continue;
				
				Troop[] tr = enemy.getTroops();
				
				Troop troop = tr[(int) (Math.random() * tr.length)];
				int fighter = (int) (Math.random() * troop.size());
				
				troop.attackFighter(fighter, type.getAttack().roll());
			}
			
			cooldown = type.getSpeed();
		}
	}
	
	public void attackFighter(int fighter, int attack)
	{
		int defense = type.getDefense().roll();
		
		if (defense >= attack) return; // blocked
		
		fighters[fighter] -= (attack - defense);
		fighters[fighter] = fighters[fighter] < 0 ? 0 : fighters[fighter];
	}
}
