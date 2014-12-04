package cn.fython.carryingcat.support;

import org.json.JSONException;
import org.json.JSONObject;

public class VideoUrl {

	/**
	 *  Sample:
	 *
	 *  {
	 *  "furl":"http:\/\/k.youku.com\/player\/getFlvPath\/sid\/241577094975912add333_01\/st\/mp4\/fileid\/03002001005187BA198F6F0230E416AB237B8B-4D0C-7D2A-0E75-2CF3633882E3?K=49e6046a8d138790282a0175&hd=0&ymovie=1&myp=0&ts=445&ypp=2&ctype=12&ev=1&token=6077&oip=3079203019&ep=cCaVH06KVscE7CTagT8bNni2d3QMXP4J9h%2BHgdJjALshSeC460%2FQzpXGSIlCFPoZByYHFpqDqdPgGEhmYfQ3qRsQ2DqqSPrgi%2Ffm5a8gspMGZRo%2Bes%2Bit1SYQzf2",
	 *  "ftype":"mp4",
	 *  "bytes":16752742,
	 *  "seconds":445,
	 *  "time":"07:25",
	 *  "size":"15.98MB"
	 *  }
	 *
	 **/

	public String url,
				type,
				time,
				size;

	public int bytes, seconds;

	public VideoUrl() {

	}

	public VideoUrl(String url, String type, String time, String size, int bytes, int seconds) {
		this.url = url;
		this.type = type;
		this.time = time;
		this.size = size;
		this.bytes = bytes;
		this.seconds = seconds;
	}

	public JSONObject toJSONObject() {
		JSONHelper helper = new JSONHelper();
		helper.write("furl", url);
		helper.write("ftype", type);
		helper.write("bytes", bytes);
		helper.write("seconds", seconds);
		helper.write("time", time);
		helper.write("size", size);
		return helper.toJSONObject();
	}

}