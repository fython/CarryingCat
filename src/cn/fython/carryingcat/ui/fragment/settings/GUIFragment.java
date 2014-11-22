package cn.fython.carryingcat.ui.fragment.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import cn.fython.carryingcat.R;

public class GUIFragment extends PreferenceFragment {

	public static GUIFragment newInstance() {
		return new GUIFragment();
	}

	public GUIFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		addPreferencesFromResource(R.xml.pref_main);

		getActivity().setTitle(R.string.settings_gui);
	}

}
