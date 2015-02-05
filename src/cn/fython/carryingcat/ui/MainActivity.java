package cn.fython.carryingcat.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.adapter.HomePagerAdapter;
import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.Settings;
import cn.fython.carryingcat.support.Task;
import cn.fython.carryingcat.support.Utility;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.support.CrashHandler;
import cn.fython.carryingcat.ui.fragment.DownloadManagerFragment;
import cn.fython.carryingcat.ui.fragment.LocalVideoFragment;
import cn.fython.carryingcat.ui.task.AddActivity;

public class MainActivity extends ActionBarActivity {

	public static final int REQUEST_ADD_TASK = 10001;
	public static final int HANDLER_REFRESH_MY_VIDEO = 0, HANDLER_DELETE_SUCCESSFUL = 1;

	private ActionBar mActionBar;

	private static PagerSlidingTabStrip mTabView;
	private static ViewPager mPager;
	private static FloatingActionButton mActionBtn;

	private static HomePagerAdapter mPagerAdapter;

	private FileManager fm;
	private Settings mSets;

	private static Context mContext;

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = getApplicationContext();
		mSets = Settings.getInstance(mContext);

		if (Build.VERSION.SDK_INT == 19) {
			if (mSets.isTintEnabled()) {
				Utility.enableTint(this, new ColorDrawable(getResources().getColor(R.color.deep_purple_500)));
			}
		}

		if (Build.VERSION.SDK_INT >= 21) {
			getWindow().setNavigationBarColor(getResources().getColor(R.color.deep_purple_700));
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		CrashHandler.init(mContext);
		CrashHandler.register();

		FileManager fm = new FileManager(mContext);
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
		mActionBtn = (FloatingActionButton) findViewById(R.id.fab);
		getFloatingActionButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AddActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, REQUEST_ADD_TASK);
			}

		});

		/** Get newTask intent **/
		if (getIntent().hasExtra("url")) {
			String url = getIntent().getStringExtra("url");
			Intent intent = new Intent(MainActivity.this, AddActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
			mActionBar.setDisplayShowTitleEnabled(false);
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

	public FloatingActionButton getFloatingActionButton() {
		return mActionBtn;
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
			SettingsActivity.launch(this, SettingsActivity.FLAG_MAIN);
			return true;
		} else if (id == R.id.action_donate) {
			View v = View.inflate(
					new ContextThemeWrapper(
							getApplicationContext(),
							R.style.Theme_AppCompat_Light_Dialog
					),
					R.layout.dialog_donate,
					null
			);
			new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog)
					.setTitle(R.string.action_donate)
					.setView(v)
					.setNegativeButton(android.R.string.ok, null)
					.show();
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
				case HANDLER_DELETE_SUCCESSFUL:
					Toast.makeText(mContext, R.string.operation_delete_successful, Toast.LENGTH_SHORT)
							.show();
					break;
				default:
					Log.e(TAG, "Received a unsupported message. Msg.what=" + msg.what);
			}
		}

	};
}
