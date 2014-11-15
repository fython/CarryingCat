package cn.fython.carryingcat.support;

import java.io.File;
import java.util.ArrayList;

public class Environment {

	public static String getStorageDirPath() {
		return android.os.Environment.getExternalStorageDirectory().toString() + "/CarryingCat";
	}

	public static String getDownloadDirPath() {
		return getStorageDirPath() + "/download";
	}

	public static String getMyVideoDirPath() {
		return getStorageDirPath() + "/video";
	}

	public static ArrayList<String> getPathsInPath(String path) {
		ArrayList<String> items = new ArrayList<String>();
		for (File file:(new File(path).listFiles())) {
			if (file.isDirectory()) {
				items.add(file.getPath());
			}
		}
		return items;
	}

}
