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


public class Comment {
	public int id;
	public String text;
	public int parentComment;
	public int childCommentCount;
	public boolean isMaker;
	public User user;
	public int level;


	public Comment (int id, String text, int parentComment,
	                int childCommentCount, boolean isMaker, User user) {
		this.id = id;
		this.text = text;
		this.parentComment = parentComment;
		this.childCommentCount = childCommentCount;
		this.user = user;
		this.isMaker = isMaker;
	}

	public Comment (JsonObject object) {
		this (object.get ("id").getAsInt (),
				object.get ("body").getAsString (),
				object.get ("parent_comment_id").isJsonNull ()
						? -1 : object.get ("parent_comment_id").getAsInt (),
				object.get ("child_comments_count").isJsonNull ()
						? -1 : object.get ("child_comments_count").getAsInt (),
				object.get ("maker").getAsBoolean (),
				new User (object.get ("user").getAsJsonObject ()));
	}
}
