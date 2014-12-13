package cn.fython.carryingcat.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.adapter.MyVideoListAdapter;
import cn.fython.carryingcat.provider.CCProvider;
import cn.fython.carryingcat.provider.VideoItemProvider;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.ui.MainActivity;
import cn.fython.carryingcat.ui.video.DetailsActivity;

public class LocalVideoFragment extends Fragment implements View.OnTouchListener {

	private MainActivity mActivity;

	private SwipeRefreshLayout refreshLayout;
	private ListView mListView;
	private MyVideoListAdapter mAdapter;

	private ArrayList<VideoItem> items;

	private VideoItemProvider[] providers;

	private float mLastY = -1.0f;

	public LocalVideoFragment() {}

	public static LocalVideoFragment newInstance() {
		LocalVideoFragment fragment = new LocalVideoFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_my_video, null);

		mActivity = (MainActivity) getActivity();

		refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
		mListView = (ListView) rootView.findViewById(R.id.listView);

		mAdapter = new MyVideoListAdapter(mActivity.getApplicationContext(), new ArrayList<VideoItem>());
		mListView.setAdapter(mAdapter);
		mListView.setOnTouchListener(this);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (items != null && !mAdapter.isEmpty()) {
					DetailsActivity.launch(
							(ActionBarActivity) getActivity(),
							new View[] {view.findViewById(R.id.iv_preview), view.findViewById(R.id.tv_title)},
							providers[0].getProviderName(),
							position
					);
				}
			}

		});

		refreshLayout.setColorSchemeResources(
				R.color.blue_500, R.color.green_500, R.color.brown_500,
				R.color.deep_purple_500, R.color.indigo_500, R.color.orange_500,
				R.color.pink_500, R.color.teal_500
		);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
				refreshList();
			}

		});

		providers = new VideoItemProvider[] {new CCProvider(mActivity.getApplicationContext())};
		refreshList();

		return rootView;
	}

	public void refreshList() {
		if (!refreshLayout.isRefreshing()) {
			refreshLayout.setRefreshing(true);
		}
		mActivity.getFloatingActionButton().showFloatingActionButton();
		new RefreshTask().execute();
	}

	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				mLastY = ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				if (mLastY == -1.0f) break;

				float y = ev.getY();

				if (y < mLastY - 10f && mListView.getFirstVisiblePosition() >= 1) {
					mActivity.getFloatingActionButton().hideFloatingActionButton();
				} else if (y > mLastY + 10f) {
					mActivity.getFloatingActionButton().showFloatingActionButton();
				}

				mLastY = y;
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mLastY = -1.0f;
				break;
		}
		return false;
	}

	public class RefreshTask extends AsyncTask<Void, Void, ArrayList<VideoItem>> {

		@Override
		protected ArrayList<VideoItem> doInBackground(Void... params) {
			ArrayList<VideoItem> temp = new ArrayList<VideoItem>();
			for (VideoItemProvider provider:providers) {
				temp.addAll(provider.getVideoList());
			}
			return temp;
		}

		@Override
		protected void onPostExecute(ArrayList<VideoItem> result) {
			items = result;
			if (result != null) {
				mAdapter = new MyVideoListAdapter(mActivity.getApplicationContext(), result);
				mListView.setAdapter(mAdapter);
			}
			refreshLayout.setRefreshing(false);
		}

	}

}
