package cn.fython.carryingcat.provider;

import android.content.Context;

import java.util.ArrayList;

import cn.fython.carryingcat.support.VideoItem;

public abstract class VideoItemProvider {

	private Context mContext;

	public VideoItemProvider(Context context) {
		this.mContext = context;
	}

	public abstract ArrayList<VideoItem> getVideoList();

	public abstract VideoItem getVideoItem(int id);

	public abstract String getProviderName();
}
