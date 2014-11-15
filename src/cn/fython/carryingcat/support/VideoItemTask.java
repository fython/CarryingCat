package cn.fython.carryingcat.support;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.fython.carryingcat.support.api.FlvxzTools;

public class VideoItemTask extends VideoItem {

	public ArrayList<Integer> progress;

	public VideoItemTask(VideoItem videoItem){
		JSONObject jsonObject = videoItem.toJSONObject();
		try {
			this.srcs = FlvxzTools.getVideoSource(jsonObject.getJSONArray("sources"));
			this.selectedSource = jsonObject.getInt("selectedSource");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.progress = new ArrayList<Integer>();
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject object = super.toJSONObject();
		JSONArray progressArray = new JSONArray();
		for (int i = 0; i < progress.size(); i++) {
			progressArray.put(progress.get(i));
		}
		try {
			object.put("progress", progressArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}
