package cn.fython.carryingcat.support.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.fython.carryingcat.support.Utility;
import cn.fython.carryingcat.support.VideoSource;

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

	public static VideoSource getVideoSource(String url) {
		String encodedUrl = convertToFlvxzUrl(url);
		Log.i(TAG, "Encoded by FyCafe.Me:" + encodedUrl);

		String json = getFlvxzResult(encodedUrl, true);

		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(json);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "The result from Flvxz is broken.");
			return null;
		}

		String videoTitle = null;
		ArrayList<String> videoUrl, videoName;
		videoUrl = new ArrayList<String>();
		videoName = new ArrayList<String>();

		// TODO Unfinished!!!

		JSONObject obj;
		try {
			obj = jsonArray.getJSONObject(0);
			videoTitle = obj.getString("title");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return new VideoSource(videoTitle, videoUrl, videoName);
	}

}
