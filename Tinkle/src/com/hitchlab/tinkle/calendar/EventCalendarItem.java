package com.hitchlab.tinkle.calendar;

public class EventCalendarItem {
	private long calendarEid;
	private String eid;
	private long lastModified;
	private long startTime;
	
	public EventCalendarItem() {
		
	}

	/**
	 * @return the calendarEid
	 */
	public long getCalendarEid() {
		return calendarEid;
	}

	/**
	 * @param calendarEid the calendarEid to set
	 */
	public void setCalendarEid(long calendarEid) {
		this.calendarEid = calendarEid;
	}

	/**
	 * @return the eid
	 */
	public String getEid() {
		return eid;
	}

	/**
	 * @param eid the eid to set
	 */
	public void setEid(String eid) {
		this.eid = eid;
	}

	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	
}
