package cn.fython.carryingcat.provider;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.JSONHelper;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.support.VideoSource;
import cn.fython.carryingcat.support.VideoUrl;
import cn.fython.carryingcat.ui.fragment.DownloadManagerFragment;

public class BiliProvider extends VideoItemProvider {

	private String biliPath;
	public static final String TAG = "BiliProvider";
	public static final String SUBVIDEO_PROVIDER_NAME = "Bilibili-Subvideo";

	public BiliProvider(Context context, String biliPath) {
		super(context);
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
					VideoItem v = readBilibiliEntryJson(FileManager.readFile(p.get(0) + "/entry.json"), true, false);
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
					VideoItem v = readBilibiliEntryJson(FileManager.readFile(p.get(0) + "/entry.json"), false, true);
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
				v = readBilibiliEntryJson(FileManager.readFile(p.get(0) + "/entry.json"), false, false);
				v.providerId = id;
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (p.size() > 1) {
			// 多P
			try {
				v = readBilibiliEntryJson(FileManager.readFile(p.get(0) + "/entry.json"), false, true);
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

	public ArrayList<VideoItem> getSubvideoList(int id) {
		ArrayList<VideoItem> result = new ArrayList<VideoItem>();

		String rootPath = getVideoItem(id).path;
		rootPath = rootPath.substring(0, rootPath.lastIndexOf("/"));
		Log.i(TAG, "Subvideo Root Path: " + rootPath);

		ArrayList<String> dirs = FileManager.getPathsInPath(rootPath);
		int sid = id * 100;
		for (String dir : dirs) {
			try {
				VideoItem v = readBilibiliEntryJson(FileManager.readFile(dir + "/entry.json"), true, false);
				v.providerName = SUBVIDEO_PROVIDER_NAME;
				v.providerId = sid;
				result.add(v);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			sid++;
		}

		return result;
	}

	/**
	 * SID计算方法: Provider生成序号 * 100 + Subvideo序号
	 * 很脑瘫的方法就不要吐槽了 2333 主要还是太懒 (B站如果有超过100个P的视频我就爆炸了哈哈
	 * */
	public VideoItem getSubvideo(int sid) {
		int vid = sid / 100;
		int pid = sid % 100;

		String rootPath = getVideoItem(vid).path;
		rootPath = rootPath.substring(0, rootPath.lastIndexOf("/"));
		Log.i(TAG, "Subvideo Root Path: " + rootPath);

		String dir = FileManager.getPathsInPath(rootPath).get(pid);

		VideoItem v = null;

		try {
			v = readBilibiliEntryJson(FileManager.readFile(dir + "/entry.json"), true, false);
			v.providerName = SUBVIDEO_PROVIDER_NAME;
			v.providerId = sid;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return v;
	}

	@Override
	public String getProviderName() {
		return "Bilibili";
	}

	private VideoItem readBilibiliEntryJson(String json, boolean useSubtitle, boolean isDir) throws JSONException {
		JSONHelper d = new JSONHelper(new JSONObject(json));
		VideoItem v = new VideoItem();
		v.name = !useSubtitle ? d.readString("title") : d.readJSONObject("page_data").readString("part");
		v.path = d.readString("storage_path");
		if (v.path.indexOf("entry.json") != -1) {
			v.path = v.path.substring(0, v.path.lastIndexOf("entry.json") - 1);
		}
		if (FileManager.findFirstVideoFile(v.path) == null) {
			try {
				v.path = FileManager.getPathsInPath(v.path).get(0);
			} catch (Exception e) {
				Log.e(TAG, "Couldn't find directory.");
			}
		}

		VideoSource vs = new VideoSource(v.name);
		VideoUrl fakeUrl = new VideoUrl();
		if (!isDir) {
			fakeUrl.size = String.valueOf(DownloadManagerFragment.getSize(d.readInt("total_bytes", 0)));
			SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
			String time = formatter.format(d.readInt("total_time_milli", 0));
			fakeUrl.time = time;
		} else {
			try {
				String folderPath = v.path.substring(0, v.path.lastIndexOf("/"));
				fakeUrl.size = String.valueOf(DownloadManagerFragment.getSize(FileManager.getFolderSize(new File(folderPath))));
				fakeUrl.time = String.format(mContext.getString(R.string.multivideo_count_title), FileManager.getPathsInPath(folderPath).size());
			} catch (Exception e) {
				fakeUrl.size = "";
				fakeUrl.time = "";
				e.printStackTrace();
			}
		}
		vs.addVideoUrl(fakeUrl);
		v.srcs.add(vs);
		v.providerName = "Bilibili";

		return v;
	}

}
