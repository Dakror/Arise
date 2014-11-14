package de.dakror.arise.net.packet;

/**
 * @author Dakror
 */
public class Packet02Disconnect extends Packet {
	public enum Cause {
		SERVER_CLOSED("Der Server wurde geschlossen. Wir untersuchen dieses Problem bereits und versuchen, den Server schnellstmöglichst wieder zu starten."),
		SERVER_CONFIRMED("Der Server bestätigt die Anfrage des Clients auf Abmeldung."),
		USER_DISCONNECT("Spiel beendet."),
		INACTIVE("Du wurdest aus Inaktivität von über einer Stunde vom Server getrennt."),
		KICK("Du wurdest vom Server gekickt."),
		
		;
		private String description;
		
		private Cause(String desc) {
			description = desc;
		}
		
		public String getDescription() {
			return description;
		}
	}
	
	int id;
	Cause cause;
	
	public Packet02Disconnect(int id, Cause cause) {
		super(2);
		this.id = id;
		this.cause = cause;
	}
	
	public Packet02Disconnect(byte[] data) {
		super(2);
		String[] parts = readData(data).split(":");
		id = Integer.parseInt(new String(parts[0]));
		cause = Cause.values()[Integer.parseInt(new String(parts[1].trim()))];
	}
	
	@Override
	protected byte[] getPacketData() {
		return (id + ":" + cause.ordinal()).getBytes();
	}
	
	public int getUserId() {
		return id;
	}
	
	public Cause getCause() {
		return cause;
	}
}
