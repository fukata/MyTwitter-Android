package org.fukata.android.mytw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MyTwitterActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(this, TimelineActivity.class);
		startActivity(intent);
    }
}