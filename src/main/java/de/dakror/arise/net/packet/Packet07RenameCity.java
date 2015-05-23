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

public class Packet07RenameCity extends Packet {
	int cityId;
	String newName;
	
	public Packet07RenameCity(int cityId, String newName) {
		super(7);
		this.cityId = cityId;
		this.newName = newName;
	}
	
	public Packet07RenameCity(byte[] data) {
		super(7);
		String[] parts = readData(data).split(":");
		cityId = Integer.parseInt(new String(parts[0]));
		newName = new String(parts[1]);
	}
	
	@Override
	protected byte[] getPacketData() {
		return (cityId + ":" + newName).getBytes();
	}
	
	public int getCityId() {
		return cityId;
	}
	
	public String getNewName() {
		return newName;
	}
}
