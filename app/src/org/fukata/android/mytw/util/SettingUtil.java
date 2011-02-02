package org.fukata.android.mytw.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fukata.android.mytw.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public enum SettingUtil {
	;
	private static final Pattern URL_PATTERN = Pattern.compile("(https?://)[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+");
	
	private static final String KEY_API_SERVER_URL = "api_server_url";
	private static final String KEY_FONT_SIZE = "font_size";
	
	private static final String NAME = "mytw";
	private static SharedPreferences preferences;
	private static Context context;
	
	public static void init(Context context) {
		SettingUtil.context = context;
		preferences = context.getSharedPreferences(NAME, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
	}

	public static boolean setApiServerUrl(String url) {
		// url形式チェック
		Matcher matcher = URL_PATTERN.matcher(url);
		if (matcher.matches()) {
			Editor edit = preferences.edit();
			edit.putString(KEY_API_SERVER_URL, url);
			return edit.commit();
		} else {
			return false;
		}
	}
	
	public static boolean setFontSize(int size) {
		Editor edit = preferences.edit();
		edit.putInt(KEY_FONT_SIZE, size);
		return edit.commit();
	}
	
	public static String getApiServerUrl() {
		return preferences.getString(KEY_API_SERVER_URL, null);
	}
	
	public static int getFontSizeIndex() {
		return preferences.getInt(KEY_FONT_SIZE, 0);
	}

	public static float getFontSize() {
		int i = getFontSizeIndex();
		int[] fontSizes = context.getResources().getIntArray(R.array.font_size_values);
		if (fontSizes.length>i) {
			return fontSizes[i];
		} else {
			return fontSizes[0];
		}
	}
}
