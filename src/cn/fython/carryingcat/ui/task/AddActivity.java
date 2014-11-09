package cn.fython.carryingcat.ui.task;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.adapter.AddStepPagerAdapter;
import cn.fython.carryingcat.ui.fragment.StepOneFragment;
import cn.fython.carryingcat.ui.fragment.StepTwoFragment;

public class AddActivity extends ActionBarActivity {

	private ActionBar mActionBar;

	private ViewPager mPager;

	private Fragment fragment0, fragment1;
	private AddStepPagerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		mPager = (ViewPager) findViewById(R.id.pager);

		/** bind fragments and adapter **/
		fragment0 = StepOneFragment.newInstance();
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

}
