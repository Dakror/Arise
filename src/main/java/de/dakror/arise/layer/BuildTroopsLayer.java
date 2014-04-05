package de.dakror.arise.layer;

import java.awt.Graphics2D;

import de.dakror.arise.battlesim.TroopType;
import de.dakror.arise.game.building.Barracks;

/**
 * @author Dakror
 */
public class BuildTroopsLayer extends MPLayer
{
	Barracks barracks;
	TroopType type;
	
	public BuildTroopsLayer(Barracks barracks, TroopType troopType)
	{
		this.barracks = barracks;
		type = troopType;
		modal = true;
	}
	
	@Override
	public void draw(Graphics2D g)
	{}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void init()
	{}
	
}
