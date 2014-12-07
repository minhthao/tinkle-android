package com.hitchlab.tinkle.objects;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class FbEventCompleteInfo implements Parcelable {
	
	private FbEvent event;
	private Venue venue;
	private ArrayList<String> friendsAttending;
	private ArrayList<String> friendsMaybe;
	private ArrayList<String> friendsUnreplied;

	private int attendingCount;
	private int maybeCount;
	private int notRepliedCount;
	private boolean inviteOption;
	private String coverPicture;
	
	public static final Parcelable.Creator<FbEventCompleteInfo> CREATOR = new Parcelable.Creator<FbEventCompleteInfo>() {
		public FbEventCompleteInfo createFromParcel(Parcel in) {
			return new FbEventCompleteInfo(in);
		}

		public FbEventCompleteInfo[] newArray(int size) {
			return new FbEventCompleteInfo[size];
		}
	};
	
	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(event, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
		dest.writeParcelable(venue, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
		dest.writeStringList(friendsAttending);
		dest.writeStringList(friendsMaybe);
		dest.writeStringList(friendsUnreplied);
		dest.writeInt(attendingCount);
		dest.writeInt(maybeCount);
		dest.writeInt(notRepliedCount);
		dest.writeByte((byte) (inviteOption ? 1 : 0)); 
		dest.writeString(coverPicture);
	}

	/**
	 * Constructor using parcel
	 * @param in
	 */
	public FbEventCompleteInfo(Parcel in) {
		this.event = (FbEvent) in.readParcelable(FbEvent.class.getClassLoader());
		this.venue = (Venue) in.readParcelable(Venue.class.getClassLoader());
		this.friendsAttending = new ArrayList<String>();
		this.friendsMaybe = new ArrayList<String>();
		this.friendsUnreplied = new ArrayList<String>();
		in.readStringList(friendsAttending);
		in.readStringList(friendsMaybe);
		in.readStringList(friendsUnreplied);
		this.attendingCount = in.readInt();
		this.maybeCount = in.readInt();
		this.notRepliedCount = in.readInt();
		this.inviteOption = (in.readByte() != 0);
		this.coverPicture = in.readString();
		
	}

	public FbEventCompleteInfo() {
		this.event = new FbEvent();
		this.venue = new Venue();
		this.friendsAttending = new ArrayList<String>();
		this.friendsMaybe = new ArrayList<String>();
		this.friendsUnreplied = new ArrayList<String>();

		this.attendingCount = 0;
		this.maybeCount = 0;
		this.notRepliedCount = 0;
		this.inviteOption = false;
		this.coverPicture = "";
	}
	
	/**
	 * @return the event
	 */
	public FbEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(FbEvent event) {
		this.event = event;
	}

	/**
	 * @return the venue
	 */
	public Venue getVenue() {
		return venue;
	}


	/**
	 * @param venue the venue to set
	 */
	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	/**
	 * @return the friendsAttending
	 */
	public ArrayList<String> getFriendsAttending() {
		return friendsAttending;
	}


	/**
	 * @param friendsAttending the friendsAttending to set
	 */
	public void setFriendsAttending(ArrayList<String> friendsAttending) {
		this.friendsAttending = friendsAttending;
	}


	/**
	 * @return the friendsMaybe
	 */
	public ArrayList<String> getFriendsMaybe() {
		return friendsMaybe;
	}


	/**
	 * @param friendsMaybe the friendsMaybe to set
	 */
	public void setFriendsMaybe(ArrayList<String> friendsMaybe) {
		this.friendsMaybe = friendsMaybe;
	}


	/**
	 * @return the friendsUnreplied
	 */
	public ArrayList<String> getFriendsUnreplied() {
		return friendsUnreplied;
	}


	/**
	 * @param friendsUnreplied the friendsUnreplied to set
	 */
	public void setFriendsUnreplied(ArrayList<String> friendsUnreplied) {
		this.friendsUnreplied = friendsUnreplied;
	}


	/**
	 * @return the attendingCount
	 */
	public int getAttendingCount() {
		return attendingCount;
	}


	/**
	 * @param attendingCount the attendingCount to set
	 */
	public void setAttendingCount(int attendingCount) {
		this.attendingCount = attendingCount;
	}


	/**
	 * @return the maybeCount
	 */
	public int getMaybeCount() {
		return maybeCount;
	}


	/**
	 * @param maybeCount the maybeCount to set
	 */
	public void setMaybeCount(int maybeCount) {
		this.maybeCount = maybeCount;
	}


	/**
	 * @return the notRepliedCount
	 */
	public int getNotRepliedCount() {
		return notRepliedCount;
	}


	/**
	 * @param notRepliedCount the notRepliedCount to set
	 */
	public void setNotRepliedCount(int notRepliedCount) {
		this.notRepliedCount = notRepliedCount;
	}


	/**
	 * @return the inviteOption
	 */
	public boolean isInviteOption() {
		return inviteOption;
	}


	/**
	 * @param inviteOption the inviteOption to set
	 */
	public void setInviteOption(boolean inviteOption) {
		this.inviteOption = inviteOption;
	}

	/**
	 * @return the coverPicture
	 */
	public String getCoverPicture() {
		return coverPicture;
	}


	/**
	 * @param coverPicture the coverPicture to set
	 */
	public void setCoverPicture(String coverPicture) {
		this.coverPicture = coverPicture;
	}

	////////VENUE
	/**
	 * @return the id
	 */
	public String getVenueId() {
		return venue.getId();
	}

	/**
	 * @param id the id to set
	 */
	public void setVenueId(String id) {
		this.venue.setId(id);
	}

	/**
	 * @return the street
	 */
	public String getVenueStreet() {
		return venue.getStreet();
	}

	/**
	 * @param street the street to set
	 */
	public void setVenueStreet(String street) {
		this.venue.setStreet(street);
	}

	/**
	 * @return the city
	 */
	public String getVenueCity() {
		return venue.getCity();
	}

	/**
	 * @param city the city to set
	 */
	public void setVenueCity(String city) {
		this.venue.setCity(city);
	}

	/**
	 * @return the state
	 */
	public String getVenueState() {
		return venue.getState();
	}

	/**
	 * @param state the state to set
	 */
	public void setVenueState(String state) {
		this.venue.setState(state);
	}

	/**
	 * @return the zip
	 */
	public String getVenueZip() {
		return venue.getZip();
	}

	/**
	 * @param zip the zip to set
	 */
	public void setVenueZip(String zip) {
		this.venue.setZip(zip);
	}

	/**
	 * @return the country
	 */
	public String getVenueCountry() {
		return venue.getCountry();
	}

	/**
	 * @param country the country to set
	 */
	public void setVenueCountry(String country) {
		this.venue.setCountry(country);
	}

	/**
	 * @return the latitude
	 */
	public double getVenueLatitude() {
		return venue.getLatitude();
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setVenueLatitude(double latitude) {
		this.venue.setLatitude(latitude);
	}

	/**
	 * @return the longitude
	 */
	public double getVenueLongitude() {
		return venue.getLongitude();
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setVenueLongitude(double longitude) {
		this.venue.setLongitude(longitude);
	}

	/**
	 * get the full address 
	 */
	public String getFullAddress() {
		return getVenueStreet() + "\n" +
				getVenueCity() + ", " + getVenueState() + " " + getVenueZip() + "\n" +
				getVenueCountry();
	}

}
