package cn.fython.carryingcat.ui.task;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import cn.fython.carryingcat.support.Settings;
import cn.fython.carryingcat.support.Utility;
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

	private Settings mSets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mSets = Settings.getInstance(getApplicationContext());

		if (Build.VERSION.SDK_INT == 19) {
			if (mSets.isTintEnabled()) {
				Utility.enableTint(this, new ColorDrawable(getResources().getColor(R.color.deep_purple_500)));
			}
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		mPager = (ViewPager) findViewById(R.id.pager);

		/** Get shareIntent **/
		Intent intent = getIntent();
		String sharedText = null;
		if (intent.hasExtra("url")) {
			sharedText = intent.getStringExtra("url");
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
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("data", data.toJSONObject().toString());
		setResult(MainActivity.RESULT_OK, intent);
		finish();
	}

}
