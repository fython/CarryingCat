package cn.fython.carryingcat.support;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.fython.carryingcat.support.api.FlvxzTools;

public class VideoItem {

	public String name, path, playurl, imgurl;
	public int selectedSource = 0;
	public ArrayList<VideoSource> srcs;

	public VideoItem() {
		this.srcs = new ArrayList<VideoSource>();
	}

	public VideoItem(ArrayList<VideoSource> srcs) {
		this.srcs = srcs;
		if (this.srcs.size() > 0) {
			this.name = this.srcs.get(0).title;
			this.playurl = this.srcs.get(0).playurl;
			this.imgurl = this.srcs.get(0).imgurl;
		}
	}

	public VideoItem(ArrayList<VideoSource> srcs, int selectedSource) {
		this.srcs = srcs;
		this.selectedSource = selectedSource;
		if (this.srcs.size() > 0) {
			this.name = this.srcs.get(0).title;
			this.playurl = this.srcs.get(0).playurl;
			this.imgurl = this.srcs.get(0).imgurl;
		}
	}
	
	public VideoItem(String name, String path,
	                 ArrayList<VideoSource> srcs) {
		this.srcs = srcs;
		if (this.srcs.size() > 0) {
			this.name = this.srcs.get(0).title;
			this.playurl = this.srcs.get(0).playurl;
			this.imgurl = this.srcs.get(0).imgurl;
		}
		this.name = name;
		this.path = path;
	}

	public VideoItem(JSONObject jsonObject) throws JSONException {
		try {
			this.path = jsonObject.getString("path");
		} catch (JSONException e) {

		}
		this.srcs = FlvxzTools.getVideoSource(jsonObject.getJSONArray("sources"));
		this.selectedSource = jsonObject.getInt("selectedSource");
	}

	public JSONObject toJSONObject() {
		JSONObject object = new JSONObject();
		try {
			JSONArray array = new JSONArray();
			for (VideoSource src:srcs) {
				array.put(src.toJSONObject());
			}
			object.put("path", path);
			object.put("sources", array);
			object.put("selectedSource", selectedSource);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}
