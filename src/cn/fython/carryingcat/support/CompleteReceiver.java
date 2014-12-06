package cn.fython.carryingcat.support;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.fython.carryingcat.provider.DownloadProvider;
import cn.fython.carryingcat.support.download.DownloadManagerPro;
import cn.fython.carryingcat.ui.fragment.DownloadManagerFragment;

public class CompleteReceiver extends BroadcastReceiver {

	public static final String ACTION_UPDATE_PROGRESS = "cn.fython.carryingcat.broadcast.ACTION_UPDATE_PROGRESS";

	DownloadProvider provider = null;
	ArrayList<Task> array;

	DownloadManager dm;
	DownloadManagerPro dmPro;

	public static final String TAG = "CompleteReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive, " + intent.toString());
		if (provider == null) {
			provider = new DownloadProvider(context);
		}
		if (dm == null) {
			dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		}
		if (dmPro == null) {
			dmPro = new DownloadManagerPro(dm);
		}
		array = provider.getTaskList();
		long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

		for (int i = 0; i < array.size(); i++) {
			Task task = array.get(i);
			Log.i(TAG, task.toJSONObject().toString());
			long downloadId = task.downloadId;

			Log.i(TAG, "completeDownloadId" + completeDownloadId);
			if (completeDownloadId == downloadId) {
				sendUpdateBroadcast(context, i, false);
				if (dmPro.getStatusById(downloadId) == DownloadManager.STATUS_SUCCESSFUL) {
					String oldPath = Environment.getExternalStorageDirectory() + task.downloadPath;

					// 重新生成视频大小信息
					try {
						String videoPath = FileManager.findFirstVideoFile(oldPath);
						File vf = new File(videoPath);
						VideoItem vi = new VideoItem(new JSONObject(FileManager.readFile(oldPath + "/data.json")));
						vi.srcs.get(vi.selectedSource).getVideoUrl(0).size =
								String.valueOf(DownloadManagerFragment.getSize(vf.length()));
						FileManager.saveFile(oldPath + "/data.json", vi.toJSONObject().toString());
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// 从临时下载目录复制到视频目录
					try {
						FileManager.copyDirectory(new File(oldPath), new File(task.targetPath));
						FileManager.deleteDir(Environment.getExternalStorageDirectory() + oldPath);
					} catch (IOException e) {
						e.printStackTrace();
					}
					sendUpdateBroadcast(context, i, true);
				}
			}

		}
	}

	private void sendUpdateBroadcast(Context context, int id, boolean forDeleting) {
		Intent intent = new Intent();
		intent.setAction(ACTION_UPDATE_PROGRESS);
		intent.putExtra("id", id);
		intent.putExtra("delete", forDeleting);
		context.sendBroadcast(intent);
	}

}
