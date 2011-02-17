package org.fukata.android.mytw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MyTwitterActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, TimelineActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);
		finish();
	}
}