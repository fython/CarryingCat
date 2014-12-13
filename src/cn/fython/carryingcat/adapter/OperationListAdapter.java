package cn.fython.carryingcat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.fython.carryingcat.R;

public class OperationListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<OperationItem> opeartions;
	private int color;

	private LayoutInflater inflater;

	public OperationListAdapter(Context context, ArrayList<OperationItem> operations,@ColorRes int color) {
		this.context = context;
		this.opeartions = operations;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.color = color;
	}

	@Override
	public int getCount() {
		return opeartions.size();
	}

	public void addItem(OperationItem item) {
		opeartions.add(item);
	}

	@Override
	public OperationItem getItem(int i) {
		return opeartions.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.operation_list_item, null);

			holder = new ViewHolder();
			holder.icon = (ImageView) view.findViewById(R.id.view_icon);
			holder.title = (TextView) view.findViewById(R.id.view_title);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.icon.setImageDrawable(getItem(i).icon);
		holder.icon.setColorFilter(color);
		holder.title.setText(getItem(i).title);
		holder.title.setTextColor(context.getResources().getColor(color));

		return view;
	}

	private class ViewHolder {

		public ImageView icon;
		public TextView title;

	}

	public static class OperationItem {

		public Drawable icon;
		public String title;
		public String key;

		// Normal Item with Blank Icon
		public OperationItem(String title, String key) {
			this.icon = new ColorDrawable(Color.TRANSPARENT);
			this.title = title;
			this.key = key;
		}

		// Normal Item
		public OperationItem(Drawable icon, String title, String key) {
			this.icon = icon;
			this.title = title;
			this.key = key;
		}

	}

}
