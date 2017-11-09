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

public class Topics implements Parcelable {

	public static final String TAG = Topics.class.getSimpleName ();

	public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";

	private List<Topic> topics;

	public Topics () {
		topics = new ArrayList<> ();
	}

	public Topics (Parcel in) {
		topics = new ArrayList<> ();
		in.readList (topics, getClass ().getClassLoader ());
	}

	public List<Topic> gettopics () {
		return topics;
	}

	public void setTopics (List<Topic> topics) {
		this.topics = topics;
	}

	public boolean isEmpty () {
		return topics.isEmpty ();
	}

	public int size () {
		return topics.size ();
	}

	public void clear () {
		topics.clear ();
	}

	@Override
	public int describeContents () {
		return 0;
	}

	public static Topics getParcelable (Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			return savedInstanceState.getParcelable (PARCELABLE_KEY);
		} else {
			throw new IllegalArgumentException (
					TAG + ": \'getParcelable\' Method has null argument: savedInstanceState.");
		}
	}

	public static void putParcelable (Bundle savedInstanceState, Topics topics) {
		if (savedInstanceState != null && topics != null) {
			savedInstanceState.putParcelable (PARCELABLE_KEY, topics);
		}
	}

	@Override
	public void writeToParcel (Parcel parcel, int i) {
		parcel.writeList (topics);
	}

	public static Parcelable.Creator<Topics> getCREATOR () {
		return CREATOR;
	}

	public static final Parcelable.Creator<Topics> CREATOR = new Parcelable
			.Creator<Topics> () {
		public Topics createFromParcel (Parcel in) {
			return new Topics (in);
		}

		public Topics[] newArray (int size) {
			return new Topics[size];
		}
	};
}
