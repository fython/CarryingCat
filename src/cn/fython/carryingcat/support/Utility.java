package cn.fython.carryingcat.support;

import android.app.Activity;
import android.content.res.Configuration;

public class Utility {

	public static boolean isLandscape(Activity activity) {
		Configuration c = activity.getResources().getConfiguration();
		if (c.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		} else {
			return false;
		}
	}

}
