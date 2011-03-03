package org.fukata.android.mytw.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文字列のマッチの汎用クラス
 * 
 * @author smeghead
 *
 */
public class StringMatchUtils {
	static final Pattern URL_PATTERN = Pattern.compile("(https?://)[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+\\@\\,\\_\\!\\*\\(\\)]+", Pattern.MULTILINE | Pattern.DOTALL);

	/**
	 * 文字列が含んでいるURLをListで返す。
	 * @param str 検査対象文字列
	 * @return URL List
	 */
	public static List<String> getUrls(String str) {
		List<String> urls = new ArrayList<String>();
		if (str == null || str.length() == 0) return urls;
		
		Matcher m = URL_PATTERN.matcher(str);
		while (m.find()) {
			MatchResult mr = m.toMatchResult();
			urls.add(str.substring(mr.start(), mr.end()));
		}
		return urls;
	}
}
