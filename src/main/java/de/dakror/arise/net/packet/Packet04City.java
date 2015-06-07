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
public class Packet04City extends Packet {
	int x, y, userId, cityId, level;
	String cityName, username;
	
	public Packet04City(int cityId, int x, int y, int userId, int level, String cityName, String username) {
		super(4);
		this.cityId = cityId;
		this.x = x;
		this.y = y;
		this.userId = userId;
		this.level = level;
		this.cityName = cityName;
		this.username = username;
	}
	
	public Packet04City(byte[] data) {
		super(4);
		String s = readData(data);
		String[] parts = s.split(":");
		cityId = Integer.parseInt(new String(parts[0]));
		x = Integer.parseInt(new String(parts[1]));
		y = Integer.parseInt(new String(parts[2]));
		userId = Integer.parseInt(new String(parts[3]));
		level = Integer.parseInt(new String(parts[4]));
		cityName = new String(parts[5]);
		username = new String(parts[6]);
	}
	
	@Override
	protected byte[] getPacketData() {
		return (cityId + ":" + x + ":" + y + ":" + userId + ":" + level + ":" + cityName + ":" + username).getBytes();
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public int getCityId() {
		return cityId;
	}
	
	public int getLevel() {
		return level;
	}
	
	public String getCityName() {
		return cityName;
	}
	
	public String getUsername() {
		return username;
	}
}
