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

public class Comments implements Parcelable {

	public static final String TAG = Comments.class.getSimpleName ();

	public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";

	private List<Comment> comments;

	public Comments () {
		comments = new ArrayList<> ();
	}

	private Comments (Parcel in) {
		comments = new ArrayList<> ();
		in.readList (comments, getClass ().getClassLoader ());
	}

	public List<Comment> getComments () {
		return comments;
	}

	public void setComments (List<Comment> comments) {
		this.comments = comments;
	}

	public void add (Comment comment) {
		comments.add (comment);
	}

	public boolean isEmpty () {
		return comments.isEmpty ();
	}

	public int getCount () {
		return comments.size ();
	}

	@Override
	public int describeContents () {
		return 0;
	}

	public static Comments getParcelable (Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			return savedInstanceState.getParcelable (PARCELABLE_KEY);
		} else {
			throw new IllegalArgumentException (
					TAG + ": \'getParcelable\' Method has null argument: savedInstanceState.");
		}
	}

	public static void putParcelable (Bundle savedInstanceState, Comments comments) {
		if (savedInstanceState != null && comments != null) {
			savedInstanceState.putParcelable (PARCELABLE_KEY, comments);
		}
	}

	@Override
	public void writeToParcel (Parcel parcel, int i) {
		parcel.writeList (comments);
	}

	public static Parcelable.Creator<Comments> getCREATOR () {
		return CREATOR;
	}

	public static final Parcelable.Creator<Comments> CREATOR = new Parcelable
			.Creator<Comments> () {
		public Comments createFromParcel (Parcel in) {
			return new Comments (in);
		}

		public Comments[] newArray (int size) {
			return new Comments[size];
		}
	};
}
