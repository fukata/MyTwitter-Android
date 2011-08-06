package org.fukata.android.mytw.database;

import org.fukata.android.mytw.database.schema.TweetSchema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyTwitterDB extends SQLiteOpenHelper {
	final static String TAG = MyTwitterDB.class.getSimpleName();
	
	final static String DB_NAME = "mytw.db";
	final static int DB_VERSION = 2;

	static MyTwitterDB db;
	
	public static MyTwitterDB getInstance(Context context) {
		if ( db == null ) {
			db = new MyTwitterDB(context);
		}
		return db;
	}
	
	private MyTwitterDB(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.beginTransaction();

			// tweets
			Log.i(TAG, "create tweets");
			db.execSQL(
					"CREATE TABLE " + TweetSchema.TABLE + " ( " + 
					TweetSchema.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					TweetSchema.STATUS_ID + " TEXT, " + 
					TweetSchema.STATUS + " TEXT, " + 
					TweetSchema.USERNAME + " TEXT, " + 
					TweetSchema.USER_ID + " TEXT, " + 
					TweetSchema.SOURCE + " TEXT, " + 
					TweetSchema.IN_REPLY_TO_STATUS_ID + " TEXT, " + 
					TweetSchema.CREATED_AT + " INTEGER, " +
					TweetSchema.TWEET_TYPE + " INTEGER, " +
					TweetSchema.CUSTOM + " TEXT " +
					");"
			);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(getClass().getSimpleName(), "Schema Update old:" + oldVersion + " new:" + newVersion);
		if( oldVersion == 1 && newVersion == 2 ) {
            db.execSQL("ALTER TABLE " + TweetSchema.TABLE + " ADD COLUMN " 
                + TweetSchema.IN_REPLY_TO_STATUS_ID + " TEXT"
                + ";" );
		}
	}

}
