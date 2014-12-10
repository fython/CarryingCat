package cn.fython.carryingcat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.adapter.HomePagerAdapter;
import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.Task;
import cn.fython.carryingcat.support.Utility;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.support.CrashHandler;
import cn.fython.carryingcat.ui.fragment.DownloadManagerFragment;
import cn.fython.carryingcat.ui.fragment.LocalVideoFragment;
import cn.fython.carryingcat.ui.task.AddActivity;
import cn.fython.carryingcat.view.FloatingActionButton;

public class MainActivity extends ActionBarActivity {

	public static final int REQUEST_ADD_TASK = 10001;
	public static final int HANDLER_REFRESH_MY_VIDEO = 0;

	private ActionBar mActionBar;

	private static PagerSlidingTabStrip mTabView;
	private static ViewPager mPager;
	private static FloatingActionButton mActionBtn;

	private static HomePagerAdapter mPagerAdapter;

	FileManager fm;

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		CrashHandler.init(getApplicationContext());
		CrashHandler.register();
		
		FileManager fm = new FileManager(getApplicationContext());
		fm.initCarryingCatDirectory();

		setUpActionBar();

		/** bind TabView & ViewPager **/
		mTabView = getPagerSlidingTabStrip();
		mPager = (ViewPager) findViewById(R.id.pager);

		mPagerAdapter = new HomePagerAdapter(
				getFragmentManager(),
				getResources().getStringArray(R.array.home_tabs)
		);

		mPager.setAdapter(mPagerAdapter);
		mTabView.setViewPager(mPager);

		/** bind ActionButton **/
		mActionBtn = new FloatingActionButton.Builder(this)
				.withButtonSize(getResources().getDimensionPixelSize(R.dimen.action_button_size))
				.withButtonColor(getResources().getColor(R.color.blue_500))
				.withDrawable(getResources().getDrawable(R.drawable.ic_add_white_36dp))
				.withGravity(Gravity.BOTTOM|Gravity.RIGHT)
				.withMargins(0, 0,
						getResources().getDimensionPixelSize(R.dimen.action_button_margin),
						getResources().getDimensionPixelSize(R.dimen.action_button_margin)
				).create();
		mActionBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AddActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				startActivityForResult(intent, REQUEST_ADD_TASK);
			}

		});

		/** Get newTask intent **/
		if (getIntent().hasExtra("url")) {
			String url = getIntent().getStringExtra("url");
			Intent intent = new Intent(MainActivity.this, AddActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
			intent.putExtra("url", url);
			startActivityForResult(intent, REQUEST_ADD_TASK);
		}
	}

	private void setUpActionBar() {
		Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(tb);
		
		mActionBar = getSupportActionBar();
		if (Utility.isLandscape(this)) {
			mActionBar.setCustomView(R.layout.actionbar_custom_land);
			mActionBar.setDisplayShowCustomEnabled(true);
		}
	}

	public PagerSlidingTabStrip getPagerSlidingTabStrip() {
		if (!Utility.isLandscape(this)) {
			return (PagerSlidingTabStrip) this.findViewById(R.id.pager_tab);
		} else {
			return (PagerSlidingTabStrip) mActionBar.getCustomView().findViewById(R.id.pager_tab);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
			case REQUEST_ADD_TASK:
				String jsonStr = data.getStringExtra("data");
				addTaskToManager(jsonStr);
				break;
		}
	}

	private void addTaskToManager(String jsonStr) {
		VideoItem vi;
		try {
			vi = new VideoItem(new JSONObject(jsonStr));
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		/** Build New Task **/
		Task newTask = new Task.Builder().setDataFromVideoItem(vi).build();
		Log.i(TAG, newTask.toJSONObject().toString());
		if (mPager.getCurrentItem() != 1) {
			mPager.setCurrentItem(1, true);
		}

		Log.i(TAG, newTask.toJSONObject().toString());

		if (fm == null) {
			fm = new FileManager(getApplicationContext());
		}
		try {
			fm.makeDir(Environment.getExternalStorageDirectory() + newTask.downloadPath);
			fm.saveFile(
					Environment.getExternalStorageDirectory() + newTask.downloadPath + "/task.json",
					newTask.toJSONObject().toString()
			);
			fm.saveFile(
					Environment.getExternalStorageDirectory() + newTask.downloadPath + "/data.json",
					vi.toJSONObject().toString()
			);
			getDownloadManagerFragment().receiveNewTask(getApplicationContext(), newTask);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static LocalVideoFragment getLocalVideoFragment() {
		return (LocalVideoFragment) mPagerAdapter.getItem(0);
	}

	public static DownloadManagerFragment getDownloadManagerFragment() {
		return (DownloadManagerFragment) mPagerAdapter.getItem(1);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.main, menu);

		MenuItem itemSearch = menu.findItem(R.id.action_search);
		MenuItemCompat.setShowAsAction(itemSearch, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("flag", SettingsActivity.FLAG_MAIN);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case HANDLER_REFRESH_MY_VIDEO:
					getLocalVideoFragment().refreshList();
					break;
				default:
					Log.e(TAG, "Received a unsupported message. Msg.what=" + msg.what);
			}
		}

	};
}
