package cn.fython.carryingcat.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class LocalVideoFragment extends Fragment {

	private ListView mListView;

	public LocalVideoFragment() {}

	public static LocalVideoFragment newInstance() {
		LocalVideoFragment fragment = new LocalVideoFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = new ListView(inflater.getContext());
		return rootView;
	}

}
