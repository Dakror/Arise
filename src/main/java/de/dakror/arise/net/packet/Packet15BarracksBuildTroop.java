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

import de.dakror.arise.settings.TroopType;

/**
 * @author Dakror
 */
public class Packet15BarracksBuildTroop extends Packet {
	int cityId, buildingId, amount;
	TroopType type;
	
	public Packet15BarracksBuildTroop(int cityId, int buildingId, TroopType type, int amount) {
		super(15);
		this.cityId = cityId;
		this.buildingId = buildingId;
		this.type = type;
		this.amount = amount;
	}
	
	public Packet15BarracksBuildTroop(byte[] data) {
		super(15);
		
		String[] parts = readData(data).split(":");
		cityId = Integer.parseInt(new String(parts[0]));
		buildingId = Integer.parseInt(new String(parts[1]));
		type = TroopType.values()[Integer.parseInt(new String(parts[2]))];
		amount = Integer.parseInt(new String(parts[3]));
	}
	
	@Override
	protected byte[] getPacketData() {
		return (cityId + ":" + buildingId + ":" + type.ordinal() + ":" + amount).getBytes();
	}
	
	public int getCityId() {
		return cityId;
	}
	
	public int getBuildingId() {
		return buildingId;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public TroopType getTroopType() {
		return type;
	}
}
