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

import com.google.gson.JsonObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Product extends RealmObject {

	@PrimaryKey
	private int id;

	private String title;
	private String tagline;
	private String discussionUrl;
	private String productUrl;
	private int votes;
	private int numberOfComments;
	private String smallImgUrl;
	private String largeImgUrl;
	private String date;
	private boolean read;

	// Empty constructor needed for Realm
	public Product () {
		this.id = 0;
		this.title = "";
		this.tagline = "";
		this.discussionUrl = "";
		this.productUrl = "";
		this.votes = 0;
		this.numberOfComments = 0;
		this.smallImgUrl = "";
		this.largeImgUrl = "";
		this.date = "";
		this.read = false;
	}

	public Product (int id,
	                String title,
	                String tagline,
	                String discussionUrl,
	                String productUrl,
	                int votes,
	                int numberOfComments,
	                String smallImgUrl,
	                String largeImgUrl,
	                String day) {
		this.id = id;
		this.title = title;
		this.tagline = tagline;
		this.discussionUrl = discussionUrl;
		this.productUrl = productUrl;
		this.votes = votes;
		this.numberOfComments = numberOfComments;
		this.smallImgUrl = smallImgUrl;
		this.largeImgUrl = largeImgUrl;
		this.date = day;
		this.read = false;
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
				object.get ("screenshot_url").getAsJsonObject ().get ("850px")
						.getAsString (),
				object.get ("day").getAsString ());
	}

	public int getId () {
		return id;
	}

	public void setId (int id) {
		this.id = id;
	}

	public String getTitle () {
		return title;
	}

	public void setTitle (String title) {
		this.title = title;
	}

	public String getTagline () {
		return tagline;
	}

	public void setTagline (String tagline) {
		this.tagline = tagline;
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

	public int getVotes () {
		return votes;
	}

	public void setVotes (int votes) {
		this.votes = votes;
	}

	public int getNumberOfComments () {
		return numberOfComments;
	}

	public void setNumberOfComments (int numberOfComments) {
		this.numberOfComments = numberOfComments;
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

	public String getDate () {
		return date;
	}

	public void setDate (String date) {
		this.date = date;
	}

	public boolean isRead () {
		return read;
	}

	public void setRead (boolean read) {
		this.read = read;
	}
}
