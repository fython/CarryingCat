package cn.fython.carryingcat.support;

import java.util.ArrayList;

public class VideoSource {

	public String name, websrc;
	public ArrayList<String> sourceUrl, sourceType;

	public VideoSource(String name, ArrayList<String> sourceUrl, ArrayList<String> sourceType) {
		this.name = name;
		this.sourceUrl = sourceUrl;
		this.sourceType = sourceType;
	}

}
