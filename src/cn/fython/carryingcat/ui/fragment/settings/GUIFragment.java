package cn.fython.carryingcat.ui.fragment.settings;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.Settings;

public class GUIFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

	public static GUIFragment newInstance() {
		return new GUIFragment();
	}

	public GUIFragment() {

	}

	private Settings mSets;

	SwitchPreference pref_tint;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		addPreferencesFromResource(R.xml.pref_ui);

		mSets = Settings.getInstance(getActivity().getApplicationContext());

		pref_tint = (SwitchPreference) findPreference("kitkat_tint");

		int sdkCode = Build.VERSION.SDK_INT;
		if (sdkCode < 19) {
			pref_tint.setSummary(R.string.title_force_tint_desc);
			pref_tint.setEnabled(false);
		}
		if (sdkCode > 19) {
			pref_tint.setSummary(R.string.title_force_tint_lollipop);
			pref_tint.setEnabled(false);
		}
		if (sdkCode == 19) {
			boolean b = mSets.getBoolean(Settings.Field.KITKAT_TINT, false);
			pref_tint.setChecked(b);
		}
		pref_tint.setOnPreferenceChangeListener(this);

		getActivity().setTitle(R.string.settings_gui);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		boolean b = (Boolean) newValue;
		if (preference == pref_tint) {
			pref_tint.setChecked(b);
			mSets.putBoolean(Settings.Field.KITKAT_TINT, b);
			return true;
		}
		return false;
	}
}
