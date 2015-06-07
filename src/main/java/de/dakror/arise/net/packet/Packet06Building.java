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
public class Packet06Building extends Packet {
	int cityId, id, type, level, x, y, stage, timeleft;
	String meta;
	
	public Packet06Building(int cityId) {
		super(6);
		this.cityId = cityId;
	}
	
	public Packet06Building(int cityId, int id, int type, int level, int x, int y, int stage, int timeleft, String meta) {
		super(6);
		
		this.cityId = cityId;
		this.id = id;
		this.type = type;
		this.level = level;
		this.x = x;
		this.y = y;
		this.stage = stage;
		this.timeleft = timeleft;
		this.meta = meta;
	}
	
	public Packet06Building(byte[] data) {
		super(6);
		String s = readData(data);
		if (s.contains(":")) {
			String[] parts = s.split(":");
			cityId = Integer.parseInt(new String(parts[0]));
			id = Integer.parseInt(new String(parts[1]));
			type = Integer.parseInt(new String(parts[2]));
			level = Integer.parseInt(new String(parts[3]));
			x = Integer.parseInt(new String(parts[4]));
			y = Integer.parseInt(new String(parts[5]));
			stage = Integer.parseInt(new String(parts[6]));
			timeleft = Integer.parseInt(new String(parts[7]));
			if (parts.length == 9) meta = new String(parts[8]);
		} else cityId = Integer.parseInt(s.trim());
	}
	
	@Override
	protected byte[] getPacketData() {
		if (type != 0) return (cityId + ":" + id + ":" + type + ":" + level + ":" + x + ":" + y + ":" + stage + ":" + timeleft + (meta != null ? ":" + meta : "")).getBytes();
		else return (cityId + "").getBytes();
	}
	
	public int getCityId() {
		return cityId;
	}
	
	public int getId() {
		return id;
	}
	
	public int getBuildingType() {
		return type;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getStage() {
		return stage;
	}
	
	public int getTimeleft() {
		return timeleft;
	}
	
	public String getMeta() {
		return meta;
	}
}
