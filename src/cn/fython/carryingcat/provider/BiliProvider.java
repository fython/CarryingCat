package cn.fython.carryingcat.provider;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.JSONHelper;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.support.VideoSource;
import cn.fython.carryingcat.support.VideoUrl;
import cn.fython.carryingcat.support.download.DownloadManagerPro;
import cn.fython.carryingcat.ui.fragment.DownloadManagerFragment;

public class BiliProvider extends VideoItemProvider {

	public String biliPath;
	public FileManager fm;

	public BiliProvider(Context context) {
		super(context);
		fm = new FileManager(context);
		this.biliPath = FileManager.getSDCardRootPath() + "/Android/data/tv.danmaku.bili/download";
	}

	public BiliProvider(Context context, String biliPath) {
		super(context);
		fm = new FileManager(context);
		this.biliPath = biliPath;
	}

	@Override
	public ArrayList<VideoItem> getVideoList() {
		ArrayList<String> dirs = FileManager.getPathsInPath(biliPath);
		ArrayList<VideoItem> result = new ArrayList<VideoItem>();
		int id = 0;
		for (String dir: dirs) {
			ArrayList<String> p = FileManager.getPathsInPath(dir);
			if (p.size() <= 0) {
				continue;
			}
			if (p.size() == 1) {
				// 只有1P
				try {
					VideoItem v = readBilibiliEntryJson(FileManager.readFile(p.get(0) + "/entry.json"), true);
					v.providerId = id;
					result.add(v);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (p.size() > 1) {
				// 多P
				try {
					VideoItem v = readBilibiliEntryJson(FileManager.readFile(p.get(0) + "/entry.json"), false);
					v.providerId = id;
					v.isDir = true;
					result.add(v);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			id++;
		}
		return result;
	}

	@Override
	public VideoItem getVideoItem(int id){
		VideoItem v = null;
		String dir = FileManager.getPathsInPath(biliPath).get(id);
		ArrayList<String> p = FileManager.getPathsInPath(dir);
		if (p.size() == 1) {
			// 只有1P
			try {
				v = readBilibiliEntryJson(FileManager.readFile(p.get(0) + "/entry.json"), false);
				v.providerId = id;
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (p.size() > 1) {
			// 多P
			try {
				v = readBilibiliEntryJson(FileManager.readFile(p.get(0) + "/entry.json"), false);
				v.providerId = id;
				v.isDir = true;
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return v;
	}

	@Override
	public String getProviderName() {
		return "Bilibili";
	}

	private VideoItem readBilibiliEntryJson(String json, boolean needSubtitle) throws JSONException {
		JSONHelper d = new JSONHelper(new JSONObject(json));
		VideoItem v = new VideoItem();
		v.name = d.readString("title");
		if (needSubtitle) {
			v.name += " " + d.readJSONObject("page_data").readString("part");
		}
		v.path = d.readString("storage_path");
		if (v.path.indexOf("entry.json") != -1) {
			v.path = v.path.substring(0, v.path.lastIndexOf("entry.json") - 1);
		}

		VideoSource vs = new VideoSource(v.name);
		VideoUrl fakeUrl = new VideoUrl();
		fakeUrl.size = String.valueOf(DownloadManagerFragment.getSize(d.readInt("total_bytes", 0)));
		SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
		String time = formatter.format(d.readInt("total_time_milli", 0));
		fakeUrl.time = time;
		vs.addVideoUrl(fakeUrl);
		v.srcs.add(vs);
		v.providerName = "Bilibili";

		return v;
	}

}
