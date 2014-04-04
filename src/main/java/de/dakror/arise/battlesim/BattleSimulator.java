package de.dakror.arise.battlesim;

import de.dakror.arise.game.Game;
import de.dakror.arise.settings.CFG;

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
		
		boolean attAttack = true;
		
		long t = System.currentTimeMillis();
		
		while (!def.isDead() && !att.isDead())
		{
			if (attAttack) att.tick(def);
			else def.tick(att);
			
			attAttack = !attAttack;
		}
		
		BattleResult br = new BattleResult(def.isDead(), def.isDead() ? att.size() : def.size());
		br.seconds = ((System.currentTimeMillis() - t) / 1000f);
		
		return br;
	}
	
	public static void main(String[] args)
	{
		new Game();
		try
		{
			Game.loadConfig(BattleSimulator.class.getResource("/config.json"));
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
