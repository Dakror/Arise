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
public class Packet12UpgradeBuilding extends Packet {
	int buildingId, cityId;
	
	public Packet12UpgradeBuilding(int buildingId, int cityId) {
		super(12);
		this.buildingId = buildingId;
		this.cityId = cityId;
	}
	
	public Packet12UpgradeBuilding(byte[] data) {
		super(12);
		String[] parts = readData(data).split(":");
		buildingId = Integer.parseInt(new String(parts[0]));
		cityId = Integer.parseInt(new String(parts[1]));
	}
	
	public int getBuildingId() {
		return buildingId;
	}
	
	public int getCityId() {
		return cityId;
	}
	
	@Override
	protected byte[] getPacketData() {
		return (buildingId + ":" + cityId).getBytes();
	}
}
