package de.dakror.arise.battlesim;

/**
 * @author Dakror
 */
public class Dice
{
	int count, maxValue;
	
	public Dice(int count, int maxValue)
	{
		this.count = count;
		this.maxValue = maxValue;
	}
	
	public Dice(String s)
	{
		String[] p = s.trim().split("W");
		count = Integer.parseInt(p[0]);
		maxValue = Integer.parseInt(p[1]);
	}
	
	public int getCount()
	{
		return count;
	}
	
	public int getMaxValue()
	{
		return maxValue;
	}
	
	public int getHighestValue()
	{
		return count * maxValue;
	}
	
	public int getLowestValue()
	{
		return count;
	}
	
	public int roll()
	{
		int v = 0;
		for (int i = 0; i < count; i++)
			v += Math.round(Math.random() * maxValue) + 1;
		
		return v;
	}
	
	@Override
	public String toString()
	{
		return count + "W" + maxValue;
	}
}
