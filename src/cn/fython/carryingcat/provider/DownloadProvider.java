package cn.fython.carryingcat.provider;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.Task;

public class DownloadProvider{

	private Context context;
	private FileManager fm;

	public DownloadProvider(Context context) {
		this.context = context;
		fm = new FileManager(context);
	}

	public ArrayList<Task> getTaskList() {
		ArrayList<Task> items = new ArrayList<Task>();

		ArrayList<String> dirs = fm.getPathsInPath(FileManager.getDownloadDirPath(true));
		for (String dir:dirs) {
			try {
				Log.d(getProviderName(), "Checking:" + dir);
				Task v = new Task(new JSONObject(fm.readFile(dir + "/task.json")));
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
