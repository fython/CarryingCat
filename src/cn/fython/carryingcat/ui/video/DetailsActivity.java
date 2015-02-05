package cn.fython.carryingcat.ui.video;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.adapter.OperationListAdapter;
import cn.fython.carryingcat.provider.BiliProvider;
import cn.fython.carryingcat.provider.CCProvider;
import cn.fython.carryingcat.provider.VideoItemProvider;
import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.Settings;
import cn.fython.carryingcat.support.Utility;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.ui.MainActivity;
import cn.fython.carryingcat.ui.fragment.DownloadManagerFragment;

import static cn.fython.carryingcat.adapter.OperationListAdapter.*;

public class DetailsActivity extends ActionBarActivity {

	private static final int FLAG_REFRESH_PICTURE = 0;

	private ActionBar mActionBar;
	private ImageView iv_preview;
	private ListView lv_opeartion;
	private OperationListAdapter mAdapter;

	private Settings mSets;

	private VideoItemProvider provider;
	private VideoItem item;

	private static final String TAG = "DetailsActivity";

	private static final String OPERATION_DELETE = "delete", OPERATION_SHARE_INTENT = "share_intent";

	public static final String EXTRA_IMAGE = "DetailActivity:image", EXTRA_TITLE = "DetailActivity:title";

	private AlertDialog dialogDelete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mSets = Settings.getInstance(getApplicationContext());

		if (Build.VERSION.SDK_INT == 19) {
			if (mSets.isTintEnabled()) {
				Utility.enableTint(this, new ColorDrawable(getResources().getColor(R.color.deep_purple_500)));
			}
		}

		if (Build.VERSION.SDK_INT >= 21) {
			getWindow().setNavigationBarColor(getResources().getColor(R.color.deep_purple_500));
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		setUpActionBar();

		/** get Intent data **/
		Intent intent = getIntent();
		String providerType = intent.getStringExtra("provider_type");
		int id = intent.getIntExtra("id", 0);
		if (providerType.equals("carryingcat")) {
			provider = new CCProvider(getApplicationContext());
		}
		if (providerType.equals("Bilibili")) {
			provider = new BiliProvider(getApplicationContext(), mSets.getBilibiliPath());
		}
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		iv_preview = (ImageView) findViewById(R.id.iv_preview);
		ViewCompat.setTransitionName(tv_title, EXTRA_TITLE);
		ViewCompat.setTransitionName(iv_preview, EXTRA_IMAGE);
		try {
			item = provider.getVideoItem(id);
			Log.i(TAG, item.toJSONObject().toString());
			tv_title.setText(item.srcs.get(0).title);

			// 尝试读取缩略图 当无法读取时自动生成
			File file = new File(item.path + "/.preview");
			if (file.exists()) {
				Picasso.with(getApplicationContext()).load(file).into(iv_preview);
			} else {
				new Thread() {

					@Override
					public void run() {
						try {
							Bitmap refreshBitmap = FileManager.createVideoThumbnail(FileManager.findFirstVideoFile(item.path));
							if (refreshBitmap != null) {
								try {
									FileManager.saveBitmap(item.path + "/.preview", refreshBitmap);
								} catch (IOException e) {
									e.printStackTrace();
								}
								// 刷新视频缩略图
								mHandler.sendEmptyMessage(FLAG_REFRESH_PICTURE);
								MainActivity.mHandler.sendEmptyMessage(MainActivity.HANDLER_REFRESH_MY_VIDEO);
							}
						} catch (NullPointerException e) {

						}
					}

				}.start();
			}

			// 重新生成大小
			if (provider.getProviderName() == "carryingcat") {
				new Thread() {
					@Override
					public void run() {
						try {
							String videoPath = FileManager.findFirstVideoFile(item.path);
							File vf = new File(videoPath);
							VideoItem vi = new VideoItem(new JSONObject(FileManager.readFile(item.path + "/data.json")));
							vi.srcs.get(vi.selectedSource).getVideoUrl(0).size = String.valueOf(DownloadManagerFragment.getSize(vf.length()));
							FileManager.saveFile(item.path + "/data.json", vi.toJSONObject().toString());
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}

			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.setDataAndType(Uri.parse(FileManager.findFirstVideoFile(item.path)), "video/*");
					startActivity(i);
				}

			});

			bindOperationList();
		} catch (Exception e) {
			e.printStackTrace();
			tv_title.setText(R.string.result_unavailable);

			// 创建不可用的 FAB
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.hide();

			lv_opeartion = (ListView) findViewById(R.id.listView);

			mAdapter = new OperationListAdapter(getApplicationContext(), new ArrayList<OperationListAdapter.OperationItem>(), android.R.color.white);
			addOperation(getString(R.string.result_unavailable), null, null);

			lv_opeartion.setAdapter(mAdapter);
		}


	}

