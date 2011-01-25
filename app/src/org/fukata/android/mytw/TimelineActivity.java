package org.fukata.android.mytw;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.fukata.android.exandroid.loader.process.BaseRequest;
import org.fukata.android.exandroid.loader.process.ProcessLoader;
import org.fukata.android.exandroid.util.StringUtil;
import org.fukata.android.mytw.twitter.Twitter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class TimelineActivity extends ListActivity implements OnClickListener, OnItemLongClickListener {
	// 定期的に最新のツイートを取得する間隔
	static final int UPDATE_TIMELINE_INTERVAL = 60000;
	static final int UPDATE_TIMELINE_CHECK_INVERVAL = 10000;
	
	static final int RS_CODE_UPDATE_STATUS = 1;
	
	static final int MENU_UPDATE_STATUS = Menu.FIRST + 1;
	static final int MENU_NEW_TIMELINE = Menu.FIRST + 2;
	static final int MENU_REFRESH_TIMELINE = Menu.FIRST + 3;
	
	static final String INTENT_EXTRA_SELECTION = "selection";
	static final int INTENT_EXTRA_SELECTION_HEAD = 1;
	static final int INTENT_EXTRA_SELECTION_END = 2;
	
	Button more;
    TimelineAdapter adapter;
	List<TimelineItem> statuses;
	ProcessLoader timelineLoader;
	Twitter twitter;
	String lastStatuseId;
	String latestStatuseId;
	boolean isFirstLoad = true;
	
	long lastUpdateTimeline = System.currentTimeMillis();
	Timer intervalUpdateTimer;
	
	ItemDialog itemDialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        View footerView = getLayoutInflater().inflate(R.layout.timeline_footer, null);
        more = (Button) footerView.findViewById(R.id.more);
        more.setOnClickListener(this);
        itemDialog = new ItemDialog(this);
        
        twitter = new Twitter();
        timelineLoader = new ProcessLoader(this);
        statuses = new ArrayList<TimelineItem>();
        adapter = new TimelineAdapter(this, statuses);
        getListView().addFooterView(footerView);
        getListView().setOnItemLongClickListener(this);
        setListAdapter(adapter);
        
        attachUpdateInterval();
    }
	
	private void attachUpdateInterval() {
		intervalUpdateTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				long now = System.currentTimeMillis();
				if (now > lastUpdateTimeline+UPDATE_TIMELINE_INTERVAL) {
					// 他に更新中の場合はエンキューを行わない。
					if (timelineLoader.getLoaderQueue().size()==0) {
						loadTimeline(LoadMode.NEW);
					}
				}
			}
		};
		intervalUpdateTimer.schedule(task, UPDATE_TIMELINE_CHECK_INVERVAL, UPDATE_TIMELINE_CHECK_INVERVAL);
	}

	@Override
	protected void onResume() {
		super.onResume();
		timelineLoader.startBackgroundThread();
		if (isFirstLoad) {
			loadTimeline(LoadMode.REFRESH);
		} else {
			loadTimeline(LoadMode.NEW);
		}
		isFirstLoad = false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (timelineLoader != null) {
			timelineLoader.stopBackgroundThread();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_UPDATE_STATUS, Menu.NONE, R.string.update_status);
		menu.add(Menu.NONE, MENU_NEW_TIMELINE, Menu.NONE, R.string.new_timeline);
		menu.add(Menu.NONE, MENU_REFRESH_TIMELINE, Menu.NONE, R.string.refresh_timeline);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_UPDATE_STATUS:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(this, UpdateStatusActivity.class);
			startActivity(intent);
			break;
		case MENU_NEW_TIMELINE:
			loadTimeline(LoadMode.NEW);
			break;
		case MENU_REFRESH_TIMELINE:
			loadTimeline(LoadMode.REFRESH);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId()==R.id.more) {
			loadTimeline(LoadMode.MORE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RS_CODE_UPDATE_STATUS==requestCode) {
			loadTimeline(LoadMode.NEW);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	void postRetweet(final TimelineItem item) {
		Runnable successCallback = new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), R.string.retweet_successful, Toast.LENGTH_LONG).show();
				loadTimeline(LoadMode.NEW);
			}
		};
		timelineLoader.load(new BaseRequest(successCallback, null) {
			@Override
			public void processRequest(ProcessLoader loader) {
				twitter.postReTweet(item.getStatusId());
				super.processRequest(loader);
			}
		});
	}
	
	private void loadTimeline(final LoadMode mode) {
		preLoadTimeline(mode);
		final List<TimelineItem> timeline = new ArrayList<TimelineItem>();
		Runnable successCallback = new Runnable() {
			@Override
			public void run() {
				processUpdateTimeline(mode, timeline);
				lastUpdateTimeline = System.currentTimeMillis();
			};
		};
		timelineLoader.load(new BaseRequest(successCallback, null) {
			@Override
			public void processRequest(ProcessLoader loader) {
				List<TimelineItem> list = null;
				if (LoadMode.REFRESH==mode) {
					list = twitter.getHomeTimeline();
				} else if (LoadMode.NEW==mode) {
					list = twitter.getNewHomeTimeline(latestStatuseId);
				} else if (LoadMode.MORE==mode) {
					list = twitter.getMoreHomeTimeline(lastStatuseId);
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
	
	void processUpdateTimeline(LoadMode mode, final List<TimelineItem> timeline) {
		if (timeline.size()>0) {
			if (LoadMode.REFRESH==mode) {
				latestStatuseId = timeline.get(0).getStatusId();
				lastStatuseId = timeline.get(timeline.size()-1).getStatusId();
				adapter.clear();
				for (TimelineItem item : timeline) {
					adapter.add(item);
				}
				Toast.makeText(getApplicationContext(), R.string.update_successful, Toast.LENGTH_LONG).show();
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
				Toast.makeText(getApplicationContext(), R.string.update_successful, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	void preLoadTimeline(LoadMode mode) {
		if (LoadMode.REFRESH==mode) {
		} else if (LoadMode.NEW==mode) {
		} else if (LoadMode.MORE==mode) {
			more.setEnabled(false);
			more.setText(R.string.retrieving);
		}
	}
	
	enum LoadMode {
		REFRESH, NEW, MORE;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
		if (statuses.size()<=position) {
			return false;
		}
		
		TimelineItem item = statuses.get(position);
		if (item==null) {
			return false;
		}

		itemDialog.show(item);
		return false;
	};
	
	class ItemDialog extends AlertDialog.Builder {
		ListView optionsView;
		ArrayAdapter<String> optionsAdapter;
		Activity activity;
		static final int OPTION_RETWEET = 0;
		static final int OPTION_RETWEET_WITH_COMMENT = 1;
		static final int OPTION_REPLY = 2;
		
		final String[] options = {
			getString(R.string.retweet), 
			getString(R.string.retweet_with_comment),
			getString(R.string.reply),
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
						intent.putExtra(TimelineActivity.INTENT_EXTRA_SELECTION, TimelineActivity.INTENT_EXTRA_SELECTION_HEAD);
						startActivity(intent);
					} else if (OPTION_REPLY==which) {
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setClass(activity, UpdateStatusActivity.class);
						intent.putExtra(Intent.EXTRA_TEXT, "@"+item.getUsername()+" ");
						intent.putExtra(TimelineActivity.INTENT_EXTRA_SELECTION, TimelineActivity.INTENT_EXTRA_SELECTION_END);
						startActivity(intent);
					}
				}
			});
			create().show();
		}
	}
}
