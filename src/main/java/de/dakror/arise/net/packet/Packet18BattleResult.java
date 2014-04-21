package de.dakror.arise.net.packet;

public class Packet18BattleResult extends Packet
{
	boolean won, def;
	int dead;
	String attCity, defCity, attCityOwner, defCityOwner;
	
	public Packet18BattleResult(boolean won, boolean def, int dead, String attCity, String defCity, String attCityOwner, String defCityOwner)
	{
		super(18);
		this.won = won;
		this.def = def;
		this.dead = dead;
		this.attCity = attCity;
		this.defCity = defCity;
		this.attCityOwner = attCityOwner;
		this.defCityOwner = defCityOwner;
	}
	
	public Packet18BattleResult(byte[] data)
	{
		super(18);
		String[] parts = readData(data).split(":");
		won = Boolean.parseBoolean(new String(parts[0]));
		def = Boolean.parseBoolean(new String(parts[1]));
		dead = Integer.parseInt(new String(parts[2]));
		attCity = new String(parts[3]);
		defCity = new String(parts[4]);
		attCityOwner = new String(parts[5]);
		defCityOwner = new String(parts[6]);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (Boolean.toString(won) + ":" + Boolean.toString(def) + ":" + dead + ":" + attCity + ":" + defCity + ":" + attCityOwner + ":" + defCityOwner).getBytes();
	}
	
	public boolean hasWon()
	{
		return won;
	}
	
	public boolean isDefender()
	{
		return def;
	}
	
	public int getDead()
	{
		return dead;
	}
	
	public String getAttCity()
	{
		return attCity;
	}
	
	public String getDefCity()
	{
		return defCity;
	}
	
	public String getAttCityOwner()
	{
		return attCityOwner;
	}
	
	public String getDefCityOwner()
	{
		return defCityOwner;
	}
	
	@Override
	public String toString()
	{
		if (won)
		{
			String add = " und das gegnerische Heer zerschlagen!";
			if (def) return "Deine Stadt " + defCity + " \nhat sich erfolgreich gegen die Truppen von \n" + attCityOwner + " aus " + attCity + " \nzur Wehr gesetzt" + add;
			else return "Deine Stadt " + attCity + " \nhat einen erfolgreichen Angriff auf die Stadt \n" + defCity + " von " + defCityOwner + " \nausgeführt" + add + (dead > 0 ? " \nJedoch sind beim Ansturm \n" + dead + " \nSoldaten umgekommen." : " \nDein Heer hat keine Verluste zu melden.");
		}
		else
		{
			if (def) return "Deine Stadt " + defCity + " \nwurde von Truppen von \n" + attCityOwner + " aus " + attCity + " \nüberrant!";
			else return "Deine Stadt " + attCity + " \nscheiterte beim Angriff auf die Stadt \n" + defCity + " von " + defCityOwner + "!";
		}
	}
}
