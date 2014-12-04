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
import cn.fython.carryingcat.support.DownloadManagerHelper;
import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.Task;
import cn.fython.carryingcat.support.download.DownloadManagerPro;
import cn.fython.carryingcat.ui.MainActivity;
import me.drakeet.materialdialog.MaterialDialog;

public class DownloadManagerFragment extends Fragment implements View.OnClickListener {

	private MainActivity mActivity;

	private ListView mListView;
	private DownloadManagerHelper mHelper;

	private DownloadHandler mHandler;

	private FileManager fm;
	private DownloadManager dm;
	private DownloadManagerPro dmPro;

	private MaterialDialog dialogDelete;

	private boolean shouldRefresh = true;

	private final static String TAG = "DownloadManagerFragment";

	public DownloadManagerFragment() {
		mHandler = new DownloadHandler();
		mHelper = new DownloadManagerHelper(mHandler);
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

		mHelper.init(mActivity.getApplicationContext());

		fm = new FileManager(mActivity.getApplicationContext());
		dm = (DownloadManager) mActivity.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
		dmPro = new DownloadManagerPro(dm);

		mActivity.getApplicationContext().registerReceiver(
				mHelper.getChangeReceiver(), new IntentFilter(CompleteReceiver.ACTION_UPDATE_PROGRESS)
		);

		mListView = (ListView) rootView.findViewById(R.id.listView);

		mHelper.bindListView(mListView);
		mHelper.initDataFromProvider();
		mHelper.initAdapter();

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "position " + position + " is clicked.");
				switch (mHelper.getTask(position).mode) {
					case DownloadManager.STATUS_RUNNING:
						dmPro.pauseDownload(mHelper.getTask(position).downloadId);
						break;
					case DownloadManager.STATUS_FAILED:
						Task task = mHelper.getTask(position);
						mHelper.deleteTask(position, false);
						mHelper.restartTask(task);
						break;
					case DownloadManager.STATUS_PENDING:
						break;
					case DownloadManager.STATUS_PAUSED:
						dmPro.resumeDownload(mHelper.getTask(position).downloadId);
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
		mActivity.getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, mHelper.getDownloadObserver());
		if (shouldRefresh) {
			mActivity.getApplicationContext()
					.getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, mHelper.getDownloadObserver());
			mHelper.initDataFromProvider();
			mHelper.updateProgress();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mActivity.getApplicationContext()
				.getContentResolver().unregisterContentObserver(mHelper.getDownloadObserver());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mActivity.getApplicationContext().unregisterReceiver(mHelper.getChangeReceiver());
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.fl_start_all) {
			mHelper.restartAll();
			return;
		} else if (id == R.id.fl_pause_all) {
			mHelper.pauseAll();
			return;
		} else if (id == R.id.fl_delete_all) {
			mHelper.deleteAll();
			return;
		} else {
			// Nothing to do
		}
	}

	public void showDeleteDialog(final int index) {
		if (dialogDelete == null) {
			dialogDelete = new MaterialDialog(mActivity);
			dialogDelete.setMessage(R.string.download_ask_for_deleting_warning)
					.setPositiveButton(android.R.string.ok, new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mHelper.deleteTask(index, true);
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
						mHelper.getTask(index).title
				)
		);
		dialogDelete.show();
	}

	public void receiveNewTask(Task task) {
		Log.i(TAG, "receiverNewTask!");
		Log.i(TAG, "Task data: " + task.toJSONObject().toString());
		mHelper.restartTask(task);
	}

	public class DownloadHandler extends Handler {

		@Override
		public void handleMessage(Message m) {
			switch (m.what) {

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
