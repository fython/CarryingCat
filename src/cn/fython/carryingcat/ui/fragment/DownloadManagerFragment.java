package cn.fython.carryingcat.ui.fragment;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.DecimalFormat;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.CompleteReceiver;
import cn.fython.carryingcat.support.download.DownloadManagerHelper;
import cn.fython.carryingcat.support.Task;
import cn.fython.carryingcat.support.download.DownloadManagerPro;
import cn.fython.carryingcat.ui.MainActivity;

public class DownloadManagerFragment extends Fragment {

	private MainActivity mActivity;

	private ListView mListView;
	private DownloadManagerHelper mHelper;
	private DownloadHandler mHandler;

	private AlertDialog dialogDelete;

	private final static String TAG = "DownloadManagerFragment";

	public DownloadManagerFragment() {
		mHandler = new DownloadHandler();
		mHelper = new DownloadManagerHelper(mHandler);
		this.setHasOptionsMenu(true);
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

		mActivity.getApplicationContext().registerReceiver(
				mHelper.getChangeReceiver(), new IntentFilter(CompleteReceiver.ACTION_UPDATE_PROGRESS)
		);

		mListView = (ListView) rootView.findViewById(R.id.listView);

		mHelper.initDataFromProvider();
		mHelper.bindListView(mListView);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "position " + position + " is clicked.");
				switch (mHelper.getTask(position).mode) {
					case DownloadManager.STATUS_RUNNING:
						mHelper.pauseTask(position);
						break;
					case DownloadManager.STATUS_PAUSED:
					case DownloadManager.STATUS_FAILED:
						Task task = mHelper.getTask(position);
						mHelper.deleteTask(position, false);
						mHelper.restartTask(mActivity.getApplicationContext(), task);
						break;
					case DownloadManager.STATUS_PENDING:
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

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		/** observer download change **/
		mActivity.getContentResolver().registerContentObserver(
				DownloadManagerPro.CONTENT_URI, true, mHelper.getDownloadObserver()
		);
		if (mHelper.shouldRefresh) {
			mHelper.initDataFromProvider();
			mHelper.initAdapter();
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
	public void onPrepareOptionsMenu(Menu menu) {
		mActivity.getMenuInflater().inflate(R.menu.download_manager, menu);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.start_all) {
			mHelper.restartAll();
			return true;
		} else if (id == R.id.pause_all) {
			mHelper.pauseAll();
			return true;
		} else if (id == R.id.delete_all) {
			mHelper.deleteAll();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showDeleteDialog(final int index) {
		if (dialogDelete == null) {
			dialogDelete = new AlertDialog.Builder(mActivity)
					.setMessage(R.string.download_ask_for_deleting_warning)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface d, int which) {
							mHelper.deleteTask(index, true);
							dialogDelete.dismiss();
						}
					})
					.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface d, int which) {
							dialogDelete.dismiss();
						}
					})
					.create();
		}
		dialogDelete.setTitle(
				String.format(
						getString(R.string.download_ask_for_deleting),
						mHelper.getTask(index).title
				)
		);
		dialogDelete.show();
	}

	public void receiveNewTask(Context mContext, Task task) {
		Log.i(TAG, "receiverNewTask!");
		Log.i(TAG, "Task data: " + task.toJSONObject().toString());
		mHelper.restartTask(mContext, task);
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

}
