package de.dakror.arise.settings;

import java.awt.Color;

/**
 * @author Dakror
 */
public enum TransferType
{
	TROOPS_FRIEND("#0096ba", true),
	TROOPS_ATTACK("#a80000", false),
	RESOURCES("#1ca739", true),
	
	;
	
	private Color color;
	private boolean visibleForTarget;
	
	private TransferType(String c, boolean visibleForTarget)
	{
		color = Color.decode(c);
		this.visibleForTarget = visibleForTarget;
	}
	
	public boolean isVisibleForTarget()
	{
		return visibleForTarget;
	}
	
	public Color getColor()
	{
		return color;
	}
}
