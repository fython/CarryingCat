package cn.fython.carryingcat.support;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

	public static final String XML_NAME = "settings";

	private static Settings sInstance;

	private SharedPreferences mPrefs;

	public static Settings getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new Settings(context);
		}

		return sInstance;
	}

	private Settings(Context context) {
		mPrefs = context.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
	}

	public Settings putBoolean(String key, boolean value) {
		mPrefs.edit().putBoolean(key, value).commit();
		return this;
	}

	public boolean getBoolean(String key, boolean def) {
		return mPrefs.getBoolean(key, def);
	}

	public Settings putInt(String key, int value) {
		mPrefs.edit().putInt(key, value).commit();
		return this;
	}

	public int getInt(String key, int defValue) {
		return mPrefs.getInt(key, defValue);
	}

	public Settings putString(String key, String value) {
		mPrefs.edit().putString(key, value).commit();
		return this;
	}

	public String getString(String key, String def) {
		return mPrefs.getString(key, def);
	}

	/** 更简单明了地获取应用设置 */

	public boolean isTintEnabled() {
		return this.getBoolean(Settings.Field.KITKAT_TINT, false);
	}

	public void setTintEnabled(boolean isEnabled) {
		this.putBoolean(Field.KITKAT_TINT, isEnabled);
	}

	public boolean isBilibiliEnabled() {
		return this.getBoolean(Field.BILIBILI_ENABLED, true);
	}

	public void setBilibiliEnabled(boolean isEnabled) {
		this.putBoolean(Field.BILIBILI_ENABLED, isEnabled);
	}

	public String getBilibiliPath() {
		return this.getString(Field.BILIBILI_PATH, getBilibiliDefaultPath());
	}

	// 获取默认Bilibili客户端目录
	public String getBilibiliDefaultPath() {
		return FileManager.getSDCardRootPath() + "/Android/data/tv.danmaku.bili/download";
	}

	// 此处应当交由UI部分检验目录路径的合法性
	public void setBilibiliPath(String availablePath) {
		this.putString(Field.BILIBILI_PATH, availablePath);
	}

	public class Field {

		public static final String KITKAT_TINT = "FORCE_TINT";

		public static final String BILIBILI_ENABLED = "BILIBILI";
		public static final String BILIBILI_PATH = "BILIBILI_PATH";

	}

}