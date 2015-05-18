/*
 * Copyright (C) 2015 Jasper van Riet
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspervanriet.huntingthatproduct.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Collection implements Parcelable {
	private int id;
	private String name;
	private String title;
	@SerializedName ("background_image_url")
	private String backgroundImageUrl;
	@SerializedName ("collection_url")
	private String collectionUrl;
	private List<Product> posts;

	@Override
	public int describeContents () {
		return 0;
	}

	@Override
	public void writeToParcel (Parcel out, int flags) {
		out.writeInt (id);
		out.writeString (name);
		out.writeString (title);
		out.writeString (backgroundImageUrl);
		out.writeString (collectionUrl);
		out.writeList (posts);
	}

	public static final Creator<Collection> CREATOR = new Parcelable
			.Creator<Collection> () {
		public Collection createFromParcel (Parcel in) {
			return new Collection (in);
		}

		public Collection[] newArray (int size) {
			return new Collection[size];
		}
	};

	private Collection (Parcel in) {
		this.id = in.readInt ();
		this.name = in.readString ();
		this.title = in.readString ();
		this.backgroundImageUrl = in.readString ();
		this.collectionUrl = in.readString ();
		posts = new ArrayList<> ();
		in.readList (posts, getClass ().getClassLoader ());
	}

	public int getId () {
		return id;
	}

	public void setId (int id) {
		this.id = id;
	}

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
	}

	public String getTitle () {
		return title;
	}

	public void setTitle (String title) {
		this.title = title;
	}

	public String getBackgroundImageUrl () {
		return backgroundImageUrl;
	}

	public void setBackgroundImageUrl (String backgroundImageUrl) {
		this.backgroundImageUrl = backgroundImageUrl;
	}

	public String getCollectionUrl () {
		return collectionUrl;
	}

	public void setCollectionUrl (String collectionUrl) {
		this.collectionUrl = collectionUrl;
	}

	public List<Product> getPosts () {
		return posts;
	}

	public void setPosts (List<Product> posts) {
		this.posts = posts;
	}

	public static Creator<Collection> getCREATOR () {
		return CREATOR;
	}
}
