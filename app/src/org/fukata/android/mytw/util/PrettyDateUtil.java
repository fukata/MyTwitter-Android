package org.fukata.android.mytw.util;

import java.util.Date;

public enum PrettyDateUtil {
	;

	public static String toString(Date date) {
		long	current = (new Date()).getTime(),
			timestamp = date.getTime(),
			diff = (current - timestamp)/1000;
		int	amount = 0;
		String	what = "";

		/**
		 * Second counts
		 * 3600: hour
		 * 86400: day
		 * 604800: week
		 * 2592000: month
		 * 31536000: year
		 */

		if(diff > 31536000) {
			amount = (int)(diff/31536000);
			what = "year";
		}
		else if(diff > 31536000) {
			amount = (int)(diff/31536000);
			what = "month";
		}
		else if(diff > 604800) {
			amount = (int)(diff/604800);
			what = "week";
		}
		else if(diff > 86400) {
			amount = (int)(diff/86400);
			what = "day";
		}
		else if(diff > 3600) {
			amount = (int)(diff/3600);
			what = "hour";
		}
		else if(diff > 60) {
			amount = (int)(diff/60);
			what = "minute";
		}
		else {
			amount = (int)diff;
			what = "second";
			if(amount < 6) {
				return "Just now";
			}
		}

		if(amount == 1) {
			if(what.equals("day")) {
				return "Yesterday";
			}
			else if(what.equals("week") || what.equals("month") || what.equals("year")) {
				return "Last " + what;
			}
		}
		else {
			what += "s";
		}

		return amount + " " + what + " ago";
	}
}