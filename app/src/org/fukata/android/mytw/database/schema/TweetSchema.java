package org.fukata.android.mytw.database.schema;

public enum TweetSchema {
	;
	public static final String TABLE = "tweets";
	public static final String ID = "_id";
	public static final String STATUS_ID = "status_id";
	public static final String STATUS = "status";
	public static final String USERNAME = "username";
	public static final String USER_ID = "user_id";
	public static final String SOURCE = "source";
	public static final String IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";
	public static final String CREATED_AT = "created_at";
	public static final String TWEET_TYPE = "tweet_type";
	public static final String CUSTOM = "custom";
	
	public enum TweetType {
		HOME(0),
		MENTION(1),
		DM(2),
		CUSTOM(3)
		;
		private int type;
		
		private TweetType(int type) {
			this.type = type;
		}
		
		public int getType() {
			return this.type;
		}
		
		public static TweetType find(int type) {
			TweetType[] types = values();
			for (TweetType t : types) {
				if ( t.getType() == type ) {
					return t;
				}
			}
			return null;
		}
	}
}
