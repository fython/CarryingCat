package cn.fython.carryingcat.ui.fragment.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.ui.SettingsActivity;

public class MainFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

	private Preference pref_gui, pref_download;
	private Preference pref_application_info, pref_gplus, pref_weibo, pref_email, pref_blog;

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
		pref_gplus = findPreference("gplus");
		pref_weibo = findPreference("weibo");
		pref_email = findPreference("email");
		pref_blog = findPreference("blog");

		String version = "Unknown";
		try {
			version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
		} catch (Exception e) {
			// Keep the default value
		}
		pref_application_info.setSummary(version);

		pref_gui.setOnPreferenceClickListener(this);
		pref_download.setOnPreferenceClickListener(this);
		pref_gplus.setOnPreferenceClickListener(this);
		pref_weibo.setOnPreferenceClickListener(this);
		pref_email.setOnPreferenceClickListener(this);
		pref_blog.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference p) {
		// TODO Unfinished!!
		if (p == pref_gui) {
			Intent intent = new Intent(getActivity(), SettingsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
			intent.putExtra("flag", SettingsActivity.FLAG_GUI);
			startActivity(intent);
			return true;
		} else if (p == pref_download) {
			Intent intent = new Intent(getActivity(), SettingsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
			intent.putExtra("flag", SettingsActivity.FLAG_DOWNLOAD);
			startActivity(intent);
			return true;
		}
		return false;
	}
}
