package cn.fython.carryingcat.ui.video;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.provider.CCProvider;
import cn.fython.carryingcat.provider.VideoItemProvider;
import cn.fython.carryingcat.support.FileManager;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.view.FloatingActionButton;

public class DetailsActivity extends ActionBarActivity {

	private ActionBar mActionBar;
	private ImageView iv_preview;

	private VideoItemProvider provider;
	private VideoItem item;

	private static final String TAG = "DetailsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		setUpActionBar();

		/** get Intent data **/
		Intent intent = getIntent();
		String providerType = intent.getStringExtra("provider_type");
		if (providerType.equals("carryingcat")) {
			provider = new CCProvider(getApplicationContext());
		}
		int id = intent.getIntExtra("id", 0);
		item = provider.getVideoList().get(id);

		Log.i(TAG, item.toJSONObject().toString());

		iv_preview = (ImageView) findViewById(R.id.iv_preview);

		FloatingActionButton fab = new FloatingActionButton.Builder(this)
				.withButtonSize(getResources().getDimensionPixelSize(R.dimen.action_button_size))
				.withButtonColor(getResources().getColor(R.color.pink_500))
				.withDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_grey300_24dp))
				.withGravity(Gravity.BOTTOM|Gravity.RIGHT)
				.withMargins(0, 0,
						getResources().getDimensionPixelSize(R.dimen.action_button_margin),
						getResources().getDimensionPixelSize(R.dimen.action_button_margin)
				).create();
		fab.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setDataAndType(Uri.parse(FileManager.findFirstVideoFile(item.path)), "video/*");
				startActivity(i);
			}

		});

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(item.srcs.get(0).title);
	}

	private void setUpActionBar() {
		Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(tb);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
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



}
