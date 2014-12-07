package com.hitchlab.tinkle.datasource;

import java.util.ArrayList;

import com.hitchlab.tinkle.calendar.EventCalendarItem;
import com.hitchlab.tinkle.objects.FbEvent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class CalendarEventDataSource {
	private SQLTablesHelper tableHelper;

	String[] columns = {SQLTablesHelper.CALENDAR_EVENT_CALENDAR_ID, 
			SQLTablesHelper.CALENDAR_EVENT_FB_ID, 
			SQLTablesHelper.CALENDAR_EVENT_LAST_MODIFIED,
			SQLTablesHelper.CALENDAR_EVENT_START_TIME};

	public CalendarEventDataSource(Context context) {
		this.tableHelper = new SQLTablesHelper(context);
	}


	/**
	 * get the content value to be update/insert to the db
	 * @param eventItem
	 * @return
	 */
	private ContentValues prepareContentValues(EventCalendarItem eventItem) {
		ContentValues contents = new ContentValues();
		contents.put(SQLTablesHelper.CALENDAR_EVENT_CALENDAR_ID, eventItem.getCalendarEid());
		contents.put(SQLTablesHelper.CALENDAR_EVENT_FB_ID, eventItem.getEid());
		contents.put(SQLTablesHelper.CALENDAR_EVENT_LAST_MODIFIED, eventItem.getLastModified());
		contents.put(SQLTablesHelper.CALENDAR_EVENT_START_TIME, eventItem.getStartTime());
		return contents;
	}

	/**
	 * add an event to the DB
	 * @param EventTableItem
	 */
	public void addEvent(EventCalendarItem event) {
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			ContentValues contents = prepareContentValues(event);
			db.insert(SQLTablesHelper.CALENDAR_EVENT_TABLE_NAME, null, contents);
		} finally {
			db.close();
		}
	}

	/**
	 * add a list of event to the DB
	 * @param EventTableItem
	 */
	public void addEvents(ArrayList<EventCalendarItem> events) {
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			for (EventCalendarItem event : events) {
				ContentValues contents = prepareContentValues(event);
				db.insert(SQLTablesHelper.CALENDAR_EVENT_TABLE_NAME, null, contents);
			}
		} finally {
			db.close();
		}
	}

	/**
	 * get the event item stored in the database
	 * @return ArrayList<EventTableItem>
	 */
	public ArrayList<EventCalendarItem> getEventItems() {
		ArrayList<EventCalendarItem> eventItems = new ArrayList<EventCalendarItem>();
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.CALENDAR_EVENT_TABLE_NAME, columns, 
					null, null, null, null, null);
			if (cursor!= null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					EventCalendarItem eventItem = cursorToEvent(cursor);
					eventItems.add(eventItem);
					cursor.moveToNext();
				}
			}
			cursor.close();
		} finally {
			db.close();
		}
		return eventItems;
	}

	/**
	 * get the future event items stored in the database
	 * @return ArrayList<EventTableItem>
	 */
	public ArrayList<EventCalendarItem> getFutureEventItems() {
		ArrayList<EventCalendarItem> eventItems = new ArrayList<EventCalendarItem>();
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.CALENDAR_EVENT_TABLE_NAME, columns, 
					SQLTablesHelper.CALENDAR_EVENT_START_TIME + " > ?", 
					new String[] {String.valueOf(System.currentTimeMillis())}, null, null, null);
			if (cursor!= null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					EventCalendarItem eventItem = cursorToEvent(cursor);
					eventItems.add(eventItem);
					cursor.moveToNext();
				}
			}
			cursor.close();
		} finally {
			db.close();
		}
		return eventItems;
	}

	/**
	 * get the event item with a given FbId
	 * @param FbId
	 * @return EventTableItem
	 */
	public EventCalendarItem getEventItem(String fbId) {
		EventCalendarItem eventItem = null;
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.CALENDAR_EVENT_TABLE_NAME, columns, 
					SQLTablesHelper.CALENDAR_EVENT_FB_ID + " = ?", 
					new String[] {fbId}, 
					null, null, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) eventItem = cursorToEvent(cursor);
			cursor.close();
		} finally {
			db.close();
		}
		return eventItem;
	}

	/**
	 * Remove a EventTableItem with a given fbId from the db
	 * @param fbId of the EventTableItem
	 */
	public void removeEventWithFbId(String fbId) {
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			db.delete(SQLTablesHelper.CALENDAR_EVENT_TABLE_NAME, SQLTablesHelper.CALENDAR_EVENT_FB_ID + "=?", new String[] {fbId});
		}	finally {
			db.close();
		}

	}
	
	/**
	 * Remove all events
	 */
	public void removeEvents() {
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			db.delete(SQLTablesHelper.CALENDAR_EVENT_TABLE_NAME, null, null);
		}	finally {
			db.close();
		}

	}

	/**
	 * Update a EvenTableItem with a given fbId and updateTime
	 * @param EventTableItem
	 */
	public void updateEvent(EventCalendarItem eventItem) {
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			ContentValues contents = prepareContentValues(eventItem);
			db.update(SQLTablesHelper.CALENDAR_EVENT_TABLE_NAME, contents, SQLTablesHelper.CALENDAR_EVENT_FB_ID + "=?", new String[] {eventItem.getEid()});
		}	finally {
			db.close();
		}
	}
	
	/**
	 * Update the item in the db with a given FbEvent
	 * @param FbEvent
	 */
	public void updateEvent(FbEvent event) {
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			ContentValues contents = new ContentValues();
			contents.put(SQLTablesHelper.CALENDAR_EVENT_LAST_MODIFIED, event.getUpdated_time());
			db.update(SQLTablesHelper.CALENDAR_EVENT_TABLE_NAME, contents, SQLTablesHelper.CALENDAR_EVENT_FB_ID + "=?", new String[] {event.getId()});
		}	finally {
			db.close();
		}
	}

	/**
	 * Transform the result from the cursor to the event table item
	 * @param Cursor
	 * @return PinMarkerObj
	 */
	private EventCalendarItem cursorToEvent(Cursor cursor) {
		EventCalendarItem item = new EventCalendarItem();
		item.setCalendarEid(cursor.getLong(0));
		item.setEid(cursor.getString(1));
		item.setLastModified(cursor.getLong(2));
		item.setStartTime(cursor.getLong(3));
		return item;
	}

}
