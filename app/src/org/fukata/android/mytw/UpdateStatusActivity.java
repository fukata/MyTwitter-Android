package org.fukata.android.mytw;

import java.util.ArrayList;
import java.util.List;

import org.fukata.android.exandroid.loader.process.BaseRequest;
import org.fukata.android.exandroid.loader.process.ProcessLoader;
import org.fukata.android.exandroid.util.StringUtil;
import org.fukata.android.mytw.twitter.Twitter;
import org.fukata.android.mytw.util.SettingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateStatusActivity extends Activity implements OnClickListener {
	final static int STATUS_MAX = 140;
	
	final static int MENU_UPDATE_STATUS = Menu.FIRST + 1;
	
	ProcessLoader updateStatusLoader;
	Twitter twitter;
	EditText status;
	Button updateStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_status);
		SettingUtil.init(this);
		
		updateStatus = (Button) findViewById(R.id.update_status);
		updateStatus.setOnClickListener(this);
		
		updateStatusLoader = new ProcessLoader(this);
		final Activity ac = this;
		final Handler handler = new Handler();
		twitter = new Twitter(
			new Runnable() {
				@Override
				public void run() {
					handler.post(new Runnable() {
						@Override
						public void run() {
							ac.setTitle(ac.getTitle() + " --- loading...");
						}
					});
				}
			},
			new Runnable() {
				@Override
				public void run() {
					handler.post(new Runnable() {
						@Override
						public void run() {
							ac.setTitle(ac.getString(R.string.app_name));
						}
					});
				}
			}
		);
		status = (EditText) findViewById(R.id.status);
		status.addTextChangedListener(new StatusWatcher());
		
		handleIntent(getIntent());
		updateStatusCount();
	}
	
	void handleIntent(Intent intent) {
		if (intent!=null && intent.getExtras()!=null) {
			Bundle extras = intent.getExtras();
			List<String> datas = new ArrayList<String>();
			datas.add(extras.getString(android.content.Intent.EXTRA_SUBJECT));
			datas.add(extras.getString(android.content.Intent.EXTRA_TEXT));
//			send.putExtra(Browser.EXTRA_SHARE_FAVICON, favicon);
//	        send.putExtra(Browser.EXTRA_SHARE_SCREENSHOT, screenshot);
			StringBuilder sb = new StringBuilder();
			for (String str : datas) {
				if (StringUtil.isBlank(str)) {
					continue;
				}
				if (sb.length()>0) {
					sb.append(" ");
				}
				sb.append(str);
			}
			status.setText(sb);
			
			// カーソル位置を変更
			int selection = extras.getInt(MyTwitterApp.INTENT_EXTRA_SELECTION);
			// カーソルを先頭へ移動
			if (selection==MyTwitterApp.INTENT_EXTRA_SELECTION_HEAD) {
				status.setSelection(0, status.getText().length());
				status.setSelection(0);
			} else {
				status.setSelection(status.getText().length());
			}
		}
	}

	class StatusWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			updateStatusCount();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			updateStatusCount();
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			updateStatusCount();
		}
		
	}
	
	void updateStatusCount() {
		int length = status.getText().length();
		updateStatus.setText( getString(R.string.update_status_with_count, length, STATUS_MAX) );
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateStatusLoader.startBackgroundThread();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (updateStatusLoader != null) {
			updateStatusLoader.stopBackgroundThread();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_status:
			updateStatus();
			break;
		default:
			break;
		}
	}
	
	public void updateStatus() {
		final String st = status.getText().toString();
		if (StringUtil.isNotBlank(st)) {
			updateStatus.setEnabled(false);
			updateStatus.setText(R.string.updating);
			
			final List<Boolean> rs = new ArrayList<Boolean>();
			Runnable successCallback = new Runnable() {
				@Override
				public void run() {
					if (rs.size()>0 && rs.get(0)) {
						Toast.makeText(getApplicationContext(), getText(R.string.update_successful), Toast.LENGTH_LONG).show();
						finishActivity(MyTwitterApp.RS_CODE_UPDATE_STATUS);
						finish();
					} else {
						Toast.makeText(getApplicationContext(), getText(R.string.update_unsuccessful), Toast.LENGTH_LONG).show();
						updateStatusCount();
						updateStatus.setEnabled(true);
					}
				}
			};
			updateStatusLoader.load(new BaseRequest(successCallback, null) {
				@Override
				public void processRequest(ProcessLoader loader) {
					rs.add(twitter.updateStatus(st));
					super.processRequest(loader);
				}
			});
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_UPDATE_STATUS, Menu.NONE, R.string.update_status);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_UPDATE_STATUS:
			updateStatus();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
