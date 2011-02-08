package org.fukata.android.mytw;

import org.fukata.android.mytw.util.SettingUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class SettingsActivity extends Activity implements OnClickListener {
	EditText apiServerUrl;
	Spinner fontSizes;
	Spinner autoIntervals;
	CheckBox backgroundProcess;
	CheckBox notification;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		SettingUtil.init(this);
		
		apiServerUrl = (EditText) findViewById(R.id.api_server_url);
		fontSizes = (Spinner) findViewById(R.id.font_size);
		autoIntervals = (Spinner) findViewById(R.id.auto_interval);
		backgroundProcess = (CheckBox) findViewById(R.id.background_process);
		notification = (CheckBox) findViewById(R.id.notification);

		backgroundProcess.setOnClickListener(this);
		
		initFields();
	}
	
	private void initFields() {
		apiServerUrl.setText(SettingUtil.getApiServerUrl());
		fontSizes.setSelection(SettingUtil.getFontSizeIndex());
		autoIntervals.setSelection(SettingUtil.getAutoIntervalIndex());
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
		// background process
		SettingUtil.setBackgroundProcess(backgroundProcess.isChecked());
		// notification
		SettingUtil.setNotification(notification.isChecked());
	}

	@Override
	public void onClick(View v) {
		if (v==backgroundProcess) {
			updateFields();
		}
	}
}
