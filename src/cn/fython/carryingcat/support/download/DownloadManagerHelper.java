package cn.fython.carryingcat.support.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.adapter.DownloadManagerListAdapter;
import cn.fython.carryingcat.provider.DownloadProvider;
import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.Task;
import cn.fython.carryingcat.ui.MainActivity;
import cn.fython.carryingcat.ui.fragment.DownloadManagerFragment;

public class DownloadManagerHelper {

	private Context mContext;
	private DownloadManagerFragment.DownloadHandler mHandler;

	private ArrayList<Task> tasks;
	private ListView mListView;
	private DownloadManagerListAdapter mAdapter;

	private DownloadChangeObserver downloadObserver;
	private ChangeReceiver changeReceiver;

	private DownloadManager dm;
	private DownloadManagerPro dmPro;

	public boolean shouldRefresh = false;

	private static final String TAG = "DownloadManagerHelper";

	public DownloadManagerHelper(DownloadManagerFragment.DownloadHandler mHandler) {
		this.mHandler = mHandler;
		this.downloadObserver = new DownloadChangeObserver();
		this.changeReceiver = new ChangeReceiver();
	}

	public void init(Context context) {
		this.mContext = context;
		dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
		dmPro = new DownloadManagerPro(dm);
	}

	public void bindListView(ListView listView) {
		this.mListView = listView;
		initAdapter();
	}

	public void initDataFromProvider() {
		tasks = new DownloadProvider(mContext).getTaskList();
	}

	public void initAdapter() {
		mAdapter = new DownloadManagerListAdapter(mContext, tasks);
		if (mListView != null) {
			mListView.setAdapter(mAdapter);
		}
	}

	public void updateProgress() {
		for (int i = 0; i < tasks.size(); i++) updateProgress(i);
	}

	public Task getTask(int index) {
		return tasks.get(index);
	}

	public void pauseTask(int position) {
		dmPro.pauseDownload(getTask(position).downloadId);
	}

	public void resumeTask(int position) {
		dmPro.resumeDownload(getTask(position).downloadId);
	}

	public void deleteTask(int index, boolean deleteFile) {
		try {
			dm.remove(getTask(index).downloadId);
			if (deleteFile) {
				FileManager.deleteDir(Environment.getExternalStorageDirectory() + getTask(index).downloadPath);
			}
			tasks.remove(index);
			mAdapter.removeItem(index);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAdapter.notifyDataSetChanged();
	}

	public void restartAll() {
		for (int i = 0; i < tasks.size(); i++) {
			switch (getTask(i).mode) {
				case DownloadManager.STATUS_FAILED:
					Task task = getTask(i);
					deleteTask(i, false);
					restartTask(task);
					break;
				case DownloadManager.STATUS_PAUSED:
					dmPro.resumeDownload(getTask(i).downloadId);
					break;
			}
		}
	}

	public void pauseAll() {
		for (int i = 0; i < tasks.size(); i++) dmPro.pauseDownload(getTask(i).downloadId);
	}

	public void deleteAll() {
		for (;tasks.size() != 0;) deleteTask(0, true);
	}

	public void restartTask(Task task) {
		DownloadManager.Request request;
		request = new DownloadManager.Request(Uri.parse(task.urls.get(0)));
		request.setDestinationInExternalPublicDir(
				task.downloadPath,
				task.fileName
		);
		request.setTitle(String.format(mContext.getString(R.string.download_noti_title), task.title));
		request.setDescription(task.fileName);
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
		request.setVisibleInDownloadsUi(false);
		request.setMimeType("video/cn.fython.carryingcat");

		task.downloadId = dm.enqueue(request);
		Log.i(TAG, task.toJSONObject().toString());
		tasks.add(task);
		initAdapter();
	}

	public void updateProgress(int index) {
		shouldRefresh = false;
		Task task = getTask(index);
		Log.i(TAG, "update:" + task.toJSONObject().toString());

		int[] bytesAndStatus = dmPro.getBytesAndStatus(task.downloadId);
		task.bytes = dmPro.getDownloadBytes(task.downloadId);
		if (task.progress.size() < 1) {
			task.progress.add((int) ((double) bytesAndStatus[0] / bytesAndStatus[1] * 100));
		} else {
			task.progress.set(0, (int) ((double) bytesAndStatus[0] / bytesAndStatus[1] * 100));
		}
		task.mode = bytesAndStatus[2];
		if (task.mode == DownloadManager.STATUS_SUCCESSFUL) {
			MainActivity.mHandler.sendEmptyMessage(MainActivity.HANDLER_REFRESH_MY_VIDEO);
		}
		mAdapter.setItem(index, task);
		mAdapter.notifyDataSetChanged();

		new SaveThread(index).start();
	}

	private class SaveThread extends Thread {

		private int index;

		public SaveThread(int index) {
			this.index = index;
		}

		@Override
		public void run() {
			try {
				FileManager.saveFile(
						Environment.getExternalStorageDirectory() +
								getTask(index).downloadPath + "/task.json",
						getTask(index).toJSONObject().toString()
				);
				shouldRefresh = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public DownloadChangeObserver getDownloadObserver() {
		return downloadObserver;
	}

	public ChangeReceiver getChangeReceiver() {
		return changeReceiver;
	}

	class DownloadChangeObserver extends ContentObserver {

		public DownloadChangeObserver() {
			super(mHandler);
		}

		@Override
		public void onChange(boolean selfChange) {
			Log.i(TAG, "onChange");
			for (int i = 0; i < tasks.size(); i++) updateProgress(i);
		}

	}

	class ChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int id = intent.getIntExtra("id", -1);
			boolean delete = intent.getBooleanExtra("delete", false);
			if (id != -1) {
				try {
					updateProgress(id);
				} catch (Exception e) {

				}
				if (delete) {
					deleteTask(id, false);
				}
			}

		}

	}

}
