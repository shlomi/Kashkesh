package com.asc.kshksh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class FavoritesDbAdapter {

	// Database fields
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CONTACT = "contact";
	private static final String DATABASE_TABLE = "favorites";
	private Context context;
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public void FavotritesDbAdapter(Context context) {
		this.context = context;
	}

	public FavoritesDbAdapter open() throws SQLException {
		dbHelper = new MySQLiteHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public long createFavorite(String email) {
		ContentValues values = new ContentValues();
		values.put(KEY_CONTACT, email);
		return database.insert(DATABASE_TABLE, null, values);
	}
	
	public void createMultipleFavorites(String[] email) {
		ContentValues values = new ContentValues();
		for(int i=0; i<email.length; i++){
			values.put(KEY_CONTACT, email[i]);
			database.insert(DATABASE_TABLE, null, values);
		}
	}

	public Cursor fetchAllFavorites() {
		return database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_CONTACT }, null, null, null,
				null, null);
	}
	
	public Cursor fetchAllFavoriteNames() {
		return database.query(DATABASE_TABLE, new String[] { 
				KEY_CONTACT }, null, null, null,
				null, null);
	}
	
}