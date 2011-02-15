package org.fukata.android.mytw;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.fukata.android.exandroid.loader.process.BaseRequest;
import org.fukata.android.exandroid.loader.process.ProcessLoader;
import org.fukata.android.exandroid.util.StringUtil;
import org.fukata.android.mytw.MyTwitterActivity.LoadMode;
import org.fukata.android.mytw.util.SettingUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class TimelineView extends ListView implements View.OnClickListener, OnItemLongClickListener {
	// 定期的に最新のツイートを取得する間隔
	static final int UPDATE_TIMELINE_CHECK_INVERVAL = 10000;
	
	MyTwitterActivity parentActivity;
	List<TimelineItem> items;
	TimelineAdapter adapter;

	Button more;
	
	int lastLoadCount;
	String lastStatuseId;
	String latestStatuseId;
	long lastUpdateTimeline = System.currentTimeMillis();
	Timer intervalUpdateTimer;

	boolean isFirstLoad = true;
	ItemDialog itemDialog;

	public TimelineView(Context context, MyTwitterActivity activity) {
		super(context);
		parentActivity = activity;
		items = new ArrayList<TimelineItem>();
		adapter = newInstanceTimelineAdapter(context, items);
		itemDialog = newInstanceItemDialog();
		
        View footerView = activity.getLayoutInflater().inflate(R.layout.timeline_footer, null);
        more = (Button) footerView.findViewById(R.id.more);
        more.setOnClickListener(this);
        
        addFooterView(footerView);
        setOnItemLongClickListener(this);
        setAdapter(adapter);

        attachUpdateInterval();
	}
	
	ItemDialog newInstanceItemDialog() {
		return new ItemDialog(parentActivity);
	}

	void attachUpdateInterval() {
		intervalUpdateTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				long now = System.currentTimeMillis();
				int interval = SettingUtil.getAutoInterval()*1000;
				if (interval>0 && now>lastUpdateTimeline+interval) {
					// 他に更新中の場合はエンキューを行わない。
					if (parentActivity.timelineLoader.getLoaderQueue().size()==0) {
						loadTimeline(LoadMode.NEW);
					}
				}
			}
		};
		intervalUpdateTimer.schedule(task, UPDATE_TIMELINE_CHECK_INVERVAL, UPDATE_TIMELINE_CHECK_INVERVAL);
	}

	TimelineAdapter newInstanceTimelineAdapter(Context context, List<TimelineItem> items) {
		return new TimelineAdapter(context, items);
	}
	
	void loadTimeline(final LoadMode mode) {
		preLoadTimeline(mode);
		final List<TimelineItem> timeline = new ArrayList<TimelineItem>();
		Runnable successCallback = generateSuccessCallback(timeline, mode);
		processLoadTimeline(timeline, mode, successCallback);
	}
	
	void preLoadTimeline(LoadMode mode) {
		if (LoadMode.REFRESH==mode) {
		} else if (LoadMode.NEW==mode) {
		} else if (LoadMode.MORE==mode) {
			more.setEnabled(false);
			more.setText(R.string.retrieving);
		}
	}

	void processLoadTimeline(final List<TimelineItem> timeline, final LoadMode mode, Runnable successCallback) {
		parentActivity.timelineLoader.load(new BaseRequest(successCallback, null) {

			@Override
			public void processRequest(ProcessLoader loader) {
				lastLoadCount = 0;
				List<TimelineItem> list = null;
				if (LoadMode.REFRESH==mode) {
					list = getTimeline();
				} else if (LoadMode.NEW==mode) {
					list = getNewTimeline(latestStatuseId);
					lastLoadCount = list.size();
				} else if (LoadMode.MORE==mode) {
					list = getMoreTimeline(lastStatuseId);
				}
				if (list!=null) {
					for (TimelineItem item : list) {
						timeline.add(item);
					}
				}
				super.processRequest(loader);
			}

		});
	}
	
	List<TimelineItem> getMoreTimeline(String lastStatuseId) {
		return parentActivity.twitter.getMoreHomeTimeline(lastStatuseId);
	}

	List<TimelineItem> getNewTimeline(String latestStatuseId) {
		return parentActivity.twitter.getNewHomeTimeline(latestStatuseId);
	}

	List<TimelineItem> getTimeline() {
		return parentActivity.twitter.getHomeTimeline();
	}
	
	Runnable generateSuccessCallback(final List<TimelineItem> timeline, final LoadMode mode) {
		Log.d(getClass().getSimpleName(), "generateSuccessCallback");
		Runnable callback = new Runnable() {
			@Override
			public void run() {
				// 現在表示されている最上のツイート位置
				int firstItemPosition = getFirstVisiblePosition();
				int firstItemTop = getChildCount()==0 || getChildAt(0)==null ? 0 : getChildAt(0).getTop();
				Log.d(getClass().getSimpleName(), "firstItemPosition="+firstItemPosition);
				Log.d(getClass().getSimpleName(), "firstItemTop="+firstItemTop);
				processUpdateTimeline(mode, timeline);
				processFocusItem(mode,firstItemPosition,firstItemTop);
//				processNotification(mode);
				lastUpdateTimeline = System.currentTimeMillis();
			};
		};
		return callback;
	}

	void processUpdateTimeline(LoadMode mode, final List<TimelineItem> timeline) {
		if (timeline.size()>0) {
			if (LoadMode.REFRESH==mode) {
				latestStatuseId = timeline.get(0).getStatusId();
				lastStatuseId = timeline.get(timeline.size()-1).getStatusId();
				adapter.clear();
				for (TimelineItem item : timeline) {
					adapter.add(item);
				}
				if (hasWindowFocus()) {
					Toast.makeText(parentActivity.getApplicationContext(), R.string.update_successful, Toast.LENGTH_LONG).show();
				}
			} else if (LoadMode.NEW==mode) {
				int insertAt = 0;
				for (TimelineItem item : timeline) {
					if (!StringUtil.equals(latestStatuseId, item.getStatusId())) {
						adapter.insert(item, insertAt);
						insertAt++;
					}
				}
				latestStatuseId = timeline.get(0).getStatusId();
			} else if (LoadMode.MORE==mode) {
				more.setText(R.string.more);
				more.setEnabled(true);
				for (TimelineItem item : timeline) {
					if (!StringUtil.equals(lastStatuseId, item.getStatusId())) {
						adapter.add(item);
					}
				}
				lastStatuseId = timeline.get(timeline.size()-1).getStatusId();
				if (hasWindowFocus()) {
					Toast.makeText(parentActivity.getApplicationContext(), R.string.update_successful, Toast.LENGTH_LONG).show();
				}
			}
		}
	}
	
