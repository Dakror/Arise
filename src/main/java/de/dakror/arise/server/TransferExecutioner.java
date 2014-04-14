package de.dakror.arise.server;

import de.dakror.arise.server.data.TransferData;

/**
 * @author Dakror
 */
public class TransferExecutioner
{
	public static void executeTroopAttack(TransferData transferData)
	{
		// new Thread()
		// {
		// @Override
		// public void run()
		// {
		// Army att = new Army(true, p.getAttArmy());
		// Army def = new Army(false, DBManager.getCityResources(p.getDefCityId()));
		// BattleResult br = BattleSimulator.simulateBattle(att, def);
		//
		// out(br.toString(p.getAttCityId(), p.getDefCityId()));
		//
		// if (br.isAttackers()) DBManager.resetCityArmy(p.getDefCityId());
		// else
		// {
		// for (TroopType t : TroopType.values())
		// DBManager.addCityTroops(p.getAttCityId(), t, -p.getAttArmy().get(t.getType()), false);
		// }
		//
		// for (TroopType t : TroopType.values())
		// DBManager.addCityTroops(br.isAttackers() ? p.getAttCityId() : p.getDefCityId(), t, -br.getDead().get(t.getType()), false); // winner city
		//
		// try
		// {
		// String ac = DBManager.getCityNameForId(p.getAttCityId()), dc = DBManager.getCityNameForId(p.getDefCityId()), aco = user.getUsername(), dco = DBManager.getUsernameForCityId(p.getDefCityId());
		// sendPacket(new Packet18BattleResult(br.isAttackers(), false, br.isAttackers() ? (int) br.getDead().getLength() : 0, ac, dc, aco, dco), user); // to attacker
		// User defOwner = getUserForId(DBManager.getUserIdForCityId(p.getDefCityId()));
		// if (defOwner != null) Server.currentServer.sendPacket(new Packet18BattleResult(!br.isAttackers(), true, !br.isAttackers() ? (int) br.getDead().getLength() : 0, ac, dc, aco, dco), defOwner); // to defender
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// }
		// }
		// }.start();
	}
}
