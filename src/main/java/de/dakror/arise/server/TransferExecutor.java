package de.dakror.arise.server;

import de.dakror.arise.battlesim.Army;
import de.dakror.arise.battlesim.BattleResult;
import de.dakror.arise.battlesim.BattleSimulator;
import de.dakror.arise.net.Server;
import de.dakror.arise.net.User;
import de.dakror.arise.net.packet.Packet05Resources;
import de.dakror.arise.net.packet.Packet18BattleResult;
import de.dakror.arise.net.packet.Packet19Transfer;
import de.dakror.arise.net.packet.Packet20Takeover;
import de.dakror.arise.server.data.TransferData;
import de.dakror.arise.settings.CFG;
import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.TroopType;

/**
 * @author Dakror
 */
public class TransferExecutor
{
	public static void execute(TransferData data) throws Exception
	{
		switch (data.type)
		{
			case TROOPS_ATTACK:
			{
				executeTroopAttack(data);
				break;
			}
			case TROOPS_FRIEND:
			{
				for (TroopType type : TroopType.values())
					DBManager.addCityTroops(data.cityToId, type, data.value.get(type.getType()), false);
				
				User recOwner = Server.currentServer.getUserForId(DBManager.getUserIdForCityId(data.cityToId));
				if (recOwner != null) Server.currentServer.sendPacket(new Packet05Resources(data.cityToId, DBManager.getCityResources(data.cityToId)), recOwner);
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
				
				Packet19Transfer transferBack = null;
				
				if (br.isAttackers())
				{
					DBManager.resetCityArmy(transferData.cityToId); // losers lose everything
					
					Resources alive = new Resources(transferData.value.getBinaryData());
					alive.add(Resources.mul(br.getDead(), -1));
					transferBack = DBManager.transferAttackTroopsBackHome(transferData.cityToId, transferData.cityFromId, alive); // winning attackers get sent back home
				}
				else
				{
					for (TroopType t : TroopType.values())
						DBManager.addCityTroops(transferData.cityToId, t, -br.getDead().get(t.getType()), false); // winner is defending city, their deads get subtracted here
				}
				
				try
				{
					String ac = DBManager.getCityNameForId(transferData.cityFromId);
					String dc = DBManager.getCityNameForId(transferData.cityToId);
					
					int attUserId = DBManager.getUserIdForCityId(transferData.cityFromId);
					User attOwner = Server.currentServer.getUserForId(attUserId);
					User defOwner = Server.currentServer.getUserForId(DBManager.getUserIdForCityId(transferData.cityToId));
					
					if (br.isAttackers())
					{
						Packet20Takeover p20 = DBManager.handleTakeover(transferData.cityToId, transferData.cityFromId, attUserId, new Army(true, transferData.value));
						if (attOwner != null)
						{
							if (!p20.isCityTakenOver()) Server.currentServer.sendPacket(p20, attOwner);
							if (transferBack != null) Server.currentServer.sendPacket(transferBack, attOwner);
						}
						if (defOwner != null)
						{
							if (!p20.isCityTakenOver()) Server.currentServer.sendPacket(p20, defOwner);
							if (transferBack != null) Server.currentServer.sendPacket(transferBack, defOwner);
						}
						
						if (p20.isCityTakenOver()) Server.currentServer.sendPacketToAllClientsOnWorld(p20, DBManager.getWorldIdForCityId(transferData.cityFromId));
					}
					
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
