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
public class Packet03World extends Packet {
	int id;
	String name;
	int speed;
	
	public Packet03World(int id) {
		super(3);
		this.id = id;
	}
	
	public Packet03World(int id, String name, int speed) {
		super(3);
		this.id = id;
		this.name = name;
		this.speed = speed;
	}
	
	public Packet03World(byte[] data) {
		super(3);
		String s = readData(data);
		if (s.contains(":")) {
			String[] parts = s.trim().split(":");
			
			id = Integer.parseInt(new String(parts[0]));
			name = new String(parts[1]);
			speed = Integer.parseInt(new String(parts[2]));
		}
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	@Override
	protected byte[] getPacketData() {
		return (id + ":" + name + ":" + speed).getBytes();
	}
	
}
