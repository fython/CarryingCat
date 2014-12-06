package cn.fython.carryingcat.support;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {

	private JSONObject obj;
	private final static String TAG = "JSONHelper";
	
	public JSONHelper(JSONObject obj) {
		this.obj = obj;
	}

	public JSONHelper() {
		this.obj = new JSONObject();
	}
	
	public int readInt(String name, int defValue) {
		int result = defValue;
		try {
			result = obj.getInt(name);
		} catch (JSONException e) {
			Log.e(TAG, "Error while reading \"" + name + "\" int.");
		}
		return result;
	}
	
	public String readString(String name) {
		String result = null;
		try {
			result = obj.getString(name);
		} catch (JSONException e) {
			Log.e(TAG, "Error while reading \"" + name + "\" string.");
		}
		return result;
	}

	public JSONArrayHelper<?> readJSONArray(String name) {
		try {
			return new JSONArrayHelper<Object>(obj.getJSONArray(name));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject toJSONObject() {
		return obj;
	}

	public String toString() {
		return obj.toString();
	}
	
	public void write(String name, int value) {
		try {
			obj.put(name, value);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error while writing \"" + name + "\".");
		}
	}

	public void write(String name, Object value) {
		try {
			obj.put(name, value);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error while writing \"" + name + "\".");
		}
	}

	public void write(String name, String value) {
		try {
			obj.put(name, value);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error while writing \"" + name + "\".");
		}
	}
	
	public void write(String name, JSONArray value) {
		try {
			obj.put(name, value);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error while writing \"" + name + "\".");
		}
	}
	
}