package de.dakror.arise.battlesim;

import java.io.File;

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
			Game.loadConfig(new File("C:\\Users\\Dakror\\Desktop\\config.json").toURI().toURL());
			Army att = new Army(true);
			att.initTroop(TroopType.SWORDFIGHTER, 1000000);
			
			Army def = new Army(false);
			def.initTroop(TroopType.SWORDFIGHTER, 1000000);
			BattleResult br = simulateBattle(att, def);
			CFG.p(br.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
