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

/**
 * @author Dakror
 */
public class Packet10Attribute extends Packet {
	public static enum Key {
		city,
		world_data,
		loading_complete,
		
		;
	}
	
	Key key;
	String value;
	
	public Packet10Attribute(Key key, Object value) {
		super(10);
		this.key = key;
		this.value = value.toString();
	}
	
	public Packet10Attribute(Key key) {
		this(key, true);
	}
	
	public Packet10Attribute(byte[] data) {
		super(10);
		String[] parts = readData(data).split(":");
		key = Key.values()[Integer.parseInt(parts[0])];
		value = new String(parts[1]);
	}
	
	public Key getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	protected byte[] getPacketData() {
		return (key.ordinal() + ":" + value).getBytes();
	}
}
