package cn.fython.carryingcat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.VideoItemTask;

public class DownloadManagerListAdapter extends BaseAdapter {

	private Context mContext;

	private ArrayList<VideoItemTask> tasks;

	public DownloadManagerListAdapter(Context context, ArrayList<VideoItemTask> tasks) {
		mContext = context;
		this.tasks = tasks;
	}

	@Override
	public int getCount() {
		return tasks.size();
	}

	@Override
	public VideoItemTask getItem(int position) {
		return tasks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addItem(VideoItemTask task) {
		tasks.add(task);
	}

	public void removeItem(int index) {
		tasks.remove(index);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.download_list_item, null);

			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.title.setText(tasks.get((int) getItemId(position)).name);
		holder.pb.setProgress(tasks.get((int) getItemId(position)).progress.get(0));

		return convertView;
	}

	private class ViewHolder {

		public TextView title;
		public ProgressBar pb;

	}

}
