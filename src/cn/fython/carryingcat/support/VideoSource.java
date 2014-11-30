package cn.fython.carryingcat.support;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.fython.carryingcat.support.VideoUrl;

public class VideoSource {

	public String title, playurl, quality, site, imgurl;
	private ArrayList<VideoUrl> urls;

	public VideoSource(String title) {
		this.title = title;
		this.urls = new ArrayList<VideoUrl>();
	}
	
	public VideoSource(String title, ArrayList<VideoUrl> urls) {
		this.title = title;
		this.urls = urls;
	}

	public void addVideoUrl(VideoUrl newUrl) {
		urls.add(newUrl);
	}

	public VideoUrl getVideoUrl(int position) {
		return urls.get(position);
	}

	public int getVideoUrlCount() {
		return urls.size();
	}

	public JSONObject toJSONObject() {
		JSONObject object = new JSONObject();
		try {
			object.put("title", title);
			object.put("playurl", playurl);
			object.put("quality", quality);
			object.put("site", site);
			object.put("imgurl", imgurl);
			JSONArray jsonArray = new JSONArray();
			for (VideoUrl url:urls) {
				jsonArray.put(url.toJSONObject());
			}
			object.put("files", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}
