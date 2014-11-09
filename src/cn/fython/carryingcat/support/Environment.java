package cn.fython.carryingcat.support;

public class Environment {

	public static String getStorageDirPath() {
		return android.os.Environment.getExternalStorageDirectory().toString() + "/CarryingCat";
	}

}
