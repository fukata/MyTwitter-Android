package org.fukata.android.mytw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fukata.android.exandroid.util.StringUtil;
import org.fukata.android.mytw.util.PrettyDateUtil;
import org.fukata.android.mytw.util.SettingUtil;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TimelineAdapter extends ArrayAdapter<TimelineItem> {
	LayoutInflater inflater; 
	List<TimelineItem> items;
	static final Pattern USERNAME_PATTERN = Pattern.compile("(\\.?@[a-zA-Z0-9]+)");
	static final Pattern URL_PATTERN = Pattern.compile("(https?://)[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+\\@\\,\\_\\!\\*\\(\\)]+");
	static final Pattern HASHTAG_PATTERN = Pattern.compile("(#[\\w]+)");
	
	public TimelineAdapter(Context context,	List<TimelineItem> items) {
		super(context, android.R.layout.simple_list_item_1, items);
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view==null) {
			view = inflater.inflate(R.layout.timeline_item, null);
		}
		
		
		TimelineItem item = getItem(position);
		decorateView(view, item);
		
		// username
		TextView username = (TextView) view.findViewById(R.id.timeline_username);
		username.setText(item.getUsername());
		// source
		TextView source = (TextView) view.findViewById(R.id.timeline_source);
		source.setText(item.getSource());
		// created_at
		TextView createdAt = (TextView) view.findViewById(R.id.timeline_created_at);
		createdAt.setText( PrettyDateUtil.toString(item.getCreatedAt()) );
		// status
		TextView status = (TextView) view.findViewById(R.id.timeline_status);
		status.setText(decorateStatus(item.getStatus()));

		updateTextSize(view);
		
		return view;
	}

	void updateTextSize(View view) {
		float size = SettingUtil.getFontSize();
		((TextView) view.findViewById(R.id.timeline_username)).setTextSize(size);
		((TextView) view.findViewById(R.id.timeline_from)).setTextSize(size);
		((TextView) view.findViewById(R.id.timeline_source)).setTextSize(size);
		((TextView) view.findViewById(R.id.timeline_created_at)).setTextSize(size);
		((TextView) view.findViewById(R.id.timeline_status)).setTextSize(size);
	}
	
	void decorateView(View view, TimelineItem item) {
		if (isMention(item, SettingUtil.getAccountName())) {
			view.setBackgroundColor(Color.DKGRAY);
			//orange
//			view.setBackgroundColor(Color.rgb(253, 126, 0));
		} else {
			view.setBackgroundColor(Color.BLACK);
		}
	}

	boolean isMention(TimelineItem item, String accountName) {
		String status = item.getStatus();
		Matcher usernameMatcher = USERNAME_PATTERN.matcher(status);
		usernameMatcher.reset();
		while (usernameMatcher.find()) {
			String username = usernameMatcher.group();
			if (StringUtil.equals(username, "@"+accountName)) {
				return true;
			}
		}
		
		return false;
	}

	CharSequence decorateStatus(String status) {
		Map<String, Boolean> replaced = new HashMap<String, Boolean>();

		// username
		Matcher usernameMatcher = USERNAME_PATTERN.matcher(status);
		usernameMatcher.reset();
		while (usernameMatcher.find()) {
			String username = usernameMatcher.group();
			if (replaced.containsKey(username)) {
				continue;
			}
			replaced.put(username, true);
			status = status.replace(username, "<font color=\"green\">"+username+"</font>");
		}
		
		// url
		Matcher urlMatcher = URL_PATTERN.matcher(status);
		urlMatcher.reset();
		while (urlMatcher.find()) {
			String url = urlMatcher.group();
			if (replaced.containsKey(url)) {
				continue;
			}
			replaced.put(url, true);
			status = status.replace(url, "<font color=\"aqua\">"+url+"</font>");
		}
		
		// hashtag
		Matcher hashtagMatcher = HASHTAG_PATTERN.matcher(status);
		hashtagMatcher.reset();
		while (hashtagMatcher.find()) {
			String hashtag = hashtagMatcher.group();
			if (replaced.containsKey(hashtag)) {
				continue;
			}
			replaced.put(hashtag, true);
			status = status.replace(hashtag, "<font color=\"fuchsia\">"+hashtag+"</font>");
		}
		
		return Html.fromHtml(status);
	}
}
