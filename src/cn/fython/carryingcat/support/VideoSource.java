package cn.fython.carryingcat.support;

import java.util.ArrayList;

import cn.fython.carryingcat.support.VideoUrl;

public class VideoSource {

	public String name, websrc;
	public ArrayList<VideoUrl> urls;

	public VideoSource(String name) {
		this.name = name;
		this.urls = new ArrayList<VideoUrl>();
	}
	
	public VideoSource(String name, ArrayList<VideoUrl> urls) {
		this.name = name;
		this.urls = urls;
	}

}
