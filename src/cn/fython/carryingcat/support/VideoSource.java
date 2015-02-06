package cn.fython.carryingcat.support;

import org.json.JSONObject;

import java.util.ArrayList;

public class VideoSource {

	public String title, playurl, quality, site, imgurl;
	public ArrayList<VideoUrl> urls;

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
		JSONHelper helper = new JSONHelper();
		helper.write("title" ,title);
		helper.write("title", title);
		helper.write("playurl", playurl);
		helper.write("quality", quality);
		helper.write("site", site);
		helper.write("imgurl", imgurl);
		JSONArrayHelper<JSONObject> arrayHelper = new JSONArrayHelper<JSONObject>();
		for (VideoUrl url:urls) {
			arrayHelper.put(url.toJSONObject());
		}
		helper.write("files", arrayHelper.toJSONArray());
		return helper.toJSONObject();
	}

}
