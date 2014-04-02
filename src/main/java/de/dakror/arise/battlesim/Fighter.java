package de.dakror.arise.battlesim;


/**
 * @author Dakror
 */
public class Fighter
{
	private boolean dead;
	private int x, life;
	
	private Fighter target;
	
	public Fighter(int x)
	{
		this.x = x;
		
		dead = false;
	}
	
	public Fighter getTarget()
	{
		return target;
	}
	
	public void setTarget(Fighter target)
	{
		this.target = target;
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
}
