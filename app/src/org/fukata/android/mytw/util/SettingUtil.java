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
	
	private static final String KEY_ACCOUNT_ID = "account_id";
	private static final String KEY_ACCOUNT_NAME = "account_name";
	private static final String KEY_API_SERVER_URL = "api_server_url";
	private static final String KEY_FONT_SIZE = "font_size";
	private static final String KEY_AUTO_INTERVAL = "auto_interval";
	private static final String KEY_BACKGROUND_PROCESS = "background_process";
	private static final String KEY_NOTIFICATION = "notification";
	private static final String KEY_TIMELINE_COUNT = "timeline_count";
	
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
	
	public static boolean setAutoInterval(int interval) {
		Editor edit = preferences.edit();
		edit.putInt(KEY_AUTO_INTERVAL, interval);
		return edit.commit();
	}
	
	public static boolean setBackgroundProcess(boolean process) {
		Editor edit = preferences.edit();
		edit.putBoolean(KEY_BACKGROUND_PROCESS, process);
		return edit.commit();
	}
	
	public static boolean setNotification(boolean notification) {
		Editor edit = preferences.edit();
		edit.putBoolean(KEY_NOTIFICATION, notification);
		return edit.commit();
	}
	
	public static boolean setAccountName(String accountName) {
		Editor edit = preferences.edit();
		edit.putString(KEY_ACCOUNT_NAME, accountName);
		return edit.commit();
	}
	
	public static boolean setAccountId(String accountId) {
		Editor edit = preferences.edit();
		edit.putString(KEY_ACCOUNT_ID, accountId);
		return edit.commit();
	}
	
	public static boolean setTimelineCount(int timelineCount) {
		Editor edit = preferences.edit();
		edit.putInt(KEY_TIMELINE_COUNT, timelineCount);
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
	
	public static boolean isAutoIntervalEnabled() {
		return getAutoIntervalIndex()>0;
	}
	
	public static int getAutoIntervalIndex() {
		return preferences.getInt(KEY_AUTO_INTERVAL, 0);
	}
	
	public static int getAutoInterval() {
		int i = getAutoIntervalIndex();
		int[] intervals = context.getResources().getIntArray(R.array.auto_interval_values);
		if (intervals.length>i) {
			return intervals[i];
		} else {
			return intervals[0];
		}
	}
	
	public static boolean isBackgroundProcessEnabled() {
		return preferences.getBoolean(KEY_BACKGROUND_PROCESS, false);
	}
	
	public static boolean isNotificationEnabled() {
		return preferences.getBoolean(KEY_NOTIFICATION, false);
	}
	
	public static String getAccountName() {
		return preferences.getString(KEY_ACCOUNT_NAME, "");
	}
	
	public static String getAccountId() {
		return preferences.getString(KEY_ACCOUNT_ID, "");
	}
	
	public static int getTimelineCountIndex() {
		return preferences.getInt(KEY_TIMELINE_COUNT, 0);
	}
	
	public static int getTimelineCount() {
		int i = getTimelineCountIndex();
		int[] counts = context.getResources().getIntArray(R.array.timeline_count_values);
		if (counts.length>i) {
			return counts[i];
		} else {
			return counts[0];
		}
	}
}

