package cn.fython.carryingcat.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import cn.fython.carryingcat.ui.fragment.DownloadManagerFragment;
import cn.fython.carryingcat.ui.fragment.LocalVideoFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

	private String[] TITLES;
	private LocalVideoFragment fragment0;
	private DownloadManagerFragment fragment1;

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
				if (fragment0 == null) {
					fragment0 = LocalVideoFragment.newInstance();
				}
				return fragment0;
			case 1:
				if (fragment1 == null) {
					fragment1 = DownloadManagerFragment.newInstance();
				}
				return fragment1;
			default:
				return null;
		}
	}

}