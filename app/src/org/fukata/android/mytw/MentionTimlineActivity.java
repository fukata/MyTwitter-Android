package org.fukata.android.mytw;

import java.util.List;

public class MentionTimlineActivity extends TimelineActivity {
	@Override
	List<TimelineItem> getTimeline() {
		return twitter.getMentions();
	}
	
	@Override
	List<TimelineItem> getNewTimeline(String latestStatuseId) {
		return twitter.getNewMentions(latestStatuseId);
	}
	
	@Override
	List<TimelineItem> getMoreTimeline(String lastStatuseId) {
		return twitter.getMoreMentions(lastStatuseId);
	}

	@Override
	int getNotifyNewTweetTabIndex() {
		return MyTwitterActivity.TAB_MENTIONS;
	}

	@Override
	CharSequence getNotifyNewTweetContentTitle() {
		return getString(R.string.notify_new_mention, incrementCount);
	}

	@Override
	CharSequence getNotifyNewTweetTcikerText() {
		return getString(R.string.notify_new_mention, incrementCount);
	}
}
