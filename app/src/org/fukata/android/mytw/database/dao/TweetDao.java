package org.fukata.android.mytw.database.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fukata.android.mytw.database.dto.TweetDto;
import org.fukata.android.mytw.database.schema.TweetSchema;
import org.fukata.android.mytw.database.schema.TweetSchema.TweetType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TweetDao extends BaseDao {

	public TweetDao(Context context) {
		super(context);
	}

	public List<TweetDto> findByType(TweetSchema.TweetType tweetType, int limit) {
		SQLiteDatabase db = con.getReadableDatabase();
		String sql = "SELECT * FROM " + TweetSchema.TABLE + " WHERE tweet_type = ? ORDER BY created_at DESC LIMIT ?";
		String[] selectionArgs = new String[]{ String.valueOf(tweetType.getType()), String.valueOf(limit) };
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		
		List<TweetDto> list = new ArrayList<TweetDto>();
		while (cursor.moveToNext()) {
			TweetDto dto = generateDto(cursor);
			list.add(dto);
		}
		
		cursor.close();
		return list;
	}
	
	public void addTweets(List<TweetDto> tweets, TweetSchema.TweetType tweetType) {
		
		SQLiteDatabase db = con.getWritableDatabase();
		try {
			db.beginTransaction();
			
			for (TweetDto tweet : tweets) {
				ContentValues values = new ContentValues();
				values.put(TweetSchema.STATUS_ID, tweet.statusId);
				values.put(TweetSchema.STATUS, tweet.status);
				values.put(TweetSchema.USERNAME, tweet.username);
				values.put(TweetSchema.USER_ID, tweet.userId);
				values.put(TweetSchema.SOURCE, tweet.source);
				values.put(TweetSchema.CREATED_AT, tweet.createdAt.getTime());
				values.put(TweetSchema.TWEET_TYPE, tweet.tweetType.getType());
				values.put(TweetSchema.CUSTOM, tweet.custom);
				db.insertOrThrow(TweetSchema.TABLE, null, values);
			}
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public void refreshTweets(List<TweetDto> tweets, TweetSchema.TweetType tweetType) {
		
		SQLiteDatabase db = con.getWritableDatabase();
		try {
			db.beginTransaction();
			
			// delete all tweet
			db.delete(TweetSchema.TABLE, "tweet_type = ?", new String[]{ String.valueOf(tweetType.getType()) });
			
			for (TweetDto tweet : tweets) {
				ContentValues values = new ContentValues();
				values.put(TweetSchema.STATUS_ID, tweet.statusId);
				values.put(TweetSchema.STATUS, tweet.status);
				values.put(TweetSchema.USERNAME, tweet.username);
				values.put(TweetSchema.USER_ID, tweet.userId);
				values.put(TweetSchema.SOURCE, tweet.source);
				values.put(TweetSchema.CREATED_AT, tweet.createdAt.getTime());
				values.put(TweetSchema.TWEET_TYPE, tweet.tweetType.getType());
				values.put(TweetSchema.CUSTOM, tweet.custom);
				db.insertOrThrow(TweetSchema.TABLE, null, values);
			}
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		
	}

	private TweetDto generateDto(Cursor cursor) {
		TweetDto dto = new TweetDto();
		
		dto.id = cursor.getInt( cursor.getColumnIndex(TweetSchema.ID) );
		dto.statusId = cursor.getString( cursor.getColumnIndex(TweetSchema.STATUS_ID) );
		dto.status = cursor.getString( cursor.getColumnIndex(TweetSchema.STATUS) );
		dto.username = cursor.getString( cursor.getColumnIndex(TweetSchema.USERNAME) );
		dto.userId = cursor.getString( cursor.getColumnIndex(TweetSchema.USER_ID) );
		dto.source = cursor.getString( cursor.getColumnIndex(TweetSchema.SOURCE) );
		long createdAt = cursor.getLong( cursor.getColumnIndex(TweetSchema.CREATED_AT) );
		dto.createdAt = createdAt == 0 ? null : new Date(createdAt);
		dto.tweetType = TweetType.find( cursor.getInt( cursor.getColumnIndex(TweetSchema.TWEET_TYPE) ) );
		dto.custom = cursor.getString( cursor.getColumnIndex(TweetSchema.CUSTOM) );
		
		return dto;
	}
}
