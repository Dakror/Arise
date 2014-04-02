package de.dakror.arise.battlesim;

import java.io.File;

import de.dakror.arise.game.Game;
import de.dakror.arise.settings.CFG;
import de.dakror.arise.settings.Resources.Resource;

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
		
		
	}
	
	public static void main(String[] args)
	{
		new Game();
		try
		{
			Game.loadConfig(new File("C:\\Users\\Dakror\\Desktop\\config.json").toURI().toURL());
			Army att = new Army(true);
			att.initTroop(Resource.SWORDFIGHTER, 40);
			
			Army def = new Army(false);
			def.initTroop(Resource.SWORDFIGHTER, 40);
			BattleSimulator bs = new BattleSimulator(att, def);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
