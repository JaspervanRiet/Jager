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

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Comment {
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
}
