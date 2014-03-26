package de.dakror.arise.server;

/**
 * @author Dakror
 */
public class ServerUpdater extends Thread
{
	boolean running;
	
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
				System.gc();
				Thread.sleep(1000);
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
