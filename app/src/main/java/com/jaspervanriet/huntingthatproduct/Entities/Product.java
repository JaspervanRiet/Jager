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

public class Product implements Parcelable {
	private int id;
	private String name;
	private String tagline;
	@SerializedName ("comments_count")
	private int commentsCount;
	@SerializedName ("votes_count")
	private int votesCount;
	@SerializedName ("discussion_url")
	private String discussionUrl;
	@SerializedName ("redirect_url")
	private String productUrl;
	@SerializedName ("screenshot_url")
	private ScreenshotUrl screenshotUrl;

	public class ScreenshotUrl {
		@SerializedName ("300px")
		private String smallImgUrl;
		@SerializedName ("850px")
		private String largeImgUrl;

		public ScreenshotUrl (String smallImgUrl, String largeImgUrl) {
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

	@Override
	public int describeContents () {
		return 0;
	}

	@Override
	public void writeToParcel (Parcel out, int flags) {
		out.writeInt (id);
		out.writeString (name);
		out.writeString (tagline);
		out.writeString (discussionUrl);
		out.writeString (productUrl);
		out.writeInt (votesCount);
		out.writeInt (commentsCount);
		out.writeString (screenshotUrl.getSmallImgUrl ());
		out.writeString (screenshotUrl.getLargeImgUrl ());
	}

	public static final Creator<Product> CREATOR = new Parcelable.Creator<Product> () {
		public Product createFromParcel (Parcel in) {
			return new Product (in);
		}

		public Product[] newArray (int size) {
			return new Product[size];
		}
	};

	private Product (Parcel in) {
		this.id = in.readInt ();
		this.name = in.readString ();
		this.tagline = in.readString ();
		this.discussionUrl = in.readString ();
		this.productUrl = in.readString ();
		this.votesCount = in.readInt ();
		this.commentsCount = in.readInt ();
		this.screenshotUrl = new ScreenshotUrl (in.readString (), in.readString ());
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

	public String getTagline () {
		return tagline;
	}

	public void setTagline (String tagline) {
		this.tagline = tagline;
	}

	public int getCommentsCount () {
		return commentsCount;
	}

	public void setCommentsCount (int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public int getVotesCount () {
		return votesCount;
	}

	public void setVotesCount (int votesCount) {
		this.votesCount = votesCount;
	}

	public String getDiscussionUrl () {
		return discussionUrl;
	}

	public void setDiscussionUrl (String discussionUrl) {
		this.discussionUrl = discussionUrl;
	}

	public String getProductUrl () {
		return productUrl;
	}

	public void setProductUrl (String productUrl) {
		this.productUrl = productUrl;
	}

	public ScreenshotUrl getScreenshotUrl () {
		return screenshotUrl;
	}

	public void setScreenshotUrl (ScreenshotUrl screenshotUrl) {
		this.screenshotUrl = screenshotUrl;
	}
}
