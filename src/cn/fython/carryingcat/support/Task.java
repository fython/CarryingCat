package cn.fython.carryingcat.support;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Task {

	public String targetPath, downloadPath, title, fileName;
	public ArrayList<String> urls;
	public ArrayList<Integer> progress;
	public long downloadId;
	public int mode = 0;
	public int[] bytes;

	public Task(String targetPath, String downloadPath, String title, String fileName, ArrayList<String> urls) {
		this.targetPath = targetPath;
		this.downloadPath = downloadPath;
		this.title = title;
		this.fileName = fileName;
		this.urls = urls;
		this.progress = new ArrayList<Integer>();
	}

	public Task(JSONObject json) throws JSONException {
		JSONArray pg = json.getJSONArray("progress");

		this.progress = new ArrayList<Integer>();
		for (int i = 0; i < pg.length(); i++) {
			this.progress.add(pg.getInt(i));
		}

		JSONArray ua = json.getJSONArray("urls");

		this.urls = new ArrayList<String>();
		for (int i = 0; i < ua.length(); i++) {
			this.urls.add(ua.getString(i));
		}

		try {
			this.mode = json.getInt("mode");
		} catch (JSONException e) {

		}

		this.downloadId = json.getInt("downloadId");
		this.targetPath = json.getString("targetPath");
		this.downloadPath = json.getString("downloadPath");
		this.title = json.getString("title");
		this.fileName = json.getString("fileName");
	}

	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		JSONArray progressArray = new JSONArray();
		for (int i = 0; i < progress.size(); i++) {
			progressArray.put(progress.get(i));
		}
		JSONArray urlsArray = new JSONArray();
		for (int i = 0; i < urls.size(); i++) {
			urlsArray.put(urls.get(i));
		}
		try {
			json.put("mode", mode);
			json.put("downloadId", downloadId);
			json.put("targetPath", targetPath);
			json.put("downloadPath", downloadPath);
			json.put("title", title);
			json.put("fileName", fileName);
			json.put("progress", progressArray);
			json.put("urls", urlsArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static class Builder {

		private String targetPath, downloadPath, title, fileName;
		private ArrayList<String> urls;

		public Builder() {
			targetPath = null;
			downloadPath = FileManager.getDownloadDirPath(false);
			title = null;
			fileName = null;
			urls = new ArrayList<String>();
		}

		public Builder setTargetPath(String targetPath) {
			this.targetPath = targetPath;
			return this;
		}

		public Builder setDownloadPath(String downloadPath) {
			this.downloadPath = downloadPath;
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setFileName(String fileName) throws Exception {
			this.fileName = fileName;
			return this;
		}

		public Builder setDataFromVideoItem(VideoItem src) {
			this.targetPath = src.path;
			this.title = src.name;
			if (this.title == null) {
				this.title = src.srcs.get(0).title;
			}
			this.downloadPath += "/" + this.title;
			this.fileName = src.srcs.get(src.selectedSource).quality
					+ "." + src.srcs.get(src.selectedSource).getVideoUrl(0).type;
			for (int i = 0; i < src.srcs.get(src.selectedSource).getVideoUrlCount(); i++) {
				this.urls.add(src.srcs.get(src.selectedSource).getVideoUrl(i).url);
			}
			return this;
		}

		public Task build() {
			if (targetPath == null || title == null || fileName == null) {
				throw new NullPointerException("Target path, title or file name cannot be empty.");
			}
			return new Task(targetPath, downloadPath, title, fileName, urls);
		}

	}

}
