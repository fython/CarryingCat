package cn.fython.carryingcat.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.Settings;
import cn.fython.carryingcat.support.Utility;
import cn.fython.carryingcat.ui.fragment.settings.DownloadSettingsFragment;
import cn.fython.carryingcat.ui.fragment.settings.GUIFragment;
import cn.fython.carryingcat.ui.fragment.settings.MainFragment;

public class SettingsActivity extends ActionBarActivity {

	public final static int FLAG_MAIN = 0, FLAG_GUI = 1, FLAG_DOWNLOAD = 2;

	private Settings mSets;
	private Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mSets = Settings.getInstance(getApplicationContext());

		if (Build.VERSION.SDK_INT == 19) {
			if (mSets.getBoolean(Settings.Field.KITKAT_TINT, false)) {
				Utility.enableTint(this, new ColorDrawable(getResources().getColor(R.color.deep_purple_500)));
			}
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_root);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		int flag = intent.getIntExtra("flag", FLAG_MAIN);
		switch (flag) {
			case FLAG_MAIN:
				fragment = MainFragment.newInstance();
				break;
			case FLAG_GUI:
				fragment = GUIFragment.newInstance();
				break;
			case FLAG_DOWNLOAD:
				fragment = DownloadSettingsFragment.newInstance();
				break;
			default:
				throw new NullPointerException();
		}

		getFragmentManager().beginTransaction()
				.replace(R.id.frameLayout, fragment)
				.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public static void launch(Activity activity, int settingsFlag) {
		Intent intent = new Intent(activity, SettingsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		intent.putExtra("flag", settingsFlag);
		activity.startActivity(intent);
	}

}
