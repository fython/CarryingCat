package cn.fython.carryingcat.ui.fragment.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.text.TextUtils;

import java.io.File;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.Settings;

public class DownloadSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

	public static DownloadSettingsFragment newInstance() {
		return new DownloadSettingsFragment();
	}

	public DownloadSettingsFragment() {

	}

	private Settings mSets;

	SwitchPreference pref_bilibili;
	EditTextPreference pref_bilibili_path;

	private AlertDialog dialogError;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		addPreferencesFromResource(R.xml.pref_download);

		mSets = Settings.getInstance(getActivity().getApplicationContext());

		pref_bilibili = (SwitchPreference) findPreference("bilibili_enabled");
		pref_bilibili_path = (EditTextPreference) findPreference("bilibili_path");

		pref_bilibili.setChecked(mSets.isBilibiliEnabled());
		pref_bilibili_path.setText(mSets.getBilibiliPath());

		pref_bilibili.setOnPreferenceChangeListener(this);
		pref_bilibili_path.setOnPreferenceChangeListener(this);

		pref_bilibili_path.getEditText().setHint(mSets.getBilibiliDefaultPath());
		pref_bilibili_path.setSummary(mSets.getBilibiliPath());

		getActivity().setTitle(R.string.settings_download);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == pref_bilibili) {
			boolean b = (Boolean) newValue;
			pref_bilibili.setChecked(b);
			mSets.setBilibiliEnabled(b);
			return true;
		}
		if (preference == pref_bilibili_path) {
			String s = (String) newValue;
			if (TextUtils.isEmpty(s)) {
				pref_bilibili_path.setSummary(mSets.getBilibiliDefaultPath());
				mSets.setBilibiliPath(mSets.getBilibiliDefaultPath());
				return true;
			}
			try {
				File file = new File(s);
				if (!file.exists() || !file.isDirectory() || !file.isAbsolute()) {
					throw new Exception("Wrong folder path!! (" + s +")");
				}
				pref_bilibili_path.setSummary(s);
				mSets.setBilibiliPath(s);
			} catch (Exception e) {
				dialogError = new AlertDialog.Builder(getActivity())
						.setTitle(R.string.result_error)
						.setMessage(R.string.title_bilibili_path_unavailable)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialogError.dismiss();
							}
						})
						.create();
				dialogError.show();
			} finally {
				return true;
			}
		}
		return false;
	}
}
