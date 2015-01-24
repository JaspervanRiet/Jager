/*
 * Copyright (c) Jasper van Riet 2015.
 */

package com.jaspervanriet.huntingthatproduct.Classes;

import com.google.gson.JsonObject;


public class Comment {
	public int id;
	public String text;
	public int parentComment;
	public int childCommentCount;
	public User user;
	public int level;


	public Comment (int id, String text, int parentComment, int childCommentCount, User user) {
		this.id = id;
		this.text = text;
		this.parentComment = parentComment;
		this.childCommentCount = childCommentCount;
		this.user = user;
	}

	public Comment (JsonObject object) {
		this (object.get ("id").getAsInt (),
				object.get ("body").getAsString (),
				object.get ("parent_comment_id").isJsonNull ()
						? -1 : object.get ("parent_comment_id").getAsInt (),
				object.get ("child_comments_count").isJsonNull ()
						? -1 : object.get ("child_comments_count").getAsInt (),
				new User (object.get ("user").getAsJsonObject ()));
	}
}
