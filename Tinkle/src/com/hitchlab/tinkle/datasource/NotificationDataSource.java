package com.hitchlab.tinkle.datasource;

import java.util.ArrayList;

import com.hitchlab.tinkle.objects.MyNotification;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class NotificationDataSource {

	private Context context;

	private SQLTablesHelper tableHelper;

	private String[] columns = {SQLTablesHelper.NOTIFICATION_UID,
			SQLTablesHelper.NOTIFICATION_TYPE,
			SQLTablesHelper.NOTIFICATION_MESSAGE,
			SQLTablesHelper.NOTIFICATION_MESSAGE_EXTRA1,
			SQLTablesHelper.NOTIFICATION_MESSAGE_EXTRA2,
			SQLTablesHelper.NOTIFICATION_EXTRA_INFO,
			SQLTablesHelper.NOTIFICATION_VIEWED,
			SQLTablesHelper.NOTIFICATION_TIME};

	public NotificationDataSource(Context context) {
		this.context = context;
		this.tableHelper = new SQLTablesHelper(context);
	}

	/**
	 * Prepare the content values of an Notification
	 * @param MyNotification
	 */
	private ContentValues prepareContentValues(MyNotification notification) {
		ContentValues contents = new ContentValues();
		contents.put(SQLTablesHelper.NOTIFICATION_UID, notification.getUid());
		contents.put(SQLTablesHelper.NOTIFICATION_TYPE, notification.getType());
		contents.put(SQLTablesHelper.NOTIFICATION_MESSAGE, notification.getMessage());
		contents.put(SQLTablesHelper.NOTIFICATION_MESSAGE_EXTRA1, notification.getMessageExtra1());
		contents.put(SQLTablesHelper.NOTIFICATION_MESSAGE_EXTRA2, notification.getMessageExtra2());
		contents.put(SQLTablesHelper.NOTIFICATION_EXTRA_INFO, notification.getExtraInfo());
		if (notification.isViewed()) contents.put(SQLTablesHelper.NOTIFICATION_VIEWED, 1);
		else contents.put(SQLTablesHelper.NOTIFICATION_VIEWED, 0);
		contents.put(SQLTablesHelper.NOTIFICATION_TIME, notification.getTime());
		return contents;
	}

	/**
	 * Add a notification to the db
	 * @param MyNotification
	 */
	public void addNotification(MyNotification notification) throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			ContentValues contents = prepareContentValues(notification);
			db.insert(SQLTablesHelper.NOTIFICATION_TABLE_NAME, null, contents);
		} finally {
			db.close();
		}
	}

	/**
	 * Add a list of notifications to the db
	 * @param ArrayList<Notification>
	 */
	public void addNotifications(ArrayList<MyNotification> notifications) throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			for (MyNotification notification : notifications) {
				ContentValues contents = prepareContentValues(notification);
				db.insert(SQLTablesHelper.NOTIFICATION_TABLE_NAME, null, contents);
			}
		} finally {
			db.close();
		}
	}

	/**
	 * Get the list of all the notification from the database sorted by the time des
	 * @return ArrayList<Notification>
	 */
	public ArrayList<MyNotification> getNotifications() throws SQLException {
		ArrayList<MyNotification> notifications = new ArrayList<MyNotification>();
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.NOTIFICATION_TABLE_NAME, columns, 
					null, null, null, null, SQLTablesHelper.NOTIFICATION_TIME + " DESC");
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				MyNotification notification = cursorToNotification(cursor);
				notifications.add(notification);
				cursor.moveToNext();
			}
			cursor.close();
		} finally {
			db.close();
		}
		return notifications;
	}

	/**
	 * Get the number of not yet viewed notifications since the last logged in time
	 * @return integer
	 */
	public int getNumNotViewed(int type) throws SQLException {
		int count = 0;
		long lastViewedTime = SharedPreference.getPrefLongValue(context, Preference.NOTIFICATION_LAST_VIEWED_TIME);
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.NOTIFICATION_TABLE_NAME, columns, 
					SQLTablesHelper.NOTIFICATION_VIEWED + " = 0 AND " + 
							SQLTablesHelper.NOTIFICATION_TYPE + " = ? AND " +
							SQLTablesHelper.NOTIFICATION_TIME + " >= ? ", 
							new String[]{String.valueOf(type), String.valueOf(lastViewedTime)}, 
							null, null, null);
			count = cursor.getCount();
			cursor.close();
		} finally {
			db.close();
		}
		return count;
	}

	/**
	 * Remove all the notifications in the db
	 * @throws SQLException
	 */
	public void removeNotifications() throws SQLException {
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		try {
			db.delete(SQLTablesHelper.NOTIFICATION_TABLE_NAME, null, null);
		} finally {
			db.close();
		}
	}

	/**
	 * Update the notification saying that it is viewed
	 * @param notification
	 */
	public void updateViewed(MyNotification notification) {
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		ContentValues contents = new ContentValues();
		contents.put(SQLTablesHelper.NOTIFICATION_VIEWED, 1);
		try {
			db.update(SQLTablesHelper.NOTIFICATION_TABLE_NAME, contents, 
					SQLTablesHelper.NOTIFICATION_MESSAGE + " =? AND " + SQLTablesHelper.NOTIFICATION_TIME + " =? ", 
					new String[] {notification.getMessage(), String.valueOf(notification.getTime())});
		} finally {
			db.close();
		}
	}

	/**
	 * Update the to show that the new notifications has been presented. Ie, update the list so
	 * that all non-clicked events become clicked
	 */
	public void updateClicked() {
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		ContentValues contents = new ContentValues();
		contents.put(SQLTablesHelper.NOTIFICATION_CLICKED, 1);
		try {
			db.update(SQLTablesHelper.NOTIFICATION_TABLE_NAME, contents, 
					SQLTablesHelper.NOTIFICATION_CLICKED + " = 0 ", null);
		} finally {
			db.close();
		}
	}

	/**
	 * Transform the cursor result to Notification
	 * @param cursor
	 * @return notification
	 */
	private MyNotification cursorToNotification(Cursor cursor) {
		MyNotification notification = new MyNotification();
		notification.setUid(cursor.getString(0));
		notification.setType(cursor.getInt(1));
		notification.setMessage(cursor.getString(2));
		notification.setMessageExtra1(cursor.getString(3));
		notification.setMessageExtra2(cursor.getString(4));
		notification.setExtraInfo(cursor.getString(5));
		notification.setViewed(cursor.getInt(6) == 1);
		notification.setTime(cursor.getLong(7));
		return notification;
	}

}
