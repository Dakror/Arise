package de.dakror.arise.battlesim;

import de.dakror.arise.game.Game;
import de.dakror.arise.settings.CFG;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.TroopType;

/**
 * @author Dakror
 */
public class BattleSimulator
{
	public static BattleResult simulateBattle(Army att, Army def)
	{
		if (!att.isAttacking() || def.isAttacking())
		{
			CFG.e("invalid armies");
			return null;
		}
		
		Resources attBefore = new Resources(att.getResources().getBinaryData());
		Resources defBefore = new Resources(def.getResources().getBinaryData());
		
		boolean attAttack = true;
		
		long t = System.currentTimeMillis();
		
		while (!def.isDead() && !att.isDead())
		{
			if (attAttack) att.tick(def);
			else def.tick(att);
			
			attAttack = !attAttack;
		}
		
		attBefore.add(Resources.mul(att.getResources(), -1));
		defBefore.add(Resources.mul(def.getResources(), -1));
		
		BattleResult br = new BattleResult(def.isDead(), def.isDead() ? attBefore : defBefore);
		br.seconds = ((System.currentTimeMillis() - t) / 1000f);
		
		return br;
	}
	
	public static void main(String[] args)
	{
		new Game();
		try
		{
			Game.loadConfig();
			Army att = new Army(true);
			att.initTroop(TroopType.SWORDFIGHTER, 2000);
			
			Army def = new Army(false);
			def.initTroop(TroopType.SWORDFIGHTER, 2000);
			BattleResult br = simulateBattle(att, def);
			CFG.p(br.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
