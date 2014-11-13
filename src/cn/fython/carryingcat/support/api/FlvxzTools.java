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
			throw e;
		}

		String videoTitle = null;
		ArrayList<VideoSource> videoSrc = new ArrayList<VideoSource>();
		int videoCount = jsonArray.length();

		// TODO Untested!!

		for (int i = 0; i < videoCount; i++) {

			JSONObject obj = null;
			obj = jsonArray.getJSONObject(i);

			if (obj != null) {
				try {
					/** 读取文件URL列表 **/
					ArrayList<VideoUrl> urls = new ArrayList<VideoUrl>();
					JSONArray urlarray = obj.getJSONArray("files");

					for (int j = 0; j < urlarray.length(); j++) {
						JSONObject obj1 = urlarray.getJSONObject(j);
						urls.add(
								new VideoUrl(
										obj1.getString("furl"),
										obj1.getString("ftype"),
										obj1.getString("time"),
										obj1.getString("size"),
										obj1.getInt("bytes"),
										obj1.getInt("seconds")
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

		return new VideoItem(videoSrc);
	}

}
