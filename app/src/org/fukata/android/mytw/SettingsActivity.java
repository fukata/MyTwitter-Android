package org.fukata.android.mytw;

import org.fukata.android.mytw.util.SettingUtil;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

public class SettingsActivity extends Activity {
	EditText apiServerUrl;
	Spinner fontSizes;
	Spinner autoIntervals;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		SettingUtil.init(this);
		
		apiServerUrl = (EditText) findViewById(R.id.api_server_url);
		fontSizes = (Spinner) findViewById(R.id.font_size);
		autoIntervals = (Spinner) findViewById(R.id.auto_interval);
		
		initFields();
	}
	
	private void initFields() {
		apiServerUrl.setText(SettingUtil.getApiServerUrl());
		fontSizes.setSelection(SettingUtil.getFontSizeIndex());
	}

	@Override
	protected void onPause() {
		super.onPause();
		// api server
		SettingUtil.setApiServerUrl(apiServerUrl.getText().toString());
		// font size
		int size = fontSizes.getSelectedItemPosition();
		SettingUtil.setFontSize(size);
	}
}
