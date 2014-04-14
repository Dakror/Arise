package de.dakror.arise.server;

import de.dakror.arise.battlesim.Army;
import de.dakror.arise.battlesim.BattleResult;
import de.dakror.arise.battlesim.BattleSimulator;
import de.dakror.arise.net.Server;
import de.dakror.arise.net.User;
import de.dakror.arise.net.packet.Packet18BattleResult;
import de.dakror.arise.server.data.TransferData;
import de.dakror.arise.settings.CFG;
import de.dakror.arise.settings.TroopType;

/**
 * @author Dakror
 */
public class TransferExecutor
{
	public static void execute(TransferData data)
	{
		switch (data.type)
		{
			case TROOPS_ATTACK:
			{
				executeTroopAttack(data);
				break;
			}
			default:
				CFG.p("Couldn't execute action for TransferType " + data.type.name());
				break;
		}
	}
	
	public static void executeTroopAttack(final TransferData transferData)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				Army att = new Army(true, transferData.value);
				Army def = new Army(false, DBManager.getCityResources(transferData.cityToId));
				BattleResult br = BattleSimulator.simulateBattle(att, def);
				
				Server.out(br.toString(transferData.cityFromId, transferData.cityToId));
				
				if (br.isAttackers()) DBManager.resetCityArmy(transferData.cityToId);
				else
				{
					for (TroopType t : TroopType.values())
						DBManager.addCityTroops(transferData.cityFromId, t, -transferData.value.get(t.getType()), false);
				}
				
				for (TroopType t : TroopType.values())
					DBManager.addCityTroops(br.isAttackers() ? transferData.cityFromId : transferData.cityToId, t, -br.getDead().get(t.getType()), false); // winner city
				
				try
				{
					String ac = DBManager.getCityNameForId(transferData.cityFromId);
					String dc = DBManager.getCityNameForId(transferData.cityToId);
					User attOwner = Server.currentServer.getUserForId(DBManager.getUserIdForCityId(transferData.cityFromId));
					User defOwner = Server.currentServer.getUserForId(DBManager.getUserIdForCityId(transferData.cityToId));
					
					if (attOwner != null) Server.currentServer.sendPacket(new Packet18BattleResult(br.isAttackers(), false, br.isAttackers() ? (int) br.getDead().getLength() : 0, ac, dc, attOwner.getUsername(), attOwner.getUsername()), attOwner); // to attacker
					if (defOwner != null) Server.currentServer.sendPacket(new Packet18BattleResult(!br.isAttackers(), true, !br.isAttackers() ? (int) br.getDead().getLength() : 0, ac, dc, attOwner.getUsername(), defOwner.getUsername()), defOwner); // to defender
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}
}