//	void processNotification(LoadMode mode) {
//		if (hasWindowFocus() || !SettingUtil.isNotificationEnabled()) {
//			return;
//		}
//		
//		if (mode == LoadMode.NEW && incrementCount>0) {
//			notificationNewTweet();
//		}
//	}
	
	void processFocusItem(LoadMode mode, int firstItemPosition, int firstItemTop) {
		// 表示されているツイートが最上部以外の場合
		if (mode == LoadMode.NEW && firstItemPosition > 0) {
			//新しい選択位置を設定する。
			setSelectionFromTop(firstItemPosition+lastLoadCount, firstItemTop);
		}
	}

	void doResume() {
		Log.d(getClass().getSimpleName(), "doResume");
		if (isFirstLoad) {
			loadTimeline(LoadMode.REFRESH);
		} else {
			loadTimeline(LoadMode.NEW);
		}
		isFirstLoad = false;
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId()==R.id.more) {
			loadTimeline(LoadMode.MORE);
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
		if (items.size()<=position) {
			return false;
		}

		TimelineItem item = items.get(position);
		if (item==null) {
			return false;
		}

		itemDialog.show(item);
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (parentActivity.gestureDetector.onTouchEvent(event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	CharSequence getTitle() {
		return parentActivity.getString(R.string.title_home);
	}
	
	void postRetweet(final TimelineItem item) {
		Runnable successCallback = new Runnable() {
			@Override
			public void run() {
				Toast.makeText(parentActivity.getApplicationContext(), R.string.retweet_successful, Toast.LENGTH_LONG).show();
				loadTimeline(LoadMode.NEW);
			}
		};
		parentActivity.timelineLoader.load(new BaseRequest(successCallback, null) {
			@Override
			public void processRequest(ProcessLoader loader) {
				parentActivity.twitter.postReTweet(item.getStatusId());
				super.processRequest(loader);
			}
		});
	}
	
	void deleteTweet(final TimelineItem item) {
		//FIXME 未実装
	}
	
	class ItemDialog extends AlertDialog.Builder {
		ListView optionsView;
		ArrayAdapter<String> optionsAdapter;
		Activity activity;
		static final int OPTION_RETWEET = 0;
		static final int OPTION_RETWEET_WITH_COMMENT = 1;
		static final int OPTION_REPLY = 2;

		final String[] options = {
			parentActivity.getString(R.string.retweet), 
			parentActivity.getString(R.string.retweet_with_comment),
			parentActivity.getString(R.string.reply),
		};

		public ItemDialog(Activity activity) {
			super(activity);
			this.activity = activity;
			setTitle(R.string.options);
		}
		
		public void show(final TimelineItem item) {
			setItems(options, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (OPTION_RETWEET==which) {
						postRetweet(item);
					} else if (OPTION_RETWEET_WITH_COMMENT==which) {
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setClass(activity, UpdateStatusActivity.class);
						intent.putExtra(Intent.EXTRA_TEXT, " RT @"+item.getUsername()+": "+item.getStatus());
						intent.putExtra(MyTwitterActivity.INTENT_EXTRA_SELECTION, MyTwitterActivity.INTENT_EXTRA_SELECTION_HEAD);
						parentActivity.startActivity(intent);
					} else if (OPTION_REPLY==which) {
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setClass(activity, UpdateStatusActivity.class);
						intent.putExtra(Intent.EXTRA_TEXT, "@"+item.getUsername()+" ");
						intent.putExtra(MyTwitterActivity.INTENT_EXTRA_SELECTION, MyTwitterActivity.INTENT_EXTRA_SELECTION_END);
						parentActivity.startActivity(intent);
					}
				}
			});
			create().show();
		}
	}
}