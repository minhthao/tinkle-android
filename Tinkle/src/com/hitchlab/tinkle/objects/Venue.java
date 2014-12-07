package com.hitchlab.tinkle.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Venue implements Parcelable {
	private String id;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String country;
	private double latitude;
	private double longitude;
	
	public static final Parcelable.Creator<Venue> CREATOR = new Parcelable.Creator<Venue>() {
		public Venue createFromParcel(Parcel in) {
			return new Venue(in);
		}

		public Venue[] newArray(int size) {
			return new Venue[size];
		}
	};
	
	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(street);
		dest.writeString(city);
		dest.writeString(state);
		dest.writeString(zip);
		dest.writeString(country);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
	}

	/**
	 * Constructor using parcel
	 * @param in
	 */
	public Venue(Parcel in) {
		id = in.readString();
		street = in.readString();
		city = in.readString();
		state = in.readString();
		zip = in.readString();
		country = in.readString();
		latitude = in.readDouble();
		longitude = in.readDouble();
	}
	
	/**
	 * Empty constructor
	 */
	public Venue() {
		super();
		this.id = "";
		this.street = "";
		this.city = "";
		this.state = "";
		this.zip = "";
		this.country = "";
		this.latitude = 0;
		this.longitude = 0;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the street
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * @param street the street to set
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the zip
	 */
	public String getZip() {
		return zip;
	}

	/**
	 * @param zip the zip to set
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
