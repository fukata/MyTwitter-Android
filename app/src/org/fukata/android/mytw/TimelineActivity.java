package org.fukata.android.mytw;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.fukata.android.exandroid.loader.process.BaseRequest;
import org.fukata.android.exandroid.loader.process.ProcessLoader;
import org.fukata.android.exandroid.util.StringUtil;
import org.fukata.android.mytw.twitter.Twitter;
import org.fukata.android.mytw.util.SettingUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	static final int RS_CODE_RECONFIG = 2;
	
	static final int MENU_UPDATE_STATUS = Menu.FIRST + 1;
	static final int MENU_NEW_TIMELINE = Menu.FIRST + 2;
	static final int MENU_REFRESH_TIMELINE = Menu.FIRST + 3;
	static final int MENU_SETTINGS = Menu.FIRST + 4;
	
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
        SettingUtil.init(this);
        View footerView = getLayoutInflater().inflate(R.layout.timeline_footer, null);
        more = (Button) footerView.findViewById(R.id.more);
        more.setOnClickListener(this);
        itemDialog = new ItemDialog(this);
        
        twitter = new Twitter();
        timelineLoader = new ProcessLoader(this);
        statuses = new ArrayList<TimelineItem>();
        adapter = newInstanceTimelineAdapter();
        getListView().addFooterView(footerView);
        getListView().setOnItemLongClickListener(this);
        setListAdapter(adapter);
        
        attachUpdateInterval();
        processIntent(getIntent());
    }
	
	TimelineAdapter newInstanceTimelineAdapter() {
		return new TimelineAdapter(this, statuses);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		processIntent(intent);
	}
	
	void processIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			doSearch(query);
		}
	}
	
	void doSearch(String query) {
		Toast.makeText(getApplicationContext(), "Search: "+query, Toast.LENGTH_LONG).show();
	}
	
	private void attachUpdateInterval() {
		intervalUpdateTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				long now = System.currentTimeMillis();
				int interval = SettingUtil.getAutoInterval()*1000;
				if (interval>0 && now>lastUpdateTimeline+interval) {
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
		menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, R.string.settings);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case MENU_UPDATE_STATUS:
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(this, UpdateStatusActivity.class);
			startActivity(intent);
			break;
		case MENU_NEW_TIMELINE:
			loadTimeline(LoadMode.NEW);
			break;
		case MENU_REFRESH_TIMELINE:
			loadTimeline(LoadMode.REFRESH);
			break;
		case MENU_SETTINGS:
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(this, SettingsActivity.class);
			startActivity(intent);
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
		} else if (RS_CODE_RECONFIG==requestCode) {
			loadTimeline(LoadMode.REFRESH);
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
	private int incrementCount = 0; //FIXME フィールド変数に一時的に使用する変数を定義しているが、良い方法ではないので代替方法があれば、修正してください。
	
	private void loadTimeline(final LoadMode mode) {
		preLoadTimeline(mode);
		final List<TimelineItem> timeline = new ArrayList<TimelineItem>();
		Runnable successCallback = generateSuccessCallback(timeline, mode);
		processLoadTimeline(timeline, mode, successCallback);
	}
	
	Runnable generateSuccessCallback(final List<TimelineItem> timeline, final LoadMode mode) {
		Runnable callback = new Runnable() {
			@Override
			public void run() {
				// 現在表示されている最上のツイート位置
				int firstItemPosition = getListView().getFirstVisiblePosition();
				int firstItemTop = getListView().getChildAt(0)==null ? 0 : getListView().getChildAt(0).getTop();
				Log.d("TimelineActivity", "firstItemPosition="+firstItemPosition);
				Log.d("TimelineActivity", "firstItemTop="+firstItemTop);
				processUpdateTimeline(mode, timeline);
				// 表示されているツイートが最上部以外の場合
				if (mode == LoadMode.NEW && firstItemPosition > 0) {
					//新しい選択位置を設定する。
					getListView().setSelectionFromTop(firstItemPosition+incrementCount, firstItemTop);
				}
				lastUpdateTimeline = System.currentTimeMillis();
			};
		};
		return callback;
	}

	void processLoadTimeline(final List<TimelineItem> timeline, final LoadMode mode, Runnable successCallback) {
		timelineLoader.load(new BaseRequest(successCallback, null) {
			@Override
			public void processRequest(ProcessLoader loader) {
				incrementCount = 0;
				List<TimelineItem> list = null;
				if (LoadMode.REFRESH==mode) {
					list = getTimeline();
				} else if (LoadMode.NEW==mode) {
					list = getNewTimeline(latestStatuseId);
					incrementCount = list.size();
					System.err.println("incrementCount:" + list.size());
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
		return twitter.getMoreHomeTimeline(lastStatuseId);
	}

	List<TimelineItem> getNewTimeline(String latestStatuseId) {
		return twitter.getNewHomeTimeline(latestStatuseId);
	}

	List<TimelineItem> getTimeline() {
		return twitter.getHomeTimeline();
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
	}
	
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
