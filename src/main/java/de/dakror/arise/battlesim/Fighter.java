package de.dakror.arise.battlesim;




/**
 * @author Dakror
 */
public class Fighter
{
	private boolean dead;
	private int x, life;
	
	private Troop troop;
	
	public Fighter(int x, Troop troop)
	{
		this.x = x;
		this.troop = troop;
		life = troop.getType().getLife();
		dead = false;
	}
	
	public Troop getTroop()
	{
		return troop;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getLife()
	{
		return life;
	}
	
	public boolean isDead()
	{
		return dead;
	}
	
	public void translateX(int x)
	{
		this.x += x;
	}
	
	public void hurt(int dmg)
	{
		life -= dmg;
		if (life <= 0)
		{
			dead = true;
			life = 0;
		}
	}
	
	public void tick(Army enemy)
	{
		try
		{
			Troop[] tr = enemy.getTroops();
			Troop troop = tr[(int) (Math.random() * tr.length)];
			int fighter = (int) (Math.random() * troop.size());
			
			troop.getFighter(fighter).attack(this.troop.getType().getAttack().roll());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void attack(int attDice)
	{
		int defense = troop.getType().getDefense().roll();
		
		if (defense >= attDice) return; // blocked
		
		hurt(attDice - defense);
	}
}
