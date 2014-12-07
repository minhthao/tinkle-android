package com.hitchlab.tinkle.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ViewDataSource {
	private SQLTablesHelper tableHelper;
	
	private String[] columns = {SQLTablesHelper.VIEW_EID};
	
	public ViewDataSource(Context context) {
		this.tableHelper = new SQLTablesHelper(context);
	}
	
	/**
	 * Add an event eid to the db to say that the event is already viewed
	 * @param eid
	 * @throws SQLException
	 */
	public void addEventViewed(String eid) throws SQLException {
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.VIEW_TABLE_NAME, columns,
					SQLTablesHelper.VIEW_EID + " =?",
					new String[] {eid}, 
					null, null, null);
			cursor.moveToFirst();
			if (cursor.isAfterLast()) {
				ContentValues contents = new ContentValues();
				contents.put(SQLTablesHelper.VIEW_EID, eid);
				db.insert(SQLTablesHelper.VIEW_TABLE_NAME, null, contents);
			}
			cursor.close();
		} finally {
			db.close();
		}
	}
	
	/**
	 * Return whether an event is already viewed
	 * @param eid
	 * @throws SQLException
	 */
	public boolean checkEventViewed(String eid) throws SQLException {
		boolean exist = false;
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.VIEW_TABLE_NAME, columns,
					SQLTablesHelper.VIEW_EID + " =?",
					new String[] {eid}, 
					null, null, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) exist = true;
			cursor.close();
		} finally {
			db.close();
		}
		return exist;
	}
	
	/**
	 * remove all the events
	 * @param uid
	 */
	public void removeEventsViewed() throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		try {
			db.delete(SQLTablesHelper.VIEW_TABLE_NAME, null, null);
		} finally {
			db.close();
		}
	}
}
