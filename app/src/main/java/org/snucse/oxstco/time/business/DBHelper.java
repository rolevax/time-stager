package org.snucse.oxstco.time.business;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static String DATABASE_NAME = "time.db";
	private static int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		// CursorFactory设置为null,使用默认值
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS time"
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				" subject VARCHAR, type INTEGER," +
				" datetime VARCHAR)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("ALTER TABLE person ADD COLUMN other STRING"); 
	}

}
