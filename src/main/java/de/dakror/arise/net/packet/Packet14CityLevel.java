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
public class Packet14CityLevel extends Packet {
	int cityId, newLevel;
	
	public Packet14CityLevel(int cityId, int newLevel) {
		super(14);
		this.cityId = cityId;
		this.newLevel = newLevel;
	}
	
	public Packet14CityLevel(byte[] data) {
		super(14);
		String[] parts = readData(data).split(":");
		cityId = Integer.parseInt(new String(parts[0]));
		newLevel = Integer.parseInt(new String(parts[1]));
	}
	
	public int getCityId() {
		return cityId;
	}
	
	public int getNewLevel() {
		return newLevel;
	}
	
	@Override
	protected byte[] getPacketData() {
		return (cityId + ":" + newLevel).getBytes();
	}
	
}