	private void bindOperationList() {
		lv_opeartion = (ListView) findViewById(R.id.listView);

		mAdapter = new OperationListAdapter(getApplicationContext(), new ArrayList<OperationListAdapter.OperationItem>(), android.R.color.white);
		addOperation(getString(R.string.operation_delete), OPERATION_DELETE, getResources().getDrawable(R.drawable.ic_delete_white_24dp));
		addOperation(getString(R.string.operation_share), OPERATION_SHARE_INTENT, getResources().getDrawable(R.drawable.ic_share_white_24dp));

		lv_opeartion.setAdapter(mAdapter);

		lv_opeartion.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				String key = mAdapter.getItem(i).key;
				if (key.contains(OPERATION_DELETE)) {
					showDeleteDialog();
					return;
				}
				if (key.contains(OPERATION_SHARE_INTENT)) {
					File f = new File(FileManager.findFirstVideoFile(item.path));
					ShareCompat.IntentBuilder.from(DetailsActivity.this)
							.addStream(Uri.fromFile(f))
							.setType("video/*")
							.setText(item.srcs.get(0).title)
							.startChooser();
					return;
				}
			}

		});
	}

	private void addOperation(String title, String key, Drawable icon) {
		mAdapter.addItem(
				icon != null ?
						(new OperationItem(icon, title, key)) :
						(new OperationItem(title, key))
		);
	}

	private void showDeleteDialog() {
		if (dialogDelete == null) {
			dialogDelete = new AlertDialog.Builder(this)
					.setMessage(R.string.download_ask_for_deleting_warning)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface d, int which) {
							FileManager.deleteDir(item.path);
							MainActivity.mHandler.sendEmptyMessage(MainActivity.HANDLER_DELETE_SUCCESSFUL);
							MainActivity.mHandler.sendEmptyMessage(MainActivity.HANDLER_REFRESH_MY_VIDEO);
							finish();
						}
					})
					.setNegativeButton(android.R.string.cancel, null)
					.create();
		}
		dialogDelete.setTitle(
				String.format(
						getString(R.string.download_ask_for_deleting),
						item.srcs.get(0).title
				)
		);
		dialogDelete.show();
	}

	private void setUpActionBar() {
		Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(tb);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setTitle("");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			super.onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static void launch(ActionBarActivity activity, View[] translationView, String providerName, int id) {
		Pair<View, String> pair0 = new Pair<View, String>(translationView[0], EXTRA_IMAGE),
				pair1 = new Pair<View, String>(translationView[1], EXTRA_TITLE);
		ActivityOptionsCompat options =
				ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pair0, pair1);
		Intent intent = new Intent(activity, DetailsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		intent.putExtra("provider_type", providerName);
		intent.putExtra("id", id);
		ActivityCompat.startActivity(activity, intent, options.toBundle());
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case FLAG_REFRESH_PICTURE:
					File file = new File(item.path + "/.preview");
					if (file.exists()) {
						Picasso.with(getApplicationContext()).load(file).into(iv_preview);
					}
					break;
			}
		}

	};

}
