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

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Comment implements Parcelable {

	public static final String TAG = Comment.class.getSimpleName ();

	public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";

	private int id;
	private String body;
	@SerializedName ("parent_comment_id")
	private int parentComment;
	@SerializedName ("child_comments_count")
	private int childCommentCount;
	@SerializedName ("maker")
	private boolean isMaker;
	private User user;
	private int level;
	@SerializedName ("child_comments")
	private List<Comment> childComments;

	private Comment (Parcel in) {
		this.id = in.readInt ();
		this.body = in.readString ();
		this.parentComment = in.readInt ();
		this.childCommentCount = in.readInt ();
		this.isMaker = in.readByte () != 0;
		this.user = in.readParcelable (User.class.getClassLoader ());
		this.level = in.readInt ();
		in.readList (childComments, getClass ().getClassLoader ());
	}

	@Override
	public int describeContents () {
		return 0;
	}

	@Override
	public void writeToParcel (Parcel out, int flags) {
		out.writeInt (id);
		out.writeString (body);
		out.writeInt (parentComment);
		out.writeInt (childCommentCount);
		out.writeByte ((byte) (isMaker ? 1 : 0));
		out.writeParcelable (user, flags);
		out.writeInt (level);
		out.writeList (childComments);
	}

	public int getId () {
		return id;
	}

	public void setId (int id) {
		this.id = id;
	}

	public String getBody () {
		return body;
	}

	public void setBody (String body) {
		this.body = body;
	}

	public int getParentComment () {
		return parentComment;
	}

	public void setParentComment (int parentComment) {
		this.parentComment = parentComment;
	}

	public int getChildCommentCount () {
		return childCommentCount;
	}

	public void setChildCommentCount (int childCommentCount) {
		this.childCommentCount = childCommentCount;
	}

	public boolean isMaker () {
		return isMaker;
	}

	public void setIsMaker (boolean isMaker) {
		this.isMaker = isMaker;
	}

	public User getUser () {
		return user;
	}

	public void setUser (User user) {
		this.user = user;
	}

	public int getLevel () {
		return level;
	}

	public void setLevel (int level) {
		this.level = level;
	}

	public List<Comment> getChildComments () {
		return childComments;
	}

	public void setChildComments (List<Comment> childComments) {
		this.childComments = childComments;
	}

	public int getChildCommentsCount () {
		return childComments.size ();
	}

	public static Comment getParcelable (Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			return savedInstanceState.getParcelable (PARCELABLE_KEY);
		} else {
			throw new IllegalArgumentException (
					TAG + ": \'getParcelable\' Method has null argument: savedInstanceState.");
		}
	}

	public static void putParcelable (Bundle savedInstanceState, Comment comment) {
		if (savedInstanceState != null && comment != null) {
			savedInstanceState.putParcelable (PARCELABLE_KEY, comment);
		}
	}

	public static Parcelable.Creator<Comment> getCREATOR () {
		return CREATOR;
	}

	public static final Parcelable.Creator<Comment> CREATOR = new Parcelable
			.Creator<Comment> () {
		public Comment createFromParcel (Parcel in) {
			return new Comment (in);
		}

		public Comment[] newArray (int size) {
			return new Comment[size];
		}
	};

}
