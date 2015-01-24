/*
 * Copyright (c) Jasper van Riet 2015.
 */

package com.jaspervanriet.huntingthatproduct.Classes;

import com.google.gson.JsonObject;


public class User {

	public int id;
	public String name;
	public String headline;
	public String username;
	public String smallImgUrl;
	public String largeImgUrl;

	public User (int id, String name, String headline,
	             String username, String smallImgUrl, String largeImgUrl) {
		this.id = id;
		this.name = name;
		this.headline = headline;
		this.username = username;
		this.smallImgUrl = smallImgUrl;
		this.largeImgUrl = largeImgUrl;
	}

	public User (JsonObject object) {
		this (object.get ("id").getAsInt (),
				object.get ("name").isJsonNull ()
						? "" : object.get ("name").getAsString (),
				object.get ("headline").isJsonNull ()
						? "" : object.get ("headline").getAsString (),
				object.get ("username").getAsString (),
				object.get ("image_url").getAsJsonObject ().get ("48px").getAsString (),
				object.get ("image_url").getAsJsonObject ().get ("73px").getAsString ());
	}

}
