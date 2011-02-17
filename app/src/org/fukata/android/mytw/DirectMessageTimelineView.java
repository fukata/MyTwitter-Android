package org.fukata.android.mytw;

import java.util.List;

import org.fukata.android.exandroid.loader.process.BaseRequest;
import org.fukata.android.exandroid.loader.process.ProcessLoader;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DirectMessageTimelineView extends TimelineView {

	public DirectMessageTimelineView(Context context, TimelineActivity activity) {
		super(context, activity);
	}

	@Override
	TimelineAdapter newInstanceTimelineAdapter(Context context, List<TimelineItem> items) {
		return new DirectMessageTimelineAdapter(context, items);
	}
	
	@Override
	List<TimelineItem> getMoreTimeline(String lastStatuseId) {
		Log.d(getClass().getSimpleName(), "getMoreTimeline");
		return parentActivity.twitter.getMoreDirectMessages(lastStatuseId);
	}

	@Override
	List<TimelineItem> getNewTimeline(String latestStatuseId) {
		Log.d(getClass().getSimpleName(), "getNewTimeline");
		return parentActivity.twitter.getNewDirectMessages(latestStatuseId);
	}

	@Override
	List<TimelineItem> getTimeline() {
		Log.d(getClass().getSimpleName(), "getTimeline");
		return parentActivity.twitter.getDirectMessages();
	}

//	@Override
//	CharSequence getTitle() {
//		return parentActivity.getString(R.string.title_dm);
//	}
	
	@Override
	void deleteTweet(final TimelineItem item) {
		Runnable successCallback = new Runnable() {
			@Override
			public void run() {
				Toast.makeText(parentActivity.getApplicationContext(), R.string.delete_successful, Toast.LENGTH_LONG).show();
				adapter.remove(item);
			}
		};
		parentActivity.timelineLoader.load(new BaseRequest(successCallback, null) {
			@Override
			public void processRequest(ProcessLoader loader) {
				parentActivity.twitter.deleteDirectMessage(item.getStatusId());
				super.processRequest(loader);
			}
		});
	}
	
	@Override
	ItemDialog newInstanceItemDialog() {
		return new DmItemDialog(parentActivity);
	}
	
	class DmItemDialog extends ItemDialog {
		static final int OPTION_DIRECT_MESSAGE = 0;
		static final int OPTION_DELETE = 1;

		final String[] options = {
				parentActivity.getString(R.string.direct_message), 
				parentActivity.getString(R.string.delete), 
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
						intent.putExtra(MyTwitterApp.INTENT_EXTRA_SELECTION, MyTwitterApp.INTENT_EXTRA_SELECTION_END);
						parentActivity.startActivity(intent);
					} else if (OPTION_DELETE==which) {
						deleteTweet(item);
					}
				}
			});
			create().show();
		}
	}
}
