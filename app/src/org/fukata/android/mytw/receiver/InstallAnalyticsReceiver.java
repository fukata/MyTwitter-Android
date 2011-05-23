package org.fukata.android.mytw.receiver;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.fukata.android.exandroid.util.StringUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InstallAnalyticsReceiver extends BroadcastReceiver {
	static String TAG = InstallAnalyticsReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		// get referrer
		String referrer = URLDecoder.decode( intent.getStringExtra("referrer") );
		Log.d(TAG, String.format("referrer: %s", referrer));
		if (StringUtil.isBlank(referrer)) {
			return;
		}
		
		// exact rid
//		String[] splited = referrer.split("?");
//		if (splited.length!=2) {
//			return;
//		}
		String[] queries = referrer.split("&");
		String rid = null;
		for (String query : queries) {
			String[] q = query.split("=");
			if (q.length==2 && q[0].equals("rid")) {
				rid = q[1];
				break;
			}
		}
		
		if (StringUtil.isBlank(rid)) {
			return;
		}
		
		// put analytics
		HttpParams params = new BasicHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(params);
		HttpPut method = new HttpPut(String.format("http://fukata.org:3000/%s", rid));
		try {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("log", referrer));				
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");
			method.setEntity(entity);
			HttpResponse response = client.execute(method);
		} catch (Exception e) {
		}
	}

}
