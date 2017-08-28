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

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Categories implements Parcelable {

	public static final String TAG = Categories.class.getSimpleName ();

	public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";

	private List<Category> categories;

	public Categories () {
		categories = new ArrayList<> ();
	}

	public Categories (Parcel in) {
		categories = new ArrayList<> ();
		in.readList (categories, getClass ().getClassLoader ());
	}

	public List<Category> getCategories () {
		return categories;
	}

	public void setCategories (List<Category> categories) {
		this.categories = categories;
	}

	public boolean isEmpty () {
		return categories.isEmpty ();
	}

	public int size () {
		return categories.size ();
	}

	public void clear () {
		categories.clear ();
	}

	@Override
	public int describeContents () {
		return 0;
	}

	public static Categories getParcelable (Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			return savedInstanceState.getParcelable (PARCELABLE_KEY);
		} else {
			throw new IllegalArgumentException (
					TAG + ": \'getParcelable\' Method has null argument: savedInstanceState.");
		}
	}

	public static void putParcelable (Bundle savedInstanceState, Categories categories) {
		if (savedInstanceState != null && categories != null) {
			savedInstanceState.putParcelable (PARCELABLE_KEY, categories);
		}
	}

	@Override
	public void writeToParcel (Parcel parcel, int i) {
		parcel.writeList (categories);
	}

	public static Parcelable.Creator<Categories> getCREATOR () {
		return CREATOR;
	}

	public static final Parcelable.Creator<Categories> CREATOR = new Parcelable
			.Creator<Categories> () {
		public Categories createFromParcel (Parcel in) {
			return new Categories (in);
		}

		public Categories[] newArray (int size) {
			return new Categories[size];
		}
	};
}
