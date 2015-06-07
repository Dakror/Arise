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


package de.dakror.arise.settings;

import java.awt.Color;

/**
 * @author Dakror
 */
public enum TransferType {
	TROOPS_FRIEND("Truppenverlagerung", "#0096ba", true),
	TROOPS_ATTACK("Angriffs-Marsch", "#a80000", false),
	RESOURCES("Warenhandel", "#1ca739", true),
	
	;
	
	private String desc;
	private Color color;
	private boolean visibleForTarget;
	
	private TransferType(String desc, String c, boolean visibleForTarget) {
		this.desc = desc;
		color = Color.decode(c);
		this.visibleForTarget = visibleForTarget;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public boolean isVisibleForTarget() {
		return visibleForTarget;
	}
	
	public Color getColor() {
		return color;
	}
}
