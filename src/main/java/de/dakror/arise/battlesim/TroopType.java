package de.dakror.arise.battlesim;

import java.awt.Color;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.arise.game.Game;
import de.dakror.arise.settings.CFG;
import de.dakror.arise.settings.Resources.Resource;

/**
 * @author Dakror
 */
public enum TroopType
{
	SWORDFIGHTER(Resource.SWORDFIGHTER),
	LANCEBEARER(Resource.LANCEBEARER),
	
	;
	
	private Resource type;
	private Dice attack, defense, evadeChance, hitChance;
	private boolean ranged;
	private int speed, life;
	private Color visColor;
	
	private TroopType(Resource r)
	{
		type = r;
		
		try
		{
			if (Game.config.getJSONObject("fighters").has(r.name()))
			{
				JSONObject o = Game.config.getJSONObject("fighters").getJSONObject(r.name());
				
				ranged = o.getBoolean("ranged");
				speed = o.getInt("speed");
				life = o.getInt("life");
				attack = new Dice(o.getString("attack"));
				defense = new Dice(o.getString("defense"));
				visColor = Color.decode(o.getString("vis"));
			}
			else CFG.e("invalid troopType!");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public Resource getType()
	{
		return type;
	}
	
	public void setType(Resource type)
	{
		this.type = type;
	}
	
	public Dice getAttack()
	{
		return attack;
	}
	
	public Dice getDefense()
	{
		return defense;
	}
	
	public Dice getEvadeChance()
	{
		return evadeChance;
	}
	
	public Dice getHitChance()
	{
		return hitChance;
	}
	
	public boolean isRanged()
	{
		return ranged;
	}
	
	public int getSpeed()
	{
		return speed;
	}
	
	public int getLife()
	{
		return life;
	}
	
	public Color getVisColor()
	{
		return visColor;
	}
}
