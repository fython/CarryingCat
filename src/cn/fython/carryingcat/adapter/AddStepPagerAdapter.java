package cn.fython.carryingcat.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class AddStepPagerAdapter extends FragmentPagerAdapter {

	private Fragment[] fragments;

	public AddStepPagerAdapter(FragmentManager fm, Fragment[] fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return String.valueOf(position);
	}

	@Override
	public int getCount() {
		return fragments.length;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments[position];
	}

}
