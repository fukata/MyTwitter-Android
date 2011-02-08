package org.fukata.android.mytw;

import java.util.List;

import org.fukata.android.exandroid.loader.process.BaseRequest;
import org.fukata.android.exandroid.loader.process.ProcessLoader;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

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
	
	@Override
	void deleteTweet(final TimelineItem item) {
		Runnable successCallback = new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), R.string.delete_successful, Toast.LENGTH_LONG).show();
				adapter.remove(item);
			}
		};
		timelineLoader.load(new BaseRequest(successCallback, null) {
			@Override
			public void processRequest(ProcessLoader loader) {
				twitter.deleteDirectMessage(item.getStatusId());
				super.processRequest(loader);
			}
		});
	}
	
	@Override
	ItemDialog newInstanceItemDialog() {
		return new DmItemDialog(this);
	}
	
	class DmItemDialog extends ItemDialog {
		static final int OPTION_DIRECT_MESSAGE = 0;
		static final int OPTION_DELETE = 1;
		
		final String[] options = {
				getString(R.string.direct_message), 
				getString(R.string.delete), 
		};
		
		public DmItemDialog(Activity activity) {
			super(activity);
			this.activity = activity;
			setTitle(R.string.options);
		}
		
		public void show(final TimelineItem item) {
			setItems(options, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (OPTION_DIRECT_MESSAGE==which) {
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setClass(activity, UpdateStatusActivity.class);
						intent.putExtra(Intent.EXTRA_TEXT, "d "+item.getUsername()+" ");
						intent.putExtra(TimelineActivity.INTENT_EXTRA_SELECTION, TimelineActivity.INTENT_EXTRA_SELECTION_END);
						startActivity(intent);
					} else if (OPTION_DELETE==which) {
						deleteTweet(item);
					}
				}
			});
			create().show();
		}
	}
	
	@Override
	int getNotifyNewTweetTabIndex() {
		return MyTwitterActivity.TAB_DM;
	}

	@Override
	CharSequence getNotifyNewTweetContentTitle() {
		return getString(R.string.notify_new_direct_message, incrementCount);
	}

	@Override
	CharSequence getNotifyNewTweetTcikerText() {
		return getString(R.string.notify_new_direct_message, incrementCount);
	}
}
