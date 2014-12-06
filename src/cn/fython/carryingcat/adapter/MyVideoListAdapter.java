package cn.fython.carryingcat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.VideoItem;

public class MyVideoListAdapter extends BaseAdapter {

	private ArrayList<VideoItem> items;
	private Context context;

	private Picasso p;

	public MyVideoListAdapter(Context context, ArrayList<VideoItem> items) {
		this.context = context;
		this.items = items;
		this.p = Picasso.with(context);
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
			holder.tv_other = (TextView) convertView.findViewById(R.id.tv_other);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

			holder.iv_preview = (ImageView) convertView.findViewById(R.id.iv_preview);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_title.setText(getItem(position).srcs.get(0).title);

		// TODO 用本地视频的实际数据替换API获取的数据
		holder.tv_time.setText(getItem(position).srcs.get(getItem(position).selectedSource).getVideoUrl(0).time);
		holder.tv_other.setText(getItem(position).srcs.get(getItem(position).selectedSource).getVideoUrl(0).size);

		File file = new File(items.get(position).path + "/.preview");
		if (file.exists()) {
			p.load(file).into(holder.iv_preview);
		}

		return convertView;
	}

	private class ViewHolder {

		public TextView tv_title, tv_other, tv_time;
		public ImageView iv_preview;

	}

}
