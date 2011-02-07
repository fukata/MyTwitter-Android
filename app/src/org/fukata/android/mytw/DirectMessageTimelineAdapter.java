package org.fukata.android.mytw;

import java.util.List;

import org.fukata.android.mytw.util.PrettyDateUtil;
import org.fukata.android.mytw.util.SettingUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DirectMessageTimelineAdapter extends TimelineAdapter {
	public DirectMessageTimelineAdapter(Context context,	List<TimelineItem> items) {
		super(context, items);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view==null) {
			view = inflater.inflate(R.layout.direct_message_timeline_item, null);
		}
		
		TimelineItem item = getItem(position);
		// username
		TextView username = (TextView) view.findViewById(R.id.timeline_username);
		username.setText(item.getUsername());
		// created_at
		TextView createdAt = (TextView) view.findViewById(R.id.timeline_created_at);
		createdAt.setText( PrettyDateUtil.toString(item.getCreatedAt()) );
		// status
		TextView status = (TextView) view.findViewById(R.id.timeline_status);
		status.setText(decorateStatus(item.getStatus()));

		updateTextSize(view);
		
		return view;
	}

	@Override
	void updateTextSize(View view) {
		float size = SettingUtil.getFontSize();
		((TextView) view.findViewById(R.id.timeline_username)).setTextSize(size);
		((TextView) view.findViewById(R.id.timeline_created_at)).setTextSize(size);
		((TextView) view.findViewById(R.id.timeline_status)).setTextSize(size);
	}
}
