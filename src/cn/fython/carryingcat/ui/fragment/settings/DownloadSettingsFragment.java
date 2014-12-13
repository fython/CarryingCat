package cn.fython.carryingcat.ui.fragment.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import cn.fython.carryingcat.R;

public class DownloadSettingsFragment extends PreferenceFragment {

	public static DownloadSettingsFragment newInstance() {
		return new DownloadSettingsFragment();
	}

	public DownloadSettingsFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		addPreferencesFromResource(R.xml.pref_download);

		getActivity().setTitle(R.string.settings_download);
	}

}
