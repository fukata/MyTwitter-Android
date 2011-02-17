package org.fukata.android.mytw;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.fukata.android.exandroid.loader.process.ProcessLoader;
import org.fukata.android.mytw.twitter.Twitter;
import org.fukata.android.mytw.util.SettingUtil;

import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

public class TimelineActivity extends TabActivity implements TabHost.TabContentFactory { 
	static final int TAB_HEIGHT = 45;
	static final int TAB_HOME = 0;
	static final int TAB_MENTIONS = 1;
	static final int TAB_DM = 2;
	static final String INTENT_EXTRA_SELECT_TAB = "select_tab";
	
	static final int MENU_UPDATE_STATUS = Menu.FIRST + 1;
	static final int MENU_NEW_TIMELINE = Menu.FIRST + 2;
	static final int MENU_REFRESH_TIMELINE = Menu.FIRST + 3;
	static final int MENU_SETTINGS = Menu.FIRST + 4;
	
	ProcessLoader timelineLoader;
	Twitter twitter;
	
	Map<String, TimelineView> timelineMap;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SettingUtil.init(this);
		timelineLoader = new ProcessLoader(this);
		twitter = new Twitter();
		
		// init timeline
		timelineMap = new HashMap<String, TimelineView>();
		timelineMap.put("Home", new HomeTimelineView(this, this));
		timelineMap.put("Mentions", new MentionTimelineView(this, this));
		timelineMap.put("DM", new DirectMessageTimelineView(this, this));
		Set<String> keySet = timelineMap.keySet();
		for (String key : keySet) {
			timelineMap.get(key).doResume();
		}
		
		// init tab
		TabHost host = getTabHost();
		host.addTab(host.newTabSpec("Home")
				.setIndicator("Home")
				.setContent(this));
		
		host.addTab(host.newTabSpec("Mentions")
				.setIndicator("Mentions")
				.setContent(this));
		
		host.addTab(host.newTabSpec("DM")
				.setIndicator("DM")
				.setContent(this));

		// init tab layout
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, TAB_HEIGHT);
		layout.weight = 1;
		for (int i=0; i<getTabWidget().getTabCount(); i++) {
			View childAt = getTabWidget().getChildAt(i);
			childAt.setLayoutParams(layout);
		}
		
		host.setCurrentTab(0);
		handleIntent(getIntent());
    }
	
	private void handleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras==null) {
			return;
		}
		
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			doSearch(query);
		}
		
		int tab = extras.getInt(INTENT_EXTRA_SELECT_TAB, 0);
		if (tab<getTabWidget().getTabCount()) {
			getTabHost().setCurrentTab(tab);
		}
		
	}
	
	void doSearch(String query) {
		Toast.makeText(getApplicationContext(), "Search(Unimplemented): "+query, Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		timelineLoader.startBackgroundThread();
		TimelineView currentView = (TimelineView) getTabHost().getCurrentView();
		currentView.doResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (timelineLoader != null && !SettingUtil.isBackgroundProcessEnabled()) {
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
		TimelineView v = (TimelineView) getTabHost().getCurrentView();
		switch (item.getItemId()) {
		case MENU_UPDATE_STATUS:
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(this, UpdateStatusActivity.class);
			startActivity(intent);
			break;
		case MENU_NEW_TIMELINE:
			v.loadTimeline(LoadMode.NEW);
			break;
		case MENU_REFRESH_TIMELINE:
			v.loadTimeline(LoadMode.REFRESH);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		TimelineView v = (TimelineView) getTabHost().getCurrentView();
		if (MyTwitterApp.RS_CODE_UPDATE_STATUS==requestCode) {
			v.loadTimeline(LoadMode.NEW);
		} else if (MyTwitterApp.RS_CODE_RECONFIG==requestCode) {
			v.loadTimeline(LoadMode.REFRESH);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	enum LoadMode {
		REFRESH, NEW, MORE;
	}

	@Override
	public View createTabContent(String tag) {
		TimelineView v = timelineMap.get(tag);
		v.doResume();
		return v;
	}

}
