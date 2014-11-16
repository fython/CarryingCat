package cn.fython.carryingcat.support;

import android.app.DownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.fython.carryingcat.support.api.FlvxzTools;

public class VideoItemTask extends VideoItem {

	public ArrayList<Integer> progress;
	public long downloadId;
	public int mode;
	public int[] bytes;

	public VideoItemTask(VideoItem videoItem) {
		JSONObject jsonObject = videoItem.toJSONObject();
		try {
			this.srcs = FlvxzTools.getVideoSource(jsonObject.getJSONArray("sources"));
			this.selectedSource = jsonObject.getInt("selectedSource");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.progress = new ArrayList<Integer>();
		this.downloadId = -1;
		this.mode = DownloadManager.STATUS_PENDING;
		this.bytes = new int[] {0,0};
	}

	public VideoItemTask(JSONObject jsonObject) throws JSONException {
		this.srcs = FlvxzTools.getVideoSource(jsonObject.getJSONArray("sources"));
		this.selectedSource = jsonObject.getInt("selectedSource");
		JSONArray pg = jsonObject.getJSONArray("progress");
		this.progress = new ArrayList<Integer>();
		for (int i = 0; i < pg.length(); i++) {
			this.progress.add(pg.getInt(i));
		}
		this.downloadId = jsonObject.getLong("downloadId");
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
			object.put("downloadId", downloadId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}
