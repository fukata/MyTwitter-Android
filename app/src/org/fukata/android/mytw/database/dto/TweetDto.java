package org.fukata.android.mytw.database.dto;

import java.util.Date;

import org.fukata.android.mytw.database.schema.TweetSchema;

public class TweetDto {
	public int id;
	public String statusId;
	public String status;
	public String username;
	public String userId;
	public String source;
	public String inReplyToStatusId;
	public Date createdAt;
	public TweetSchema.TweetType tweetType;
	public String custom;
	
	@Override
	public String toString() {
		return "TweetDto [id=" + id + ", statusId=" + statusId + ", status="
				+ status + ", username=" + username + ", userId=" + userId
				+ ", source=" + source + ", inReplyToStatusId=" + inReplyToStatusId
				+ ", createdAt=" + createdAt + ", tweetType=" + tweetType
				+ ", custom=" + custom + "]";
	}
}
