package de.dakror.arise.battlesim;

import java.io.File;

import de.dakror.arise.game.Game;
import de.dakror.arise.settings.CFG;

/**
 * @author Dakror
 */
public class BattleSimulator
{
	public BattleSimulator(Army att, Army def)
	{
		if (!att.isAttacking() || def.isAttacking())
		{
			CFG.e("invalid armies");
			return;
		}
		
		boolean attAttack = true;
		
		long t = System.currentTimeMillis();
		
		while (!def.isDead() && !att.isDead())
		{
			if (attAttack) att.tick(def);
			else def.tick(att);
			
			attAttack = !attAttack;
		}
		
		CFG.p("Fight took " + ((System.currentTimeMillis() - t) / 1000f) + "s");
		CFG.p("The " + (att.isDead() ? "Defenders (" + Math.round(def.getArmyLife() / (float) def.getArmyMaxLife() * 100) : "Attackers (" + Math.round(att.getArmyLife() / (float) att.getArmyMaxLife() * 100)) + "%) are victorious!");
		
		while (true);
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
			new BattleSimulator(att, def);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
