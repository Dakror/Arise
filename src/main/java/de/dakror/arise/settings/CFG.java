package de.dakror.arise.settings;

import java.io.File;
import java.util.Arrays;

import de.dakror.arise.AriseServer;
import de.dakror.arise.net.Server;

public class CFG
{
	public static final File DIR = new File(System.getProperty("user.home") + "/.dakror/Arise");
	static long time;
	
	static
	{
		DIR.mkdirs();
	}
	
	public static void u()
	{
		if (time == 0) time = System.currentTimeMillis();
		else
		{
			p(System.currentTimeMillis() - time);
			time = 0;
		}
	}
	
	public static void p(Object... p)
	{
		if (AriseServer.server != null) Server.out(p);
		else
		{
			if (p.length == 1) System.out.println(p[0]);
			else System.out.println(Arrays.toString(p));
		}
	}
	
	public static void e(Object... p)
	{
		if (AriseServer.server != null) Server.err(p);
		else
		{
			if (p.length == 1) System.err.println(p[0]);
			else System.err.println(Arrays.toString(p));
		}
	}
}
