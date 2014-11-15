package cn.fython.carryingcat.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.adapter.DownloadManagerListAdapter;
import cn.fython.carryingcat.support.VideoItemTask;

public class DownloadManagerFragment extends Fragment implements View.OnClickListener {

	private ListView mListView;
	private DownloadManagerListAdapter mAdapter;

	private final static String TAG = "DownloadManagerFragment";

	public DownloadManagerFragment() {}

	public static DownloadManagerFragment newInstance() {
		DownloadManagerFragment fragment = new DownloadManagerFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_download_manager, null);

		mListView = (ListView) rootView.findViewById(R.id.listView);

		mAdapter = new DownloadManagerListAdapter(getActivity().getApplicationContext(), new ArrayList<VideoItemTask>());
		mListView.setAdapter(mAdapter);

		rootView.findViewById(R.id.fl_start_all).setOnClickListener(this);
		rootView.findViewById(R.id.fl_pause_all).setOnClickListener(this);
		rootView.findViewById(R.id.fl_delete_all).setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.fl_start_all) {

			return;
		} else if (id == R.id.fl_pause_all) {

			return;
		} else if (id == R.id.fl_delete_all) {

			return;
		} else {
			// Nothing to do
		}
	}

	public void receiveNewTask(VideoItemTask task) {
		Log.i(TAG, "receiverNewTask!");
		Log.i(TAG, "Task data: " + task.toJSONObject().toString());
		mAdapter.addItem(task);
		mAdapter.notifyDataSetChanged();
	}

}
