/*
 * Copyright (C) 2017 Jasper van Riet
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

public class Topic implements Parcelable {

	private int id;
	private String name;
	private String slug;
	@SerializedName ("updated_at")
	private String updated;
	@SerializedName ("created_at")
	private String created;
	private String description;
	private String image;
	@SerializedName ("followers_count")
	private int followersCount;
	@SerializedName ("posts_count")
	private int postsCount;
	private boolean trending;

	private Topic (Parcel in) {
		this.id = in.readInt ();
		this.name = in.readString ();
		this.slug = in.readString ();
		this.updated = in.readString ();
		this.created = in.readString ();
		this.description = in.readString ();
		this.image = in.readString ();
		this.followersCount = in.readInt ();
		this.postsCount = in.readInt ();
		this.trending = in.readByte () != 0;
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

	public String getSlug () {
		return slug;
	}

	public void setSlug (String slug) {
		this.slug = slug;
	}

	public String getUpdated () {
		return updated;
	}

	public void setUpdated (String updated) {
		this.updated = updated;
	}

	public String getCreated () {
		return created;
	}

	public void setCreated (String created) {
		this.created = created;
	}

	public String getDescription () {
		return description;
	}

	public void setDescription (String description) {
		this.description = description;
	}

	public String getImage () {
		return image;
	}

	public void setImage (String image) {
		this.image = image;
	}

	public int getFollowersCount () {
		return followersCount;
	}

	public void setFollowersCount (int followersCount) {
		this.followersCount = followersCount;
	}

	public int getPostsCount () {
		return postsCount;
	}

	public void setPostsCount (int postsCount) {
		this.postsCount = postsCount;
	}

	public boolean isTrending () {
		return trending;
	}

	public void setTrending (boolean trending) {
		this.trending = trending;
	}

	@Override
	public int describeContents () {
		return 0;
	}

	@Override
	public void writeToParcel (Parcel out, int flags) {
		out.writeInt (id);
		out.writeString (name);
		out.writeString (slug);
		out.writeString (updated);
		out.writeString (created);
		out.writeString (description);
		out.writeString (image);
		out.writeInt (followersCount);
		out.writeInt (postsCount);
		out.writeByte ((byte) (trending ? 1 : 0));
	}

	public static final Creator<Topic> CREATOR = new Parcelable.Creator<Topic> () {
		public Topic createFromParcel (Parcel in) {
			return new Topic (in);
		}

		public Topic[] newArray (int size) {
			return new Topic[size];
		}
	};
}
