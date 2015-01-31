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

package com.jaspervanriet.huntingthatproduct.Classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

public class Collection implements Parcelable {
	public int id;
	public String name;
	public String title;
	public String backgroundImageUrl;
	public String collectionUrl;

	public Collection (int id, String name, String title,
	                   String backgroundImageUrl, String collectionUrl) {
		this.id = id;
		this.name = name;
		this.title = title;
		this.backgroundImageUrl = backgroundImageUrl;
		this.collectionUrl = collectionUrl;
	}

	public Collection (JsonObject object) {
		this (object.get ("id").getAsInt (),
				object.get ("name").isJsonNull ()
						? "" : object.get ("name").getAsString (),
				object.get ("title").isJsonNull ()
						? "" : object.get ("title").getAsString (),
				object.get ("background_image_url").isJsonNull ()
						? "" : object.get ("background_image_url")
						.getAsString (),
				object.get ("collection_url").getAsString ());
	}

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
	}
}
