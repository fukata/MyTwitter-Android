package org.fukata.android.mytw.util;

public class StringUtils {
	
	public static String strimwidth(String str, int width, String continueString) {
		String ret = "";
		if (str == null) {
			return ret;
		}
		if (continueString == null) {
			continueString = "";
		}
		if (str.length() <= width) {
			return str;
		} else {
			return str.substring(0, width - continueString.length()) + continueString;
		}
	}

}
