package de.dakror.arise.server;

import java.awt.Point;

import de.dakror.arise.settings.CFG;

/**
 * @author Dakror
 */
public class CitySpawner
{
	public static Point spawnCity(int cities, int worldId)
	{
		int citydepth = Math.round(cities / 8.0f);
		int x = 0;
		int y = 0;
		
		if (cities > 0)
		{
			int tries = 0;
			int depth = rand(1, citydepth + 5);
			Point pos = getRandomPosition(depth);
			
			while (DBManager.cityExists(pos.x, pos.y, worldId))
			{
				pos = getRandomPosition(depth);
				tries++;
				
				if (tries % 100 == 0) CFG.p("Needing more than " + tries + " to find a city spot!");
			}
			
			x = pos.x;
			y = pos.y;
		}
		
		return new Point(x, y);
	}
	
	private static int rand(int min, int max)
	{
		return (int) (Math.random() * (max - min) + min);
	}
	
	private static Point getRandomPosition(int depth)
	{
		int x = rand(-depth, depth);
		int y = 0;
		if (x == -depth || x == depth) y = rand(-depth, depth);
		else y = rand(0, 1) == 0 ? -depth : depth;
		
		return new Point(x, y);
	}
}
