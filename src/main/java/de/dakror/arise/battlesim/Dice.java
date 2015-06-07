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


package de.dakror.arise.battlesim;

/**
 * @author Dakror
 */
public class Dice {
	int count, maxValue;
	
	public Dice(int count, int maxValue) {
		this.count = count;
		this.maxValue = maxValue;
	}
	
	public Dice(String s) {
		String[] p = s.trim().split("W");
		count = Integer.parseInt(p[0]);
		maxValue = Integer.parseInt(p[1]);
	}
	
	public int getCount() {
		return count;
	}
	
	public int getMaxValue() {
		return maxValue;
	}
	
	public int getHighestValue() {
		return count * maxValue;
	}
	
	public int getLowestValue() {
		return count;
	}
	
	public int roll() {
		int v = 0;
		for (int i = 0; i < count; i++)
			v += Math.round(Math.random() * maxValue) + 1;
		
		return v;
	}
	
	@Override
	public String toString() {
		return count + "W" + maxValue;
	}
}
