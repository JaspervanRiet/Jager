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

public class Collections implements Parcelable {

	public static final String TAG = Collections.class.getSimpleName ();

	public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";

	private List<Collection> collections;

	private Collections (Parcel in) {
		collections = new ArrayList<> ();
		in.readList (collections, null);
	}

	public List<Collection> getCollections () {
		return collections;
	}

	public void setCollections (List<Collection> collections) {
		this.collections = collections;
	}

	@Override
	public int describeContents () {
		return 0;
	}

	public static Collections getParcelable (Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			return savedInstanceState.getParcelable (PARCELABLE_KEY);
		} else {
			throw new IllegalArgumentException (TAG + ": \'getParcelable\' Method has null argument: savedInstanceState.");
		}
	}

	public static void putParcelable (Bundle savedInstanceState, Collections collections) {
		if (savedInstanceState != null && collections != null) {
			savedInstanceState.putParcelable (PARCELABLE_KEY, collections);
		}
	}

	@Override
	public void writeToParcel (Parcel parcel, int i) {
		parcel.writeList (collections);
	}

	public static Creator<Collections> getCREATOR () {
		return CREATOR;
	}

	public static final Creator<Collections> CREATOR = new Parcelable
			.Creator<Collections> () {
		public Collections createFromParcel (Parcel in) {
			return new Collections (in);
		}

		public Collections[] newArray (int size) {
			return new Collections[size];
		}
	};
}
