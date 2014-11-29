package cn.fython.carryingcat.provider;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.VideoItemTask;

public class DownloadProvider{

	private Context context;
	private FileManager fm;

	public DownloadProvider(Context context) {
		this.context = context;
		fm = new FileManager(context);
	}

	public ArrayList<VideoItemTask> getTaskList() {
		ArrayList<VideoItemTask> items = new ArrayList<VideoItemTask>();

		ArrayList<String> dirs = fm.getPathsInPath(FileManager.getDownloadDirPath(true));
		for (String dir:dirs) {
			try {
				VideoItemTask v = new VideoItemTask(new JSONObject(fm.readFile(dir + "/data.json")));
				items.add(v);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return items;
	}

	public String getProviderName() {
		return "downloadmanager";
	}

}
