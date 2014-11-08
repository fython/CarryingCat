package cn.fython.carryingcat.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import cn.fython.carryingcat.ui.fragment.LocalVideoFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

	private String[] TITLES;

	public HomePagerAdapter(FragmentManager fm, String[] titles) {
		super(fm);
		this.TITLES = titles;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position].toUpperCase();
	}

	@Override
	public int getCount() {
		return TITLES.length;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case 0:
				return LocalVideoFragment.newInstance();
			case 1:
				return LocalVideoFragment.newInstance();
			default:
				return null;
		}
	}

}