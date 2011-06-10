package org.fukata.android.mytw.database.dao;

import org.fukata.android.mytw.database.MyTwitterDB;

import android.content.Context;

public abstract class BaseDao {
	protected String TAG;
	protected static MyTwitterDB con;
	public BaseDao(Context context) {
		TAG = this.getClass().getSimpleName();
		if (con == null) {
			con = MyTwitterDB.getInstance(context);
		}
	}
}
