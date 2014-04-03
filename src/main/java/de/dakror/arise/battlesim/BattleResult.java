package de.dakror.arise.battlesim;

/**
 * @author Dakror
 */
public class BattleResult
{
	int survived;
	boolean attackers;
	public float seconds;
	
	public BattleResult(boolean attackers, int survived)
	{
		this.attackers = attackers;
		this.survived = survived;
	}
	
	public int getSurvived()
	{
		return survived;
	}
	
	public boolean isAttackers()
	{
		return attackers;
	}
	
	@Override
	public String toString()
	{
		return "The " + (attackers ? "Attackers" : "Defenders") + " are victorious. " + survived + " fighters survived. Simulation took " + seconds + "s.";
	}
}
