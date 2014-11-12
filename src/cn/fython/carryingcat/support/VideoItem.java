package cn.fython.carryingcat.support;

import java.util.ArrayList;

public class VideoItem extends VideoSource {

	public String path;
	public ArrayList<String> fileName;

	public VideoItem(String name) {
		super(name);
	}
	
	public VideoItem(String name, String path) {
		super(name);
		this.path = path;
		this.fileName = new ArrayList<String>();
	}
	
	public VideoItem(String name, String path, ArrayList<String> fileName) {
		super(name);
		this.path = path;
		this.fileName = fileName;
	}

}
