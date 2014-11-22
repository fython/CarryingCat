package cn.fython.carryingcat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.VideoItem;

public class MyVideoListAdapter extends BaseAdapter {

	private ArrayList<VideoItem> items;
	private Context context;

	public MyVideoListAdapter(Context context, ArrayList<VideoItem> items) {
		this.context = context;
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public VideoItem getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.video_list_item, null);

			holder = new ViewHolder();
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_title.setText(getItem(position).srcs.get(0).title);

		return convertView;
	}

	private class ViewHolder {

		public TextView tv_title;

	}

}
