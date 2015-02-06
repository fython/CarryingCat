package cn.fython.carryingcat.ui.video;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.adapter.MyVideoListAdapter;
import cn.fython.carryingcat.provider.BiliProvider;
import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.Settings;
import cn.fython.carryingcat.support.Utility;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.ui.MainActivity;

public class MultiItemActivity extends ActionBarActivity {

	private ListView mListView;
	private SwipeRefreshLayout refreshLayout;
	private ActionBar mActionBar;

	private MyVideoListAdapter mAdapter;
	private BiliProvider mProvider;

	private ArrayList<VideoItem> items;

	private Settings mSets;

	private VideoItem rootData;

	int id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mSets = Settings.getInstance(getApplicationContext());

		if (Build.VERSION.SDK_INT == 19) {
			if (mSets.isTintEnabled()) {
				Utility.enableTint(this, new ColorDrawable(getResources().getColor(R.color.deep_purple_500)));
			}
		}

		if (Build.VERSION.SDK_INT >= 21) {
			getWindow().setNavigationBarColor(getResources().getColor(R.color.deep_purple_700));
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mutilvideo_list);

		/** Get Intent Data */
		Intent intent = getIntent();
		String providerType = intent.getStringExtra("provider_type");
		id = intent.getIntExtra("id", 0);

		mProvider = new BiliProvider(getApplicationContext(), mSets.getBilibiliPath());
		rootData = mProvider.getVideoItem(id);

		setUpActionBar();
		setUpListView();

		/** 生成预览图 */
		// 尝试读取缩略图 当无法读取时自动生成
		File file = new File(rootData.path + "/.preview");
		if (!file.exists()) {
			new Thread() {

				@Override
				public void run() {
					try {
						Bitmap refreshBitmap = FileManager.createVideoThumbnail(FileManager.findFirstVideoFile(rootData.path));
						if (refreshBitmap != null) {
							try {
								FileManager.saveBitmap(rootData.path + "/.preview", refreshBitmap);
							} catch (IOException e) {
								e.printStackTrace();
							}
							// 刷新视频缩略图
							MainActivity.mHandler.sendEmptyMessage(MainActivity.HANDLER_REFRESH_MY_VIDEO);
						}
					} catch (NullPointerException e) {

					}
				}

			}.start();
		}

		this.setTitle(rootData.name);

		refreshList();
	}

	private void setUpActionBar() {
		Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(tb);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void setUpListView() {
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
		mListView = (ListView) findViewById(R.id.listView);

		mAdapter = new MyVideoListAdapter(getApplicationContext(), new ArrayList<VideoItem>());
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (items != null && !mAdapter.isEmpty()) {
					VideoItem item = mAdapter.getItem(position);
					DetailsActivity.launch(
							MultiItemActivity.this,
							new View[]{view.findViewById(R.id.iv_preview), view.findViewById(R.id.tv_title)},
							item.providerName,
							item.providerId
					);
				}
			}

		});

		/** 初始化下拉刷新控件 */
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
	}

	public void refreshList() {
		if (!refreshLayout.isRefreshing()) {
			refreshLayout.setRefreshing(true);
		}
		new RefreshTask().execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public class RefreshTask extends AsyncTask<Void, Void, ArrayList<VideoItem>> {

		@Override
		protected ArrayList<VideoItem> doInBackground(Void... params) {
			ArrayList<VideoItem> temp = null;
			try {
				temp = mProvider.getSubvideoList(id);
			} catch (Exception e) {

			}
			return temp;
		}

		@Override
		protected void onPostExecute(ArrayList<VideoItem> result) {
			items = result;
			if (result != null) {
				mAdapter = new MyVideoListAdapter(getApplicationContext(), result);
				mListView.setAdapter(mAdapter);
			}
			refreshLayout.setRefreshing(false);
		}

	}

	public static void launch(ActionBarActivity activity, String providerName, int id) {
		Intent intent = new Intent(activity, MultiItemActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		intent.putExtra("provider_type", providerName);
		intent.putExtra("id", id);
		ActivityCompat.startActivity(activity, intent, new Bundle());
	}

}
