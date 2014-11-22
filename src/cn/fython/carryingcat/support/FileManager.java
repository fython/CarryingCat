package cn.fython.carryingcat.support;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileManager {

	private Context mContext;

	public FileManager(Context context) {
		this.mContext = context;
	}

	public static String getStorageDirPath() {
		return android.os.Environment.getExternalStorageDirectory().toString() + "/CarryingCat";
	}

	public static String getDownloadDirPath(boolean withSDRoot) {
		return (withSDRoot ? Environment.getExternalStorageDirectory().toString() : "")
				+ "/Android/data/cn.fython.carryingcat/files/download";
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
		File ccDownload = new File(getDownloadDirPath(true));
		File ccMyVideo = new File(getMyVideoDirPath());
		if (!ccRoot.exists()) {
			ccRoot.mkdirs();
		}
		if (!ccDownload.exists()) {
			ccDownload.mkdirs();
		}
		if (!ccMyVideo.exists()) {
			ccMyVideo.mkdirs();
		}
	}

	public void saveToInternal(String fileName, String content) {
		try {
			saveFile(Environment.getDataDirectory() + "/data/" + mContext.getPackageName() + "/" + fileName, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteDir(String path) {
		File file = new File(path);
		if (file.isFile()) {
			file.delete();
			return;
		}

		if(file.isDirectory()){
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				deleteDir(childFiles[i].getAbsolutePath());
			}
			file.delete();
		}
	}

	public static void saveFile(String name, String text) throws IOException {
		File file = new File(name);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(text.getBytes());
		fos.close();
	}

	public String readFile(String name) throws IOException {
		File file = new File(name);
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

	public static void copyDirectory(File sourceLocation , File targetLocation) throws IOException {
		if (sourceLocation.isDirectory()) {
			if ((!targetLocation.exists() && !targetLocation.mkdirs()) && !targetLocation.isDirectory()) {
				throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
			}

			String[] children = sourceLocation.list();
			for (int i=0; i<children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]),
						new File(targetLocation, children[i]));
			}
		} else {
			// make sure the directory we plan to store the recording in exists
			File directory = targetLocation.getParentFile();
			if (directory != null && !directory.exists() && !directory.mkdirs()) {
				throw new IOException("Cannot create dir " + directory.getAbsolutePath());
			}

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

}
