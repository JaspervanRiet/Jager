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
