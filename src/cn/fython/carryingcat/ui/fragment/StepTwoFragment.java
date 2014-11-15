package cn.fython.carryingcat.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.ui.task.AddActivity;

public class StepTwoFragment extends Fragment {

	private AddActivity mActivity;

	public StepTwoFragment() {}

	public static StepTwoFragment newInstance() {
		StepTwoFragment fragment = new StepTwoFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_add_step_1, null);

		mActivity = (AddActivity) getActivity();

		rootView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mActivity.getVideoItem() == null) {
					Toast.makeText(
							mActivity.getApplicationContext(),
							getString(R.string.hint_enter_url_error),
							Toast.LENGTH_SHORT
					).show();
					return;
				}
				mActivity.mHandler.sendEmptyMessage(1);
			}
		});

		return rootView;
	}

}
