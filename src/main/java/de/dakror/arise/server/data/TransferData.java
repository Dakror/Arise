package de.dakror.arise.server.data;

import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.TransferType;

/**
 * @author Dakror
 */
public class TransferData {
	public int id, timeleft, cityFromId, cityToId;
	public Resources value;
	public TransferType type;
	
	public TransferData(int id, int timeleft, int cityFromId, int cityToId, Resources value, TransferType type) {
		this.id = id;
		this.timeleft = timeleft;
		this.cityFromId = cityFromId;
		this.cityToId = cityToId;
		this.value = value;
		this.type = type;
	}
}
