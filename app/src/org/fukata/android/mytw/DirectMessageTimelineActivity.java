package org.fukata.android.mytw;

import java.util.List;

public class DirectMessageTimelineActivity extends TimelineActivity {
	@Override
	TimelineAdapter newInstanceTimelineAdapter() {
		return new DirectMessageTimelineAdapter(this, statuses);
	}
	
	@Override
	List<TimelineItem> getTimeline() {
		return twitter.getDirectMessages();
	}
	
	@Override
	List<TimelineItem> getNewTimeline(String latestStatuseId) {
		return twitter.getNewDirectMessages(latestStatuseId);
	}
	
	@Override
	List<TimelineItem> getMoreTimeline(String lastStatuseId) {
		return twitter.getMoreDirectMessages(lastStatuseId);
	}
}
