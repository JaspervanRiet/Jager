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

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Posts implements Parcelable {

	public static final String TAG = Collections.class.getSimpleName ();

	public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";

	private List<Product> posts;

	public Posts () {

	}

	public Posts (Parcel in) {
		posts = new ArrayList<> ();
		in.readList (posts, null);
	}

	public List<Product> getPosts () {
		return posts;
	}

	public void setPosts (List<Product> posts) {
		this.posts = posts;
	}

	public boolean isEmpty () {
		return posts.isEmpty ();
	}

	public void clear () {
		posts.clear ();
	}

	@Override
	public int describeContents () {
		return 0;
	}

	public static Posts getParcelable (Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			return savedInstanceState.getParcelable (PARCELABLE_KEY);
		} else {
			throw new IllegalArgumentException (TAG + ": \'getParcelable\' Method has null argument: savedInstanceState.");
		}
	}

	public static void putParcelable (Bundle savedInstanceState, Posts posts) {
		if (savedInstanceState != null && posts != null) {
			savedInstanceState.putParcelable (PARCELABLE_KEY, posts);
		}
	}

	@Override
	public void writeToParcel (Parcel parcel, int i) {
		parcel.writeList (posts);
	}

	public static Parcelable.Creator<Posts> getCREATOR () {
		return CREATOR;
	}

	public static final Parcelable.Creator<Posts> CREATOR = new Parcelable
			.Creator<Posts> () {
		public Posts createFromParcel (Parcel in) {
			return new Posts (in);
		}

		public Posts[] newArray (int size) {
			return new Posts[size];
		}
	};
}
