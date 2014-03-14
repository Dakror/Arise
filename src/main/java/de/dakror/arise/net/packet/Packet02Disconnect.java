package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet02Disconnect extends Packet
{
	public enum Cause
	{
		SERVER_CLOSED("Der Server wurde geschlossen."),
		SERVER_CONFIRMED("Der Server bestätigt die Anfrage des Clients auf Abmeldung."),
		USER_DISCONNECT("Spiel beendet."),
		
		;
		private String description;
		
		private Cause(String desc)
		{
			description = desc;
		}
		
		public String getDescription()
		{
			return description;
		}
	}
	
	int id;
	Cause cause;
	
	public Packet02Disconnect(int id, Cause cause)
	{
		super(2);
		this.id = id;
		this.cause = cause;
	}
	
	public Packet02Disconnect(byte[] data)
	{
		super(2);
		String[] parts = readData(data).split(":");
		id = Integer.parseInt(parts[0]);
		cause = Cause.values()[Integer.parseInt(parts[1].trim())];
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (id + ":" + cause.ordinal()).getBytes();
	}
	
	public int getUserId()
	{
		return id;
	}
	
	public Cause getCause()
	{
		return cause;
	}
}
