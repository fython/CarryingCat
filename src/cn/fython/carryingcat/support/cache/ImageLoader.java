package cn.fython.carryingcat.support.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import cn.fython.carryingcat.R;
import cn.fython.carryingcat.support.VideoItem;

public class ImageLoader {

	private MemoryCache memoryCache = new MemoryCache();
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ExecutorService executorService;
	private Context mContext;

	private ArrayList<VideoItem> data;

	public ImageLoader(Context context, ArrayList<VideoItem> data) {
		mContext = context;
		executorService = Executors.newFixedThreadPool(5);
		this.data = data;
	}

	public void DisplayImage(String id, ImageView imageView, boolean isLoadOnlyFromCache) {
		imageViews.put(imageView, id);

		Bitmap bitmap = memoryCache.get(id);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else if (!isLoadOnlyFromCache){
			queuePhoto(id, imageView);
		}
	}

	private void queuePhoto(String id, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(id, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(long id) {
		Bitmap b = null;
		try {
			b = BitmapFactory.decodeFile(data.get((int) id).path + "/.preview");
		} catch (Exception e) {
			b = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_preview);
			e.printStackTrace();
		}
		return b;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(Long.parseLong(photoToLoad.url));
			memoryCache.put("" + Long.parseLong(photoToLoad.url), bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 *
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);

		}
	}
}