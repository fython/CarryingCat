package cn.fython.carryingcat.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.fython.carryingcat.R;

public class StepTwoFragment extends Fragment {

	public StepTwoFragment() {}

	public static StepTwoFragment newInstance() {
		StepTwoFragment fragment = new StepTwoFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_add_step_1, null);
		return rootView;
	}

}
