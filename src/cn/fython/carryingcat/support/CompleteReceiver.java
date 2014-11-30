package cn.fython.carryingcat.support;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.fython.carryingcat.provider.DownloadProvider;
import cn.fython.carryingcat.support.download.DownloadManagerPro;

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
					String oldPath = task.downloadPath;
					try {
						FileManager.copyDirectory(new File(oldPath), new File(task.targetPath));
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
