package org.fukata.android.mytw;

import java.util.List;

import android.content.Context;
import android.util.Log;

public class MentionTimelineView extends TimelineView {
	public MentionTimelineView(Context context, TimelineActivity activity) {
		super(context, activity);
	}
	
	@Override
	List<TimelineItem> getMoreTimeline(String lastStatuseId) {
		Log.d(getClass().getSimpleName(), "getMoreTimeline");
		return parentActivity.twitter.getMoreMentions(lastStatuseId);
	}

	@Override
	List<TimelineItem> getNewTimeline(String latestStatuseId) {
		Log.d(getClass().getSimpleName(), "getNewTimeline");
		return parentActivity.twitter.getNewMentions(latestStatuseId);
	}

	@Override
	List<TimelineItem> getTimeline() {
		Log.d(getClass().getSimpleName(), "getTimeline");
		return parentActivity.twitter.getMentions();
	}

//	@Override
//	CharSequence getTitle() {
//		return parentActivity.getString(R.string.title_mentions);
//	}
}
