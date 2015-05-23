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
public class Packet08PlaceBuilding extends Packet {
	int cityId, type, x, y;
	
	public Packet08PlaceBuilding(int cityId, int type, int x, int y) {
		super(8);
		this.cityId = cityId;
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	public Packet08PlaceBuilding(byte[] data) {
		super(8);
		String[] parts = readData(data).split(":");
		
		cityId = Integer.parseInt(new String(parts[0]));
		type = Integer.parseInt(new String(parts[1]));
		x = Integer.parseInt(new String(parts[2]));
		y = Integer.parseInt(new String(parts[3]));
	}
	
	@Override
	protected byte[] getPacketData() {
		return (cityId + ":" + type + ":" + x + ":" + y).getBytes();
	}
	
	public int getCityId() {
		return cityId;
	}
	
	public int getBuildingType() {
		return type;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
