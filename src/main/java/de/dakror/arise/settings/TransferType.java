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
