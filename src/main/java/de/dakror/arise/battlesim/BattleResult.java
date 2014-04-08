package de.dakror.arise.battlesim;

import de.dakror.arise.settings.Resources;

/**
 * @author Dakror
 */
public class BattleResult
{
	Resources survived;
	boolean attackers;
	public float seconds;
	
	public BattleResult(boolean attackers, Resources survived)
	{
		this.attackers = attackers;
		this.survived = survived;
	}
	
	public Resources getSurvived()
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
		return "The " + (attackers ? "Attackers" : "Defenders") + " are victorious. " + (int) survived.getLength() + " fighters survived. Simulation took " + seconds + "s.";
	}
}
