package de.dakror.arise.settings;

import java.awt.Color;

/**
 * @author Dakror
 */
public enum TransferType
{
	TROOPS_FRIEND("#0096ba"),
	TROOPS_ATTACK("#a80000"),
	RESOURCES("#1ca739"),
	
	;
	
	private Color color;
	
	private TransferType(String c)
	{
		color = Color.decode(c);
	}
	
	public Color getColor()
	{
		return color;
	}
}
