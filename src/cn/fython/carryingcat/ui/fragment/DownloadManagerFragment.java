package cn.fython.carryingcat.ui.fragment;

import android.app.DownloadManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.adapter.DownloadManagerListAdapter;
import cn.fython.carryingcat.provider.DownloadProvider;
import cn.fython.carryingcat.support.CompleteReceiver;
import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.Task;
import cn.fython.carryingcat.support.download.DownloadManagerPro;
import cn.fython.carryingcat.ui.MainActivity;
import me.drakeet.materialdialog.MaterialDialog;

public class DownloadManagerFragment extends Fragment implements View.OnClickListener {

	private MainActivity mActivity;

	private ListView mListView;
	private DownloadManagerListAdapter mAdapter;
	private ArrayList<Task> tasks;

	private DownloadHandler mHandler;

	private FileManager fm;
	private DownloadManager dm;
	private DownloadManagerPro dmPro;

	private DownloadChangeObserver downloadObserver;
	private ChangeReceiver changeReceiver;

	private MaterialDialog dialogDelete;

	private boolean shouldRefresh = true;

	private final static String TAG = "DownloadManagerFragment";

	public DownloadManagerFragment() {
		mHandler = new DownloadHandler();
		downloadObserver = new DownloadChangeObserver();
		changeReceiver = new ChangeReceiver();
	}

	public static DownloadManagerFragment newInstance() {
		DownloadManagerFragment fragment = new DownloadManagerFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_download_manager, null);

		mActivity = (MainActivity) getActivity();

		fm = new FileManager(mActivity.getApplicationContext());
		dm = (DownloadManager) mActivity.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
		dmPro = new DownloadManagerPro(dm);

		mActivity.getApplicationContext().registerReceiver(
				changeReceiver, new IntentFilter(CompleteReceiver.ACTION_UPDATE_PROGRESS)
		);

		mListView = (ListView) rootView.findViewById(R.id.listView);

		tasks = new DownloadProvider(mActivity.getApplicationContext()).getTaskList();

		mAdapter = new DownloadManagerListAdapter(getActivity().getApplicationContext(), tasks);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "position " + position + " is clicked.");
				switch (getTask(position).mode) {
					case DownloadManager.STATUS_RUNNING:
						dmPro.pauseDownload(getTask(position).downloadId);
						break;
					case DownloadManager.STATUS_FAILED:
						Task task = getTask(position);
						deleteTask(position, false);
						restartTask(task);
						break;
					case DownloadManager.STATUS_PENDING:
						break;
					case DownloadManager.STATUS_PAUSED:
						dmPro.resumeDownload(getTask(position).downloadId);
						break;
				}
			}

		});
		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "position " + position + " is long clicked.");
				showDeleteDialog(position);
				return false;
			}
		});

		rootView.findViewById(R.id.fl_start_all).setOnClickListener(this);
		rootView.findViewById(R.id.fl_pause_all).setOnClickListener(this);
		rootView.findViewById(R.id.fl_delete_all).setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		/** observer download change **/
		mActivity.getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, downloadObserver);
		if (shouldRefresh) {
			tasks = new DownloadProvider(mActivity.getApplicationContext()).getTaskList();
			mAdapter = new DownloadManagerListAdapter(getActivity().getApplicationContext(), tasks);
			mListView.setAdapter(mAdapter);
			mActivity.getApplicationContext()
					.getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, downloadObserver);
			for (int i = 0; i < tasks.size(); i++) updateProgress(i);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mActivity.getApplicationContext()
				.getContentResolver().unregisterContentObserver(downloadObserver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mActivity.getApplicationContext().unregisterReceiver(changeReceiver);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.fl_start_all) {
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
			return;
		} else if (id == R.id.fl_pause_all) {
			for (int i = 0; i < tasks.size(); i++) dmPro.pauseDownload(getTask(i).downloadId);
			return;
		} else if (id == R.id.fl_delete_all) {
			for (;tasks.size() != 0;) deleteTask(0, true);
			return;
		} else {
			// Nothing to do
		}
	}

	public void deleteTask(int index, boolean deleteFile) {
		try {
			dm.remove(getTask(index).downloadId);
			if (deleteFile) {
				FileManager.deleteDir(getTask(index).downloadPath);
			}
			tasks.remove(index);
			mAdapter.removeItem(index);
			mAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showDeleteDialog(final int index) {
		if (dialogDelete == null) {
			dialogDelete = new MaterialDialog(mActivity);
			dialogDelete.setMessage(R.string.download_ask_for_deleting_warning)
					.setPositiveButton(android.R.string.ok, new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							deleteTask(index, true);
							dialogDelete.dismiss();
						}
					})
					.setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialogDelete.dismiss();
						}
					})
					.setCanceledOnTouchOutside(true);
		}
		dialogDelete.setTitle(
				String.format(
						getString(R.string.download_ask_for_deleting),
						getTask(index).title
				)
		);
		dialogDelete.show();
	}

	public Task getTask(int index) {
		return tasks.get(index);
	}

	public void receiveNewTask(Task task) {
		Log.i(TAG, "receiverNewTask!");
		Log.i(TAG, "Task data: " + task.toJSONObject().toString());
		restartTask(task);
	}

	public void restartTask(Task task) {
		DownloadManager.Request request;
		request = new DownloadManager.Request(Uri.parse(task.urls.get(0)));
		request.setDestinationInExternalPublicDir(
				task.downloadPath,
				task.fileName
		);
		request.setTitle(String.format(getString(R.string.download_noti_title), task.title));
		request.setDescription(task.fileName);
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
		request.setVisibleInDownloadsUi(false);
		request.setMimeType("video/cn.fython.carryingcat");

		task.downloadId = dm.enqueue(request);
		Log.i(TAG, task.toJSONObject().toString());
		tasks.add(task);
		shouldRefresh = false;
		mAdapter = new DownloadManagerListAdapter(mActivity.getApplicationContext(), tasks);
	}

	private class DownloadHandler extends Handler {

		@Override
		public void handleMessage(Message m) {
			switch (m.what) {

			}
		}

	}

	public void updateProgress(int index) {
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
				if (!shouldRefresh) shouldRefresh = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	class DownloadChangeObserver extends ContentObserver {

		public DownloadChangeObserver() {
			super(mHandler);
		}

		@Override
		public void onChange(boolean selfChange) {
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

	static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");

	public static final int    MB_2_BYTE             = 1024 * 1024;
	public static final int    KB_2_BYTE             = 1024;

	public static CharSequence getSize(long size) {
		if (size <= 0) {
			return "0M";
		}

		if (size >= MB_2_BYTE) {
			return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double)size / MB_2_BYTE)).append("M");
		} else if (size >= KB_2_BYTE) {
			return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double)size / KB_2_BYTE)).append("K");
		} else {
			return size + "B";
		}
	}

	public static String getNotiPercent(long progress, long max) {
		int rate = 0;
		if (progress <= 0 || max <= 0) {
			rate = 0;
		} else if (progress > max) {
			rate = 100;
		} else {
			rate = (int)((double)progress / max * 100);
		}
		return new StringBuilder(16).append(rate).append("%").toString();
	}

}
