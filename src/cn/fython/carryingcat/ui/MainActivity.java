package cn.fython.carryingcat.ui;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.adapter.HomePagerAdapter;
import cn.fython.carryingcat.support.Utility;
import cn.fython.carryingcat.view.FloatingActionButton;

public class MainActivity extends ActionBarActivity {

	private ActionBar mActionBar;

	private PagerSlidingTabStrip mTabView;
	private ViewPager mPager;
	private FloatingActionButton mActionBtn;

	private HomePagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
	}

	private void setUpActionBar() {
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		MenuItem itemSearch = menu.findItem(R.id.action_search);
		MenuItemCompat.setShowAsAction(itemSearch, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
