package org.fukata.android.mytw;

import org.fukata.android.mytw.twitter.Twitter;
import org.fukata.android.mytw.twitter.rs.User;
import org.fukata.android.mytw.util.SettingUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends Activity implements OnClickListener {
	EditText apiServerUrl;
	Spinner fontSizes;
	Spinner autoIntervals;
	Spinner timelineCounts;
	CheckBox backgroundProcess;
	CheckBox notification;
	TextView accountName;
	TextView accountId;
	Button updateUser;

	Twitter twitter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		SettingUtil.init(this);
		twitter = new Twitter();
		
		accountName = (TextView) findViewById(R.id.account_name);
		accountId = (TextView) findViewById(R.id.account_id);
		updateUser = (Button) findViewById(R.id.update_user);
		apiServerUrl = (EditText) findViewById(R.id.api_server_url);
		fontSizes = (Spinner) findViewById(R.id.font_size);
		autoIntervals = (Spinner) findViewById(R.id.auto_interval);
		timelineCounts = (Spinner) findViewById(R.id.timeline_count);
		backgroundProcess = (CheckBox) findViewById(R.id.background_process);
		notification = (CheckBox) findViewById(R.id.notification);

		backgroundProcess.setOnClickListener(this);
		updateUser.setOnClickListener(this);
		
		initFields();
	}
	
	private void initFields() {
		accountName.setText(SettingUtil.getAccountName());
		accountId.setText(SettingUtil.getAccountId());
		apiServerUrl.setText(SettingUtil.getApiServerUrl());
		fontSizes.setSelection(SettingUtil.getFontSizeIndex());
		autoIntervals.setSelection(SettingUtil.getAutoIntervalIndex());
		timelineCounts.setSelection(SettingUtil.getTimelineCountIndex());
		backgroundProcess.setChecked(SettingUtil.isBackgroundProcessEnabled());
		notification.setChecked(SettingUtil.isNotificationEnabled());
		
		updateFields();
	}
	
	private void updateFields() {
		boolean checked = backgroundProcess.isChecked();
		notification.setEnabled(checked);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// api server
		SettingUtil.setApiServerUrl(apiServerUrl.getText().toString());
		// font size
		int size = fontSizes.getSelectedItemPosition();
		SettingUtil.setFontSize(size);
		// auto interval
		int interval = autoIntervals.getSelectedItemPosition();
		SettingUtil.setAutoInterval(interval);
		// timeline count
		int timelineCount = timelineCounts.getSelectedItemPosition();
		SettingUtil.setTimelineCount(timelineCount);
		// background process
		SettingUtil.setBackgroundProcess(backgroundProcess.isChecked());
		// notification
		SettingUtil.setNotification(notification.isChecked());
	}

	@Override
	public void onClick(View v) {
		if (v==backgroundProcess) {
			updateFields();
		} else if (v==updateUser) {
			updateFields();
			updateUser();
		}
	}

	private void updateUser() {
		User user = twitter.getUser();
		if (user == null) {
			return;
		}
		
		accountName.setText(user.getScreenname());
		accountId.setText(user.getId());
		SettingUtil.setAccountName(user.getScreenname());
		SettingUtil.setAccountId(user.getId());
	}
}
