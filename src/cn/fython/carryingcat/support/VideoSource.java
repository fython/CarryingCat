package cn.fython.carryingcat.support;

import java.util.ArrayList;

import cn.fython.carryingcat.support.VideoUrl;

public class VideoSource {

	public String title, playurl, quality, site, imgurl;
	private ArrayList<VideoUrl> urls;

	public VideoSource(String title) {
		this.title = title;
		this.urls = new ArrayList<VideoUrl>();
	}
	
	public VideoSource(String title, ArrayList<VideoUrl> urls) {
		this.title = title;
		this.urls = urls;
	}

	public void addVideoUrl(VideoUrl newUrl) {
		urls.add(newUrl);
	}

	public VideoUrl getVideoUrl(int position) {
		return urls.get(position);
	}

}
