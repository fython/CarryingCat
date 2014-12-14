package cn.fython.carryingcat.support.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.fython.carryingcat.support.JSONArrayHelper;
import cn.fython.carryingcat.support.JSONHelper;
import cn.fython.carryingcat.support.Utility;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.support.VideoSource;
import cn.fython.carryingcat.support.VideoUrl;

public class FlvxzTools {

	private final static String FLVXZ_API_URL = "http://api.flvxz.com";

	/** PLEASE CREATE "Secret.java" AND USE IT FOR SAVING YOUR OWN TOKEN **/
	private final static String FLVXZ_TOKEN = Secret.FLVXZ_TOKEN;

	public final static String TAG = "FlvxzTools";

	private static String getFlvxzResult(String encodedUrl, boolean useJSON) {
		String result;

		String url = FLVXZ_API_URL
				+ addVarToUrl("token", FLVXZ_TOKEN)
				+ addVarToUrl("url", encodedUrl)
				+ (useJSON ? addVarToUrl("jsonp", "purejson") : "");
		Log.i(TAG, "url: " + url);

		result = Utility.httpGet(url);

		Log.i(TAG, "result: " + result);
		return result;
	}

	private static String addVarToUrl(String varname, String var) {
		return "/" + varname + "/" + var;
	}

	private static String convertToFlvxzUrl(String source) {
		String result = source;
		try {
			result.replace("://", ":##");
		} catch (Exception e) {

		}
		result = Utility.encryptBase64(result.getBytes());
		try {
			result.replace("+/", "-_");
		} catch (Exception e) {

		}
		return result;
	}

	public static VideoItem getVideoItem(String url) throws JSONException{
		String encodedUrl = convertToFlvxzUrl(url);
		Log.i(TAG, "Encoded result:" + encodedUrl);

		String json = getFlvxzResult(encodedUrl, true);

		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(json);
		} catch (JSONException e) {
			Log.e(TAG, "The result from Flvxz is broken.");
			e.printStackTrace();
			return null;
		}

		ArrayList<VideoSource> videoSrc = getVideoSource(jsonArray);

		VideoItem videoItem = new VideoItem(videoSrc);
		Log.i(TAG, "读取VideoItem完毕, 数据输出:" + videoItem.toString());
		return videoItem;
	}

	public static ArrayList<VideoSource> getVideoSource(JSONArray array) throws JSONException {
		ArrayList<VideoSource> videoSrc = new ArrayList<VideoSource>();

		int videoCount = array.length();

		for (int i = 0; i < videoCount; i++) {

			JSONHelper obj = new JSONHelper(array.getJSONObject(i));

			if (obj != null) {
				try {
					/** 读取文件URL列表 **/
					ArrayList<VideoUrl> urls = new ArrayList<VideoUrl>();
					JSONArrayHelper<JSONObject> urlarray = (JSONArrayHelper<JSONObject>) obj.readJSONArray("files");

					for (int j = 0; j < urlarray.length(); j++) {
						JSONHelper obj1 = new JSONHelper(urlarray.getJSONObject(j));
						String furl, ftype, time, size;
						int bytes = 0, seconds = 0;
						furl = obj1.readString("furl");
						ftype = obj1.readString("ftype");
						size = obj1.readString("size");
						if (size == null) size = "Unknown";
						time = obj1.readString("time");
						bytes = obj1.readInt("bytes", 0);
						if (size == "Unknown") {
							size = String.valueOf(bytes) + "B";
						}
						seconds = obj1.readInt("seconds", 0);
						urls.add(
								new VideoUrl(
										furl,
										ftype,
										time,
										size,
										bytes,
										seconds
								)
						);
					}

					/** 装填VideoSource **/
					VideoSource vs = new VideoSource(null, urls);
					vs.title = obj.readString("title");
					vs.site = obj.readString("site");
					vs.playurl = obj.readString("playurl");
					vs.imgurl = obj.readString("img");
					vs.quality = obj.readString("quality");
					videoSrc.add(vs);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(TAG, "当前VideoSource读取失败!");
				}
			}

		}

		return videoSrc;
	}

}
