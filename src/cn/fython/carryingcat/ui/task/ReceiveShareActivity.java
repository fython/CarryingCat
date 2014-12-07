package cn.fython.carryingcat.ui.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import cn.fython.carryingcat.ui.MainActivity;

public class ReceiveShareActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String action = intent.getAction();

		if (Intent.ACTION_SEND.equals(action)) {
			String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
			Intent startIntent = new Intent(this, MainActivity.class);
			startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startIntent.putExtra("url", sharedText);
			startActivity(startIntent);
		}
		finish();
	}

}
