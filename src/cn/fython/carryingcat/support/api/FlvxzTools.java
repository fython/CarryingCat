package cn.fython.carryingcat.support.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.fython.carryingcat.support.Utility;
import cn.fython.carryingcat.support.VideoItem;
import cn.fython.carryingcat.support.VideoSource;
import cn.fython.carryingcat.support.VideoUrl;

public class FlvxzTools {

	private final static String FYCAFE_CONVERT_API_URL = "http://fycafe.me/carryingcat/convertUrlForFlvxz.php?url=";
	private final static String FLVXZ_API_URL = "http://api.flvxz.com";

	/** PLEASE USE YOUR OWN TOKEN INSTEAD OF THIS **/
	private final static String FLVXZ_TOKEN = "824892182ec14c8286ce0985e861381d";

	public final static String TAG = "FlvxzTools";

	private static String getFlvxzResult(String encodedUrl, boolean useJSON) {
		String result;

		String url = FLVXZ_API_URL
				+ addVarToUrl("token", FLVXZ_TOKEN)
				+ addVarToUrl("url", encodedUrl)
				+ (useJSON ? addVarToUrl("jsonp", "purejson") : "");
		Log.i(TAG, "url: " + url);

		result = Utility.sendHttpMessage(url, "GET", null);

		Log.i(TAG, "result: " + result);
		return result;
	}

	private static String addVarToUrl(String varname, String var) {
		return "/" + varname + "/" + var;
	}

	private static String convertToFlvxzUrl(String source) {
		return Utility.sendHttpMessage(FYCAFE_CONVERT_API_URL + source, "GET", null);
	}

	public static VideoItem getVideoItem(String url) throws JSONException{
		String encodedUrl = convertToFlvxzUrl(url);
		Log.i(TAG, "Encoded by FyCafe.Me:" + encodedUrl);

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

			JSONObject obj = null;
			obj = array.getJSONObject(i);

			if (obj != null) {
				try {
					/** 读取文件URL列表 **/
					ArrayList<VideoUrl> urls = new ArrayList<VideoUrl>();
					JSONArray urlarray = obj.getJSONArray("files");

					for (int j = 0; j < urlarray.length(); j++) {
						JSONObject obj1 = urlarray.getJSONObject(j);
						String furl, ftype, time, size;
						int bytes = 0, seconds = 0;
						furl = obj1.getString("furl");
						ftype = obj1.getString("ftype");
						try {
							size = obj1.getString("size");
						} catch (JSONException e) {
							size = "Unknown";
						}
						time = obj1.getString("time");
						try {
							bytes = obj1.getInt("bytes");
						} catch (JSONException e) {
						} finally {
							if (size == "Unknown") {
								size = String.valueOf(bytes) + "B";
							}
						}
						seconds = obj1.getInt("seconds");
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
					try {
						vs.title = obj.getString("title");
					} catch (Exception e) {
						Log.i(TAG, "当前VideoSource无法读取title");
					}
					try {
						vs.site = obj.getString("site");
					} catch (Exception e) {
						Log.i(TAG, "当前VideoSource无法读取site");
					}
					try {
						vs.playurl = obj.getString("playurl");
					} catch (Exception e) {
						Log.i(TAG, "当前VideoSource无法读取playurl");
					}
					try {
						vs.imgurl = obj.getString("img");
					} catch (Exception e) {
						Log.i(TAG, "当前VideoSource无法读取imgurl");
					}
					try {
						vs.quality = obj.getString("quality");
					} catch (Exception e) {
						Log.i(TAG, "当前VideoSource无法读取quality");
					}
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
