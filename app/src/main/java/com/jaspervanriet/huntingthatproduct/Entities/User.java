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

public class User implements Parcelable {

	private int id;
	private String name;
	private String headline;
	private String username;
	@SerializedName ("image_url")
	private ImageUrl imageUrl;

	private User (Parcel in) {
		this.id = in.readInt ();
		this.name = in.readString ();
		this.headline = in.readString ();
		this.username = in.readString ();
		this.imageUrl = new ImageUrl (in.readString (), in.readString ());
	}

	@Override
	public int describeContents () {
		return 0;
	}

	@Override
	public void writeToParcel (Parcel out, int flags) {
		out.writeInt (id);
		out.writeString (name);
		out.writeString (headline);
		out.writeString (username);
		out.writeString (name);
		out.writeString (imageUrl.getSmallImgUrl ());
		out.writeString (imageUrl.getLargeImgUrl ());
	}

	public class ImageUrl {
		@SerializedName ("48px")
		private String smallImgUrl;
		@SerializedName ("73px")
		private String largeImgUrl;

		public ImageUrl (String smallImgUrl, String largeImgUrl) {
			this.smallImgUrl = smallImgUrl;
			this.largeImgUrl = largeImgUrl;
		}

		public String getSmallImgUrl () {
			return smallImgUrl;
		}

		public void setSmallImgUrl (String smallImgUrl) {
			this.smallImgUrl = smallImgUrl;
		}

		public String getLargeImgUrl () {
			return largeImgUrl;
		}

		public void setLargeImgUrl (String largeImgUrl) {
			this.largeImgUrl = largeImgUrl;
		}
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

	public String getHeadline () {
		return headline;
	}

	public void setHeadline (String headline) {
		this.headline = headline;
	}

	public String getUsername () {
		return username;
	}

	public void setUsername (String username) {
		this.username = username;
	}

	public ImageUrl getImageUrl () {
		return imageUrl;
	}

	public void setImageUrl (ImageUrl imageUrl) {
		this.imageUrl = imageUrl;
	}

	public static final Creator<User> CREATOR = new Parcelable.Creator<User> () {
		public User createFromParcel (Parcel in) {
			return new User (in);
		}

		public User[] newArray (int size) {
			return new User[size];
		}
	};

}
