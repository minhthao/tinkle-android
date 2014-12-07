package com.hitchlab.tinkle.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Tag implements Parcelable {
	String id;
	String name;
	int offset;
	int length;
	
	public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
		public Tag createFromParcel(Parcel in) {
			return new Tag(in);
		}

		public Tag[] newArray(int size) {
			return new Tag[size];
		}
	};
	
	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeInt(offset);
		dest.writeInt(length);
	}
	
	/**
	 * Constructor using parcel
	 * @param in
	 */
	public Tag(Parcel in) {
		this.id = in.readString();
		this.name = in.readString();
		this.offset = in.readInt();
		this.length = in.readInt();
	}
	
	/**
	 * @param id
	 * @param name
	 * @param offset
	 * @param length
	 */
	public Tag(String id, String name, int offset, int length) {
		super();
		this.id = id;
		this.name = name;
		this.offset = offset;
		this.length = length;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	
}
