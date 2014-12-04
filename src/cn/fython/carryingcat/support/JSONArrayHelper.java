package cn.fython.carryingcat.support;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONArrayHelper<E> {

	private JSONArray array;

	public JSONArrayHelper() {
		this.array = new JSONArray();
	}

	public JSONArrayHelper(JSONArray array) {
		this.array = array;
	}

	public void put(E obj) {
		array.put(obj);
	}

	public void put(int index, E obj) {
		try {
			array.put(index, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public E get(int index) {
		try {
			return (E) array.get(index);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject getJSONObject(int index) {
		try {
			return array.getJSONObject(index);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<E> toArrayList() {
		ArrayList<E> list = new ArrayList<E>();
		for (int i = 0; i < array.length(); i++) {
			list.add(get(i));
		}
		return list;
	}

	public int length() {
		return array.length();
	}

	public JSONArray toJSONArray() {
		return array;
	}

	public String toString() {
		return array.toString();
	}

}
