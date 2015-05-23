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
public class Packet09BuildingStage extends Packet {
	int buildingId, newStage, timeLeft;
	
	public Packet09BuildingStage(int buildingId, int newStage, int timeLeft) {
		super(9);
		this.buildingId = buildingId;
		this.newStage = newStage;
		this.timeLeft = timeLeft;
	}
	
	public Packet09BuildingStage(byte[] data) {
		super(9);
		String[] parts = readData(data).split(":");
		buildingId = Integer.parseInt(new String(parts[0]));
		newStage = Integer.parseInt(new String(parts[1]));
		timeLeft = Integer.parseInt(new String(parts[2]));
	}
	
	@Override
	protected byte[] getPacketData() {
		return (buildingId + ":" + newStage + ":" + timeLeft).getBytes();
	}
	
	public int getBuildingId() {
		return buildingId;
	}
	
	public int getNewStage() {
		return newStage;
	}
	
	public int getTimeLeft() {
		return timeLeft;
	}
}
