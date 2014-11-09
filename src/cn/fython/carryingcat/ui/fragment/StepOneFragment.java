package cn.fython.carryingcat.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.ui.task.AddActivity;

public class StepOneFragment extends Fragment {

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
		return rootView;
	}

}
