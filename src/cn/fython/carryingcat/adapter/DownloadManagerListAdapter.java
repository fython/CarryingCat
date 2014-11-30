package cn.fython.carryingcat.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.Task;
import cn.fython.carryingcat.ui.fragment.DownloadManagerFragment;

public class DownloadManagerListAdapter extends BaseAdapter {

	private Context mContext;

	private ArrayList<Task> tasks;

	public DownloadManagerListAdapter(Context context, ArrayList<Task> tasks) {
		mContext = context;
		this.tasks = tasks;
	}

	@Override
	public int getCount() {
		return tasks.size();
	}

	@Override
	public Task getItem(int position) {
		return tasks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addItem(Task task) {
		tasks.add(task);
	}

	public void setItem(int index, Task task) {
		tasks.set(index, task);
	}

	public void removeItem(int index) {
		tasks.remove(index);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		int index = (int) getItemId(position);
		Task targetTask = tasks.get(index);

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.download_list_item, null);

			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.size = (TextView) convertView.findViewById(R.id.tv_size);
			holder.mode = (TextView) convertView.findViewById(R.id.tv_mode);
			holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.title.setText(targetTask.title);
		holder.size.setText(DownloadManagerFragment.getSize(targetTask.bytes[0]) + "/" + DownloadManagerFragment.getSize(targetTask.bytes[1]));
		try {
			holder.pb.setIndeterminate(false);
			holder.pb.setProgress(targetTask.progress.get(0));
		} catch (Exception e) {

		}
		if (targetTask.mode == DownloadManager.STATUS_PENDING) {
			holder.pb.setIndeterminate(true);
			holder.mode.setText(R.string.status_pending);
		} else if (targetTask.mode == DownloadManager.STATUS_RUNNING) {
			holder.mode.setText(R.string.status_downloading);
		} else if (targetTask.mode == DownloadManager.STATUS_FAILED) {
			holder.mode.setText(R.string.status_failed);
		} else if (targetTask.mode == DownloadManager.STATUS_SUCCESSFUL) {
			holder.mode.setText(R.string.status_succeed);
		}

		return convertView;
	}

	private class ViewHolder {

		public TextView title, size, mode;
		public ProgressBar pb;

	}

}
