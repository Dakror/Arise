package de.dakror.arise.server;

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
}
