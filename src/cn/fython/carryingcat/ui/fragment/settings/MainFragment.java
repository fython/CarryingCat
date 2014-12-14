package cn.fython.carryingcat.ui.fragment.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.ui.SettingsActivity;

public class MainFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

	private Preference pref_gui, pref_download;
	private Preference pref_application_info, pref_weibo, pref_github, pref_blog;

	public static MainFragment newInstance() {
		return new MainFragment();
	}

	public MainFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		addPreferencesFromResource(R.xml.pref_main);

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

		pref_gui.setOnPreferenceClickListener(this);
		pref_download.setOnPreferenceClickListener(this);
		pref_weibo.setOnPreferenceClickListener(this);
		pref_blog.setOnPreferenceClickListener(this);
		pref_github.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference p) {
		if (p == pref_gui) {
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

}
