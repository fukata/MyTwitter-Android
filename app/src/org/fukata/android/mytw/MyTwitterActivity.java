package org.fukata.android.mytw;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TabHost;

public class MyTwitterActivity extends TabActivity {
	static final int TAB_HEIGHT = 35;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// タブの設定
		TabHost host = getTabHost();
		
		host.addTab(host.newTabSpec("Home")
				.setIndicator("Home")
				.setContent(new Intent(this, HomeTimelineActivity.class)));
		
		host.addTab(host.newTabSpec("Mentions")
				.setIndicator("Mentions")
				.setContent(new Intent(this, MentionTimlineActivity.class)));
		
		host.addTab(host.newTabSpec("DM")
				.setIndicator("DM")
				.setContent(new Intent(this, DirectMessageTimelineActivity.class)));
		
		host.setCurrentTab(0);

		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, TAB_HEIGHT);
		layout.weight = 1;
		for (int i=0; i<getTabWidget().getTabCount(); i++) {
			View childAt = getTabWidget().getChildAt(i);
			childAt.setLayoutParams(layout);
		}
	}
}