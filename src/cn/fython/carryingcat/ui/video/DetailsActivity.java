package cn.fython.carryingcat.ui.video;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.provider.CCProvider;
import cn.fython.carryingcat.provider.VideoItemProvider;
import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.ui.MainActivity;
import cn.fython.carryingcat.ui.fragment.DownloadManagerFragment;
import cn.fython.carryingcat.view.FloatingActionButton;

public class DetailsActivity extends ActionBarActivity {

	private static final int FLAG_REFRESH_PICTURE = 0;

	private ActionBar mActionBar;
	private ImageView iv_preview;

	private VideoItemProvider provider;
	private VideoItem item;

	private static final String TAG = "DetailsActivity";

	public static final String EXTRA_IMAGE = "DetailActivity:image", EXTRA_TITLE = "DetailActivity:title";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		setUpActionBar();

		/** get Intent data **/
		Intent intent = getIntent();
		String providerType = intent.getStringExtra("provider_type");
		if (providerType.equals("carryingcat")) {
			provider = new CCProvider(getApplicationContext());
		}
		int id = intent.getIntExtra("id", 0);
		item = provider.getVideoList().get(id);
		ArrayList<VideoItem> temp = new ArrayList<VideoItem>();
		temp.add(item);

		Log.i(TAG, item.toJSONObject().toString());

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		ViewCompat.setTransitionName(tv_title, EXTRA_TITLE);
		tv_title.setText(item.srcs.get(0).title);

		iv_preview = (ImageView) findViewById(R.id.iv_preview);
		ViewCompat.setTransitionName(iv_preview, EXTRA_IMAGE);
		// 尝试读取缩略图 当无法读取时自动生成
		File file = new File(item.path + "/.preview");
		if (file.exists()) {
			Picasso.with(getApplicationContext()).load(file).into(iv_preview);
		} else {
			new Thread() {

				@Override
				public void run() {
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
				}

			}.start();
		}

		// 重新生成大小
		new Thread() {
			@Override
			public void run() {
				try {
					String videoPath = FileManager.findFirstVideoFile(item.path);
					File vf = new File(videoPath);
					VideoItem vi = new VideoItem(new JSONObject(FileManager.readFile(item.path + "/data.json")));
					vi.srcs.get(vi.selectedSource).getVideoUrl(0).size =
							String.valueOf(DownloadManagerFragment.getSize(vf.length()));
					FileManager.saveFile(item.path + "/data.json", vi.toJSONObject().toString());
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

		FloatingActionButton fab = new FloatingActionButton.Builder(this)
				.withButtonColor(getResources().getColor(R.color.pink_500))
				.withDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_grey300_24dp))
				.withGravity(Gravity.BOTTOM|Gravity.RIGHT)
				.withMargins(0, 0,
						6,
						6
				).create();
		fab.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setDataAndType(Uri.parse(FileManager.findFirstVideoFile(item.path)), "video/*");
				startActivity(i);
			}

		});

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
		intent.putExtra("provider_type", "carryingcat");
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
