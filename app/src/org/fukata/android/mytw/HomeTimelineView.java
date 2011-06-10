package org.fukata.android.mytw;

import java.util.List;

import org.fukata.android.mytw.database.schema.TweetSchema.TweetType;

import android.content.Context;
import android.util.Log;

public class HomeTimelineView extends TimelineView {

	public HomeTimelineView(Context context, TimelineActivity activity) {
		super(context, activity);
	}

	@Override
	List<TimelineItem> getMoreTimeline(String lastStatuseId) {
		Log.d(getClass().getSimpleName(), "getMoreTimeline");
		return parentActivity.twitter.getMoreHomeTimeline(lastStatuseId);
	}

	@Override
	List<TimelineItem> getNewTimeline(String latestStatuseId) {
		Log.d(getClass().getSimpleName(), "getNewTimeline");
		return parentActivity.twitter.getNewHomeTimeline(latestStatuseId);
	}

	@Override
	List<TimelineItem> getTimeline() {
		Log.d(getClass().getSimpleName(), "getTimeline");
		return parentActivity.twitter.getHomeTimeline();
	}

	@Override
	TweetType getTweetType() {
		return TweetType.HOME;
	}
}
