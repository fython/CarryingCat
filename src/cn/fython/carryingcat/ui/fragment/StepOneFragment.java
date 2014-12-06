package cn.fython.carryingcat.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.support.api.FlvxzTools;
import cn.fython.carryingcat.ui.task.AddActivity;
import oppa.paperstyle.PaperButton;

public class StepOneFragment extends Fragment {

	private AddActivity mActivity;

	private PaperButton btn_quality;
	private EditText et_url;
	private TextView tv_name, tv_size;

	private VideoItem data;
	private String[] qualityName;

	/** Dialog values */
	private AlertDialog dialogQuality, dialogError;

	public StepOneFragment() {}

	public static StepOneFragment newInstance(String url) {
		StepOneFragment fragment = new StepOneFragment();
		Bundle data = new Bundle();
		data.putString("url", url);
		fragment.setArguments(data);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_add_step_0, null);

		mActivity = (AddActivity) getActivity();

		rootView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((AddActivity) getActivity()).nextStep();
			}
		});

		btn_quality = (PaperButton) rootView.findViewById(R.id.btn_quality);
		btn_quality.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (data != null) {
					showQualityChooseDialog(qualityName);
				} else {
					showErrorDialog(getString(R.string.result_hint_wrong_url));
				}
			}
		});

		et_url = (EditText) rootView.findViewById(R.id.et_url);
		et_url.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					return check();
				}
				if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
					return !check();
				}
				return false;
			}

		});

		tv_name = (TextView) rootView.findViewById(R.id.tv_title);
		tv_size = (TextView) rootView.findViewById(R.id.tv_size);

		boolean useChinese = getResources().getConfiguration().locale.getCountry() == Locale.CHINA.getCountry();
		if (useChinese) {
			((TextView) rootView.findViewById(R.id.title_address)).getPaint().setFakeBoldText(true);
			((TextView) rootView.findViewById(R.id.title_url_title)).getPaint().setFakeBoldText(true);
			((TextView) rootView.findViewById(R.id.title_quality)).getPaint().setFakeBoldText(true);
			((TextView) rootView.findViewById(R.id.title_size)).getPaint().setFakeBoldText(true);
		}

		Bundle data = getArguments();
		String url = data.getString("url");
		if (url != null) {
			et_url.setText(url);
		}

		return rootView;
	}

	private boolean check() {
		if (et_url.getText().toString().trim().length() < 1) {
			return true;
		}
		if (et_url.getText().toString().indexOf("http://") == -1) {
			Toast.makeText(
					mActivity.getApplicationContext(),
					getString(R.string.hint_enter_url_error),
					Toast.LENGTH_SHORT
			).show();
			return true;
		}
		et_url.clearFocus();
		new GetResultTask().execute();
		return false;
	}

	public VideoItem getVideoItem() {
		return data;
	}

	public void showQualityChooseDialog(String[] quality) {
		dialogQuality = new AlertDialog.Builder(mActivity)
				.setTitle(getString(R.string.quality_choose))
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialogQuality.dismiss();
					}
				})
				.setSingleChoiceItems(qualityName, data.selectedSource, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						data.selectedSource = which;
						setTextViewVideoSize();
					}
				})
				.create();

		if (quality == null) {
			showErrorDialog(getString(R.string.result_hint_wrong_url));
		}

		dialogQuality.show();
	}

	public void showErrorDialog(String message) {
		dialogError = new AlertDialog.Builder(mActivity)
				.setTitle(R.string.result_error)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialogError.dismiss();
					}
				})
				.create();
		dialogError.show();
	}

	public void setTextViewVideoSize() {
		tv_size.setText(
				String.format(
						getString(R.string.content_size),
						data.srcs.get(data.selectedSource).getVideoUrl(0).size,
						data.srcs.get(data.selectedSource).getVideoUrl(0).time
				)
		);
	}

	private class GetResultTask extends AsyncTask<Void, Void, VideoItem> {

		@Override
		protected VideoItem doInBackground(Void... params) {
			try {
				return FlvxzTools.getVideoItem(et_url.getText().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(VideoItem result) {
			data = result;
			if (data != null) {
				tv_name.setText(data.name);
				btn_quality.setText(data.srcs.get(0).quality);

				ArrayList<String> arrayList = new ArrayList<String>();
				for (int i = 0; i < data.srcs.size(); i++) {
					arrayList.add(data.srcs.get(i).quality);
				}
				qualityName = arrayList.toArray(new String[arrayList.size()]);
				mActivity.quality = 0;
				setTextViewVideoSize();

				// 通知AddActivity拉取VideoItem数据
				mActivity.setVideoItem(data);
			} else {
				tv_name.setText(getString(R.string.result_error));
				qualityName = new String[]{};
				btn_quality.setText(getString(R.string.result_unavailable));
				tv_size.setText(getString(R.string.result_unavailable));
			}
		}

	}

}
