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

public class Category implements Parcelable {

	private int id;
	private String slug;
	private String name;
	private String color;
	@SerializedName ("item_name")
	private String itemName;

	private Category (Parcel in) {
		this.id = in.readInt ();
		this.slug = in.readString ();
		this.name = in.readString ();
		this.color = in.readString ();
		this.itemName = in.readString ();
	}

	public int getId () {
		return id;
	}

	public void setId (int id) {
		this.id = id;
	}

	public String getSlug () {
		return slug;
	}

	public void setSlug (String slug) {
		this.slug = slug;
	}

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
	}

	public String getColor () {
		return color;
	}

	public void setColor (String color) {
		this.color = color;
	}

	public String getItemName () {
		return itemName;
	}

	public void setItemName (String itemName) {
		this.itemName = itemName;
	}

	@Override
	public int describeContents () {
		return 0;
	}

	@Override
	public void writeToParcel (Parcel out, int flags) {
		out.writeInt (id);
		out.writeString (slug);
		out.writeString (name);
		out.writeString (color);
		out.writeString (itemName);
	}

	public static final Creator<Category> CREATOR = new Parcelable.Creator<Category> () {
		public Category createFromParcel (Parcel in) {
			return new Category (in);
		}

		public Category[] newArray (int size) {
			return new Category[size];
		}
	};
}
