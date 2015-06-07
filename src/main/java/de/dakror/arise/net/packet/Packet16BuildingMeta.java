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
public class Packet16BuildingMeta extends Packet {
	int buildingId;
	String meta;
	
	public Packet16BuildingMeta(int buildingId, String meta) {
		super(16);
		this.buildingId = buildingId;
		this.meta = meta;
	}
	
	public Packet16BuildingMeta(byte[] data) {
		super(16);
		String[] parts = readData(data).split("\\[#\\]");
		buildingId = Integer.parseInt(new String(parts[0]));
		if (parts.length > 1) meta = new String(parts[1]);
		else meta = "";
	}
	
	public int getBuildingId() {
		return buildingId;
	}
	
	public String getMeta() {
		return meta;
	}
	
	@Override
	protected byte[] getPacketData() {
		return (buildingId + "[#]" + meta).getBytes();
	}
}
