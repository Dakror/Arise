package de.dakror.arise.settings;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Dakror
 */
public class Resources
{
	public enum Resource
	{
		GOLD("Gold", 2, 1, 1, true),
		PEOPLE("Einwohner", 0, 0, 0, false),
		WOOD("Holz", 3, 0, 4, true),
		STONE("Stein", 1, 1, 2, true),
		BUILDINGS("Gebäude", 3, 3, 0, false),
		SWORDFIGHTER("Schwertkämpfer", 4, 0, 0, false),
		LANCEBEARER("Lanzenträger", 4, 1, 0, false),
		ARMY("Truppen", 0, 4, 0, false),
		
		;
		
		private String name;
		private boolean usable;
		private int iconX, iconY, goldValue;
		
		private Resource(String name, int iconX, int iconY, int goldValue, boolean usable)
		{
			this.name = name;
			this.iconX = iconX;
			this.iconY = iconY;
			this.goldValue = goldValue;
			this.usable = usable;
		}
		
		public String getName()
		{
			return name;
		}
		
		public int getIconX()
		{
			return iconX;
		}
		
		public int getIconY()
		{
			return iconY;
		}
		
		public int getGoldValue()
		{
			return goldValue;
		}
		
		public boolean isUsable()
		{
			return usable;
		}
		
		public static Resource[] usable()
		{
			ArrayList<Resource> res = new ArrayList<>();
			
			for (Resource r : values())
				if (r.isUsable()) res.add(r);
			
			return res.toArray(new Resource[] {});
		}
		
		public static Resource[] usableNoGold()
		{
			ArrayList<Resource> res = new ArrayList<>();
			
			for (Resource r : values())
				if (r.isUsable() && r != GOLD) res.add(r);
			
			return res.toArray(new Resource[] {});
		}
	}
	
	public static final Resource[] serializableResources = { Resource.GOLD, Resource.WOOD, Resource.STONE, Resource.SWORDFIGHTER, Resource.LANCEBEARER };
	
	HashMap<Resource, Float> res = new HashMap<>();
	
	public Resources()
	{
		for (Resource t : Resource.values())
			res.put(t, 0f);
	}
	
	public Resources(JSONObject data) throws JSONException
	{
		this();
		
		JSONArray names = data.names();
		for (int i = 0; i < data.length(); i++)
		{
			res.put(Resource.valueOf(names.getString(i)), (float) data.getDouble(names.getString(i)));
		}
	}
	
	public Resources(byte[] data)
	{
		this();
		
		ByteBuffer bb = ByteBuffer.wrap(data);
		for (int i = 0; i < serializableResources.length; i++)
			set(serializableResources[i], bb.getFloat());
	}
	
	public int get(Resource t)
	{
		return (int) (float) res.get(t);
	}
	
	public float getF(Resource t)
	{
		return res.get(t);
	}
	
	public Resources set(Resource t, int value)
	{
		return set(t, (float) value);
	}
	
	public Resources set(Resource t, float value)
	{
		res.put(t, value);
		
		return this;
	}
	
	public void add(Resource t, int value)
	{
		add(t, (float) value);
	}
	
	public void add(Resource t, float value)
	{
		res.put(t, getF(t) + value);
	}
	
	public void add(Resources r)
	{
		for (Resource s : r.getFilled())
			add(s, r.getF(s));
	}
	
	public int size()
	{
		int s = 0;
		
		for (Resource r : res.keySet())
			if (res.get(r) != 0) s++;
		
		return s;
	}
	
	public float getLength()
	{
		float length = 0;
		
		for (Resource r : res.keySet())
			length += getF(r);
		
		return length;
	}
	
	public ArrayList<Resource> getFilled()
	{
		ArrayList<Resource> res = new ArrayList<>();
		
		for (Resource r : Resource.values())
			if (getF(r) != 0) res.add(r);
		
		return res;
	}
	
	public JSONObject getData()
	{
		JSONObject o = new JSONObject();
		try
		{
			for (Resource r : Resource.values())
			{
				o.put(r.name(), getF(r));
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return o;
	}
	
	public byte[] getBinaryData()
	{
		ByteBuffer bb = ByteBuffer.allocate(serializableResources.length * 4);
		for (Resource r : serializableResources)
		{
			bb.putFloat(getF(r));
		}
		
		return bb.array();
	}
	
	public static Resources mul(Resources res, int f)
	{
		Resources result = new Resources();
		for (Resource r : res.getFilled())
			result.set(r, res.get(r) * f);
		
		return result;
	}
	
	public static Resources mul(Resources res, float f)
	{
		Resources result = new Resources();
		for (Resource r : res.getFilled())
			result.set(r, res.getF(r) * f);
		
		return result;
	}
}
