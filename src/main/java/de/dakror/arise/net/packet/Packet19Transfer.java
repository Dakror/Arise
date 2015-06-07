/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.dakror.arise.net.packet;

import java.nio.ByteBuffer;

import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.TransferType;

/**
 * @author Dakror
 */
public class Packet19Transfer extends Packet {
	int cityFrom, cityTo, timeleft, id;
	/**
	 * Type of the transfer
	 */
	TransferType type;
	/**
	 * Will be empty when {@link Packet19Transfer#type type} equals {@link TransferType#TROOPS_ATTACK TROOPS_ATTACK}, so that no client side hack regarding acquiring that information can be done.
	 */
	Resources value;
	
	public Packet19Transfer(int id) {
		super(19);
		this.id = id;
	}
	
	public Packet19Transfer(int id, int cityFrom, int cityTo, TransferType type, Resources value, int timeleft) {
		super(19);
		this.id = id;
		this.cityFrom = cityFrom;
		this.cityTo = cityTo;
		this.type = type;
		this.value = value;
		this.timeleft = timeleft;
	}
	
	public Packet19Transfer(byte[] data) {
		super(19);
		
		String str = readData(data);
		
		if (str.startsWith("::")) {
			id = Integer.parseInt(new String(str.trim().replace("::", "")));
			return;
		}
		
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.get(); // skip id
		
		id = bb.getInt();
		cityFrom = bb.getInt();
		cityTo = bb.getInt();
		type = TransferType.values()[bb.getInt()];
		timeleft = bb.getInt();
		byte[] val = new byte[bb.getInt()];
		bb.get(val, 0, val.length);
		value = new Resources(val);
	}
	
	@Override
	protected byte[] getPacketData() {
		if (value == null) return ("::" + id).getBytes();
		
		byte[] val = value.getBinaryData();
		
		ByteBuffer bb = ByteBuffer.allocate(val.length + 24);
		bb.putInt(id);
		bb.putInt(cityFrom);
		bb.putInt(cityTo);
		bb.putInt(type.ordinal());
		bb.putInt(timeleft);
		bb.putInt(val.length);
		bb.put(val);
		
		return bb.array();
	}
	
	public boolean isMarkedForRemoval() {
		return value == null;
	}
	
	public int getId() {
		return id;
	}
	
	public int getCityFrom() {
		return cityFrom;
	}
	
	public int getCityTo() {
		return cityTo;
	}
	
	public int getTimeleft() {
		return timeleft;
	}
	
	public TransferType getTransferType() {
		return type;
	}
	
	public Resources getValue() {
		return value;
	}
	
}
