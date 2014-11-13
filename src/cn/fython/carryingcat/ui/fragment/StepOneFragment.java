package cn.fython.carryingcat.ui.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Locale;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.support.VideoSource;
import cn.fython.carryingcat.support.api.FlvxzTools;
import cn.fython.carryingcat.ui.task.AddActivity;

public class StepOneFragment extends Fragment {

	private EditText et_url;
	private TextView tv_name;

	private VideoItem data;

	public StepOneFragment() {}

	public static StepOneFragment newInstance() {
		StepOneFragment fragment = new StepOneFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_add_step_0, null);

		Button btn_next = (Button) rootView.findViewById(R.id.button);
		btn_next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((AddActivity) getActivity()).nextStep();
			}
		});

		et_url = (EditText) rootView.findViewById(R.id.et_url);
		et_url.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					new GetResultTask().execute();
				}
				return false;
			}

		});

		tv_name = (TextView) rootView.findViewById(R.id.tv_title);

		boolean useChinese = getResources().getConfiguration().locale.getCountry() == Locale.CHINA.getCountry();
		if (useChinese) {
			((TextView) rootView.findViewById(R.id.title_address)).getPaint().setFakeBoldText(true);
			((TextView) rootView.findViewById(R.id.title_url_title)).getPaint().setFakeBoldText(true);
		}

		return rootView;
	}

	private class GetResultTask extends AsyncTask<Void, Void, VideoItem> {

		@Override
		protected VideoItem doInBackground(Void... params) {
			try {
				return FlvxzTools.getVideoItem(et_url.getText().toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(VideoItem result) {
			data = result;
			if (data != null) {
				tv_name.setText(data.name);
			} else {
				tv_name.setText(getString(R.string.result_error));
			}
		}

	}

}
