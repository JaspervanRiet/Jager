/*
 * Copyright (c) Jasper van Riet 2015.
 */

package com.jaspervanriet.huntingthatproduct.Classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;


public class Product implements Parcelable {
	public int id;
	public String title;
	public String tagline;
	public String discussionUrl;
	public String productUrl;
	public int votes;
	public int numberOfComments;
	public String smallImgUrl;
	public String largeImgUrl;

	public Product (int id,
	                String title,
	                String tagline,
	                String discussionUrl,
	                String productUrl,
	                int votes,
	                int numberOfComments,
	                String smallImgUrl,
	                String largeImgUrl) {
		this.id = id;
		this.title = title;
		this.tagline = tagline;
		this.discussionUrl = discussionUrl;
		this.productUrl = productUrl;
		this.votes = votes;
		this.numberOfComments = numberOfComments;
		this.smallImgUrl = smallImgUrl;
		this.largeImgUrl = largeImgUrl;
	}

	public Product (JsonObject object) {
		this (object.get ("id").getAsInt (),
				object.get ("name").getAsString (),
				object.get ("tagline").getAsString (),
				object.get ("discussion_url").getAsString (),
				object.get ("redirect_url").getAsString (),
				object.get ("votes_count").getAsInt (),
				object.get ("comments_count").getAsInt (),
				object.get ("screenshot_url").getAsJsonObject ().get ("300px").getAsString (),
				object.get ("screenshot_url").getAsJsonObject ().get ("850px").getAsString ());
	}


	@Override
	public int describeContents () {
		return 0;
	}

	@Override
	public void writeToParcel (Parcel out, int flags) {
		out.writeInt (id);
		out.writeString (title);
		out.writeString (tagline);
		out.writeString (discussionUrl);
		out.writeString (productUrl);
		out.writeInt (votes);
		out.writeInt (numberOfComments);
		out.writeString (smallImgUrl);
		out.writeString (largeImgUrl);
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
		this.title = in.readString ();
		this.tagline = in.readString ();
		this.discussionUrl = in.readString ();
		this.productUrl = in.readString ();
		this.votes = in.readInt ();
		this.numberOfComments = in.readInt ();
		this.smallImgUrl = in.readString ();
		this.largeImgUrl = in.readString ();
	}

}
