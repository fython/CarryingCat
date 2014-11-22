package cn.fython.carryingcat.provider;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.VideoItem;

public class CCProvider extends VideoItemProvider {

	private FileManager fm;

	public CCProvider(Context context) {
		super(context);
		fm = new FileManager(context);
	}

	@Override
	public ArrayList<VideoItem> getVideoList() {
		ArrayList<VideoItem> items = new ArrayList<VideoItem>();

		ArrayList<String> dirs = fm.getPathsInPath(FileManager.getMyVideoDirPath());
		for (String dir:dirs) {
			try {
				VideoItem v = new VideoItem(new JSONObject(fm.readFile(dir + "/data.json")));
				items.add(v);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return items;
	}

}
