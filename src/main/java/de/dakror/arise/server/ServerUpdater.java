package de.dakror.arise.server;

import de.dakror.arise.net.Server;
import de.dakror.arise.net.User;
import de.dakror.arise.net.packet.Packet02Disconnect;
import de.dakror.arise.net.packet.Packet02Disconnect.Cause;
import de.dakror.dakrorbin.DakrorBin;

/**
 * @author Dakror
 */
public class ServerUpdater extends Thread
{
	boolean running;
	long lastCheck;
	
	public ServerUpdater()
	{
		running = true;
		start();
	}
	
	@Override
	public void run()
	{
		try
		{
			while (running)
			{
				DBManager.updateBuildingTimers();
				DBManager.updateBuildingStage();
				
				if (System.currentTimeMillis() - lastCheck > 60000)
				{
					DBManager.updateCityResources();
					DBManager.dispatchCityResources();
					kickInactiveUsers();
					
					DakrorBin.checkForUpdates();
					lastCheck = System.currentTimeMillis();
				}
				System.gc();
				Thread.sleep(1000);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void kickInactiveUsers() throws Exception
	{
		long hourInMs = 1000 * 60 /* * 60 */;
		for (User u : Server.currentServer.clients)
		{
			if (System.currentTimeMillis() - u.getLastInteraction() > hourInMs)
			{
				Server.currentServer.sendPacket(new Packet02Disconnect(0, Cause.INACTIVE), u);
				Server.out("Kicked user: #" + u.getId() + " (" + Cause.INACTIVE + ")");
				Server.currentServer.clients.remove(u);
			}
		}
	}
}
