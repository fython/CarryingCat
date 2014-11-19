package cn.fython.carryingcat.ui.fragment.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import cn.fython.carryingcat.R;

public class MainFragment extends PreferenceFragment {

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
	}

}
