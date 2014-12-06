package cn.fython.carryingcat.ui.task;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.adapter.AddStepPagerAdapter;
import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.ui.MainActivity;
import cn.fython.carryingcat.ui.fragment.StepOneFragment;
import cn.fython.carryingcat.ui.fragment.StepTwoFragment;

public class AddActivity extends ActionBarActivity {

	private ActionBar mActionBar;

	private ViewPager mPager;

	private static StepOneFragment fragment0;
	private static StepTwoFragment fragment1;
	private AddStepPagerAdapter mAdapter;

	private static VideoItem data;

	public static int quality = 0;
	public static boolean fromShareIntent = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		mPager = (ViewPager) findViewById(R.id.pager);

		/** Get shareIntent **/
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		String sharedText = null;
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				fromShareIntent = true;
				sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
			}
		}

		/** bind fragments and adapter **/
		fragment0 = StepOneFragment.newInstance(sharedText);
		fragment1 = StepTwoFragment.newInstance();

		mAdapter = new AddStepPagerAdapter(getFragmentManager(), new Fragment[] {fragment0, fragment1});
		mPager.setAdapter(mAdapter);

		setUpActionBar();
	}

	private void setUpActionBar() {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
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

	public void nextStep() {
		mPager.setCurrentItem(1, true);
	}

	public VideoItem getVideoItem() {
		return data;
	}

	public void setVideoItem(VideoItem data) {
		this.data = data;
	}

	public void finishAdding() {
		data.path = FileManager.getMyVideoDirPath() + "/" + data.srcs.get(0).title;
		if (fromShareIntent) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("task", data.toJSONObject().toString());
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			return;
		} else {
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("data", data.toJSONObject().toString());
			setResult(MainActivity.RESULT_OK, intent);
			finish();
		}
	}

}
