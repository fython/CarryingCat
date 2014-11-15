package cn.fython.carryingcat.ui.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.support.api.FlvxzTools;
import cn.fython.carryingcat.ui.task.AddActivity;
import me.drakeet.materialdialog.MaterialDialog;
import oppa.paperstyle.PaperButton;

public class StepOneFragment extends Fragment {

	private AddActivity mActivity;

	private PaperButton btn_quality;
	private EditText et_url;
	private TextView tv_name, tv_size;

	private VideoItem data;
	private String[] qualityName;

	/** Dialog values */
	private MaterialDialog dialogQuality, dialogError;
	private ListView lv_quality;
	private ArrayAdapter mAdapter;

	public StepOneFragment() {}

	public static StepOneFragment newInstance() {
		StepOneFragment fragment = new StepOneFragment();
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

		return rootView;
	}

	public VideoItem getVideoItem() {
		return data;
	}

	public void showQualityChooseDialog(String[] quality) {
		if (dialogQuality == null) {
			dialogQuality = new MaterialDialog(mActivity);
			View v = View.inflate(
					new ContextThemeWrapper(
							mActivity.getApplicationContext(),
							R.style.Theme_AppCompat_Light_Dialog),
					R.layout.dialog_quality_choose,
					null
			);

			lv_quality = (ListView) v.findViewById(R.id.listView);
			lv_quality.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					mActivity.quality = position;
					btn_quality.setText(data.srcs.get(position).quality);
					setTextViewVideoSize();
					dialogQuality.dismiss();
				}

			});

			dialogQuality.setTitle(getString(R.string.quality_choose))
					.setNegativeButton(android.R.string.cancel, new View.OnClickListener(){
						@Override
						public void onClick(View v) {
							dialogQuality.dismiss();
						}
					})
					.setCanceledOnTouchOutside(true)
					.setContentView(v);
		}
		if (quality == null) {
			showErrorDialog(getString(R.string.result_hint_wrong_url));
		}

		mAdapter = new ArrayAdapter<String>(
				getActivity().getApplicationContext(),
				R.layout.dialog_quality_list_item,
				quality
		);
		lv_quality.setAdapter(mAdapter);

		dialogQuality.show();
	}

	public void showErrorDialog(String message) {
		dialogError = new MaterialDialog(mActivity)
				.setTitle(R.string.result_error)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogError.dismiss();
					}
				});
		dialogError.show();
	}

	public void setTextViewVideoSize() {
		tv_size.setText(
				String.format(
						getString(R.string.content_size),
						data.srcs.get(mActivity.quality).getVideoUrl(0).size,
						data.srcs.get(mActivity.quality).getVideoUrl(0).time
				)
		);
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
				btn_quality.setText(data.srcs.get(0).quality);

				ArrayList<String> arrayList = new ArrayList<String>();
				for (int i = 0; i < data.srcs.size(); i++) {
					arrayList.add(data.srcs.get(i).quality);
				}
				qualityName = arrayList.toArray(new String[arrayList.size()]);
				mActivity.quality = 0;
				setTextViewVideoSize();

				// 通知AddActivity拉取VideoItem数据
				mActivity.mHandler.sendEmptyMessage(0);
			} else {
				tv_name.setText(getString(R.string.result_error));
			}
		}

	}

}
