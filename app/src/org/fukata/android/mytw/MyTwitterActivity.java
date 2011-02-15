package org.fukata.android.mytw;

import org.fukata.android.exandroid.loader.process.ProcessLoader;
import org.fukata.android.mytw.twitter.Twitter;
import org.fukata.android.mytw.util.SettingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ViewFlipper;

public class MyTwitterActivity extends Activity {
	static int HORIZONTAL_FLING_THRESHOLD = 50;
	GestureDetector gestureDetector;
	ViewFlipper flipper;
	ProcessLoader timelineLoader;
	Twitter twitter;

	static final int MENU_UPDATE_STATUS = Menu.FIRST + 1;
	static final int MENU_NEW_TIMELINE = Menu.FIRST + 2;
	static final int MENU_REFRESH_TIMELINE = Menu.FIRST + 3;
	static final int MENU_SETTINGS = Menu.FIRST + 4;
	
	protected static final int RS_CODE_UPDATE_STATUS = 1;
	public static final int INTENT_EXTRA_SELECTION_HEAD = 1;
	public static final int INTENT_EXTRA_SELECTION_END = 2;
	public static final String INTENT_EXTRA_SELECTION = "selection";
	
	enum LoadMode {
		REFRESH, NEW, MORE;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeine_flipper);
		SettingUtil.init(this);
		
		flipper = (ViewFlipper) findViewById(R.id.timeline_flipper);
		gestureDetector = new TimelineGestureDetector(this);
		timelineLoader = new ProcessLoader(this);
		
		twitter = new Twitter();
		
		init();
		handleIntent(getIntent());
	}

	void init() {
		flipper.addView(new HomeTimelineView(this, this));
		flipper.addView(new MentionTimelineView(this, this));
		flipper.addView(new DirectMessageTimelineView(this, this));
		flipper.getCurrentView().requestFocus();
		updateTitle();
	}

	void handleIntent(Intent intent) {
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		timelineLoader.startBackgroundThread();
		for (int i=0; i<flipper.getChildCount(); i++) {
			TimelineView v = (TimelineView) flipper.getChildAt(i);
			v.doResume();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (timelineLoader != null && !SettingUtil.isBackgroundProcessEnabled()) {
			timelineLoader.stopBackgroundThread();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	class TimelineGestureDetector extends GestureDetector {

		public TimelineGestureDetector(Context context) {
			super(context, new GestureDetector.SimpleOnGestureListener(){
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		            int distanceX = Math.abs((int) e2.getX() - (int) e1.getX());
		            int distanceY = Math.abs((int) e2.getY() - (int) e1.getY());
		            if (distanceX < HORIZONTAL_FLING_THRESHOLD || distanceX < distanceY) {
		                return false;
		            }
		            
		            if (velocityX > 0) {
		            	Log.d(this.getClass().getSimpleName(), "flipper.showNext()");
		            	flipper.showNext();
		            } else {
		            	Log.d(this.getClass().getSimpleName(), "flipper.showPrevious()");
		            	flipper.showPrevious();
		            }
		            
		            updateTitle();

					return true;
				}
			});
		}

	}
	
	void updateTitle() {
		TimelineView currentView = (TimelineView) flipper.getCurrentView();
		setTitle(currentView.getTitle());
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
		TimelineView v;
		switch (item.getItemId()) {
		case MENU_UPDATE_STATUS:
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(this, UpdateStatusActivity.class);
			startActivity(intent);
			break;
		case MENU_NEW_TIMELINE:
			v = (TimelineView) flipper.getCurrentView();
			v.loadTimeline(LoadMode.NEW);
			break;
		case MENU_REFRESH_TIMELINE:
			v = (TimelineView) flipper.getCurrentView();
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
		TimelineView v = (TimelineView) flipper.getCurrentView();
		if (RS_CODE_UPDATE_STATUS==requestCode) {
			v.loadTimeline(LoadMode.NEW);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}