package org.fukata.android.mytw.twitter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.fukata.android.mytw.TimelineItem;
import org.fukata.android.mytw.twitter.rs.Status;
import org.fukata.android.mytw.twitter.rs.User;

import com.thoughtworks.xstream.XStream;


public class Twitter {
	static final String CHARSET = "UTF-8";
	static final String API_PREFIX = "http://example.com";
	XStream xStream;
	
	public Twitter() {
		xStream = new XStream();
	}
	
	public List<TimelineItem> getHomeTimeline() {
		return loadHomeTimeline(null);
	}
	
	public List<TimelineItem> getMoreHomeTimeline(String maxId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("max_id", maxId));
		return loadHomeTimeline(params);
	}
	
	public List<TimelineItem> getNewHomeTimeline(String sinceId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("since_id", sinceId));
		return loadHomeTimeline(params);
	}
	
	private List<TimelineItem> loadHomeTimeline(List<NameValuePair> parameters) {
		HttpParams params = new BasicHttpParams();
		List<TimelineItem> list = new ArrayList<TimelineItem>();
		DefaultHttpClient client = new DefaultHttpClient(params);
		StringBuilder url = new StringBuilder(API_PREFIX+"/statuses/home_timeline/");
		if (parameters!=null && parameters.size()>0) {
			URLEncodedUtils.format(parameters, CHARSET);
			for (int i=0; i<parameters.size(); i++) {
				NameValuePair param = parameters.get(i);
				if (i==0) {
					url.append("?");
				} else {
					url.append("&");
				}
				url.append(param.getName()+"="+param.getValue());
			}
		}
		HttpGet method = new HttpGet(url.toString());
		String xml = null;
		try {
			HttpResponse response = client.execute(method);
			xml = EntityUtils.toString(response.getEntity(), CHARSET);
			xStream.alias("xml", ArrayList.class);
			xStream.alias("item", Status.class);
			xStream.alias("user", User.class);
			@SuppressWarnings("unchecked")
			List<Status> statuses = (List<Status>) xStream.fromXML(xml);
			for (Status status : statuses) {
				TimelineItem item = new TimelineItem();
				item.setStatusId(status.getId());
				item.setStatus(status.getText());
				item.setUsername(status.getUser().getScreenname());
				item.setUserId(status.getUser().getId());
				item.setSource(status.getSource());
				item.setCreatedAt( new Date(status.getCreatedat()) );
				list.add(item);
			}
		} catch (Exception e) {
		}
		
		return list;
	} 

	public boolean updateStatus(String status) {
		HttpParams params = new BasicHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(params);
		HttpPost method = new HttpPost(API_PREFIX+"/statuses/update/");
		String xml = null;
		try {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("status", status));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, CHARSET);
			method.setEntity(entity);
			HttpResponse response = client.execute(method);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean postReTweet(String statusId) {
		HttpParams params = new BasicHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(params);
		HttpPost method = new HttpPost(API_PREFIX+"/statuses/retweet/");
		String xml = null;
		try {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("id", statusId));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, CHARSET);
			method.setEntity(entity);
			HttpResponse response = client.execute(method);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
