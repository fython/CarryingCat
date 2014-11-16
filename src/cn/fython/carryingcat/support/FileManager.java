package cn.fython.carryingcat.support;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FileManager {

	private Context mContext;

	public FileManager(Context context) {
		this.mContext = context;
	}

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

	public void initCarryingCatDirectory() {
		File ccRoot = new File(getStorageDirPath());
		File ccDownload = new File(getDownloadDirPath());
		File ccMyVideo = new File(getMyVideoDirPath());
		if (!ccRoot.exists()) {
			ccRoot.mkdir();
		}
		if (!ccDownload.exists()) {
			ccDownload.mkdir();
		}
		if (!ccMyVideo.exists()) {
			ccMyVideo.mkdir();
		}
	}

	public void saveToInternal(String fileName, String content) {
		try {
			saveFile(Environment.getDataDirectory() + "/data/" + mContext.getPackageName() + "/" + fileName, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveFile(String name, String text) throws IOException {
		File file = new File(name);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(text.getBytes());
		fos.close();
	}

	public String readFile(String name) throws IOException {
		File file = mContext.getFileStreamPath(name);
		InputStream is = new FileInputStream(file);

		byte b[] = new byte[(int) file.length()];

		is.read(b);
		is.close();

		String string = new String(b);

		return string;
	}

	public void makeDir(String path) {
		File file = new File(path);
		if (!file.exists() | file.isFile()) {
			file.mkdir();
		}
	}

}
