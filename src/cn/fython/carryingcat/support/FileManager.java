package cn.fython.carryingcat.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

	public static String getSDCardRootPath() {
		return android.os.Environment.getExternalStorageDirectory().toString();
	}

	public static String getStorageDirPath() {
		return getSDCardRootPath() + "/CarryingCat";
	}

	public static String getDownloadDirPath(boolean withSDRoot) {
		return (withSDRoot ? getSDCardRootPath() : "")
				+ "/Android/data/cn.fython.carryingcat/files/download";
	}

	public static String getMyVideoDirPath() {
		return getStorageDirPath() + "/video";
	}

	/** 获取文件夹中的文件夹列表 */
	public static ArrayList<String> getPathsInPath(String path) {
		ArrayList<String> items = new ArrayList<String>();
		for (File file : (new File(path).listFiles())) {
			if (file.isDirectory()) {
				items.add(file.getPath());
			}
		}
		return items;
	}

	/** 初始化搬喵文件夹 */
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

	/** 保存到App data */
	public void saveToInternal(String fileName, String content) {
		try {
			saveFile(Environment.getDataDirectory() + "/data/" + mContext.getPackageName() + "/" + fileName, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 删除文件夹 */
	public static void deleteDir(String path) {
		File file = new File(path);
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
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

	/** 保存String为文件 */
	public static void saveFile(String path, String text) throws IOException {
		File file = new File(path);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(text.getBytes());
		fos.close();
	}

	/** 保存Bitmap为文件 */
	public static void saveBitmap(String path, Bitmap b) throws IOException {
		File f = new File(path);
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 读取文件为String */
	public static String readFile(String name) throws IOException {
		File file = new File(name);
		InputStream is = new FileInputStream(file);

		byte b[] = new byte[(int) file.length()];

		is.read(b);
		is.close();

		String string = new String(b);

		return string;
	}

	/** 创建文件夹 */
	public void makeDir(String path) {
		File file = new File(path);
		try {
			file.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 复制文件夹 */
	public static void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
		if (sourceLocation.isDirectory()) {
			if ((!targetLocation.exists() && !targetLocation.mkdirs()) && !targetLocation.isDirectory()) {
				throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
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

	/** 寻找第一个视频文件 */
	public static String findFirstVideoFile(final String path) {
		File[] list = new File(path).listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				String fileName = file.getPath();
				if (!file.isDirectory()) {
					if (fileName.endsWith(".flv") || fileName.endsWith(".mp4") || fileName.endsWith(".m3u8")) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}

		});
		return list[0].getAbsolutePath();
	}

	/** 创建视频预览图 */
	public static Bitmap createVideoThumbnail(String filePath) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(filePath);
			bitmap = retriever.getFrameAtTime();
		} catch (IllegalArgumentException e) {
			// Assume this is a corrupt video file
		} catch (RuntimeException e) {
			// Assume this is a corrupt video file.
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
				// Ignore failures while cleaning up.
			}
		}
		return bitmap;
	}

	/** 获取文件夹总大小 */
	public static long getFolderSize(File file) throws Exception{
		long size = 0;
		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++)
		{
			if (fileList[i].isDirectory())
			{
				size = size + getFolderSize(fileList[i]);
			} else
			{
				size = size + fileList[i].length();
			}
		}
		return size;
	}

}