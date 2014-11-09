package cn.fython.carryingcat.support;

import java.util.ArrayList;

public class VideoItem {

	public String name, path, websrc;
	public ArrayList<String> sourceUrl, fileName;

	public VideoItem(String name, String path, ArrayList<String> sourceUrl, ArrayList<String> fileName) {
		this.name = name;
		this.path = path;
		this.sourceUrl = sourceUrl;
		this.fileName = fileName;
	}

}
