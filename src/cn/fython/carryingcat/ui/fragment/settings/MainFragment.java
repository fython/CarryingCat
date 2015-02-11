package cn.fython.carryingcat.ui.fragment.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.Settings;
import cn.fython.carryingcat.support.Utility;
import cn.fython.carryingcat.ui.SettingsActivity;

public class MainFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

	private Preference pref_gui, pref_download;
	private Preference pref_application_info, pref_weibo, pref_github, pref_blog;

	public static MainFragment newInstance() {
		return new MainFragment();
	}

	public MainFragment() {

	}

	private Settings mSets;

	private MutilClickThread mThread;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		addPreferencesFromResource(R.xml.pref_main);

		mThread = new MutilClickThread();
		mSets = Settings.getInstance(getActivity().getApplicationContext());

		getActivity().setTitle(R.string.settings);

		pref_gui = findPreference("gui");
		pref_download = findPreference("download");
		pref_application_info = findPreference("application_info");
		pref_weibo = findPreference("weibo");
		pref_blog = findPreference("blog");
		pref_github = findPreference("github");

		String version = "Unknown";
		try {
			version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
			version += " (" + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode + ")";
		} catch (Exception e) {
			// Keep the default value
		}
		pref_application_info.setSummary(version);

		pref_application_info.setOnPreferenceClickListener(this);
		pref_gui.setOnPreferenceClickListener(this);
		pref_download.setOnPreferenceClickListener(this);
		pref_weibo.setOnPreferenceClickListener(this);
		pref_blog.setOnPreferenceClickListener(this);
		pref_github.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference p) {
		if (p == pref_application_info) {
			if (!mThread.isRunning) {
				mThread = new MutilClickThread();
				mThread.start();
				mThread.isRunning = true;
			}
			mThread.clicktimes++;
			return true;
		} else if (p == pref_gui) {
			SettingsActivity.launch(getActivity(), SettingsActivity.FLAG_GUI);
			return true;
		} else if (p == pref_download) {
			SettingsActivity.launch(getActivity(), SettingsActivity.FLAG_DOWNLOAD);
			return true;
		} else if (p == pref_weibo) {
			openWebsite(getString(R.string.about_weibo_address));
			return true;
		} else if (p == pref_blog) {
			openWebsite(getString(R.string.about_blog_address));
			return true;
		} else if (p == pref_github) {
			openWebsite(getString(R.string.about_github_address));
			return true;
		}
		return false;
	}

	private void openWebsite(String url) {
		Uri uri = Uri.parse(url);
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}

	private class MutilClickThread extends Thread {

		public int clicktimes = 0;
		public boolean isRunning = false;

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (clicktimes > 5) {
				final int now = mSets.getInt(Settings.Field.ICON_INT, Settings.Field.ICON_NORMAL);
				try {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(
									getActivity().getApplicationContext(),
									now == 0 | now == 1 ? "8-bit icon mode enabled!!" : "8-bit icon mode disabled!!",
									Toast.LENGTH_LONG
							).show();
						}
					});
				} catch (Exception e) {
					// Ignore exceptions.
				}
				int then = now == 2 ? 0 : now + 1;
				mSets.putInt(Settings.Field.ICON_INT, then);
				Utility.setIcon(getActivity(), then);
			}
			isRunning = false;
			clicktimes = 0;
		}

	}

}
