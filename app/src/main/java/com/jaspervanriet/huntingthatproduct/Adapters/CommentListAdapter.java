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

package com.jaspervanriet.huntingthatproduct.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaspervanriet.huntingthatproduct.Classes.Comment;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Utils.CircleTransform;
import com.jaspervanriet.huntingthatproduct.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter
		.CommentsViewHolder> {

	private ArrayList<Comment> mComments;
	private Context mContext;

	public CommentListAdapter (Context context, ArrayList<Comment> comments) {
		this.mContext = context;
		this.mComments = comments;

	}

	@Override
	public int getItemCount () {
		return mComments.size ();
	}

	@Override
	public CommentsViewHolder onCreateViewHolder (ViewGroup viewGroup, int viewType) {
		View itemView = LayoutInflater.
				from (viewGroup.getContext ()).
				inflate (R.layout.item_comment, viewGroup, false);
		return new CommentsViewHolder (itemView);
	}

	@Override
	public void onBindViewHolder (CommentsViewHolder holder, int position) {
		Comment comment = mComments.get (position);
		LinearLayout.MarginLayoutParams params = (LinearLayout.MarginLayoutParams) holder
				.commentLayout.getLayoutParams ();
		params.setMargins (comment.level * 50, 0, 0, 0);
		holder.commentLayout.setLayoutParams (params);
		loadComment (holder, position);

	}

	private void loadComment (CommentsViewHolder holder, int position) {
		holder.comment.setText (Html.fromHtml (mComments.get (position).text));
		holder.name.setText (
				Html.fromHtml (mComments.get (position).user.name) + " -  @"
						+ Html.fromHtml (mComments.get (position).user.username));
		holder.headline.setText (Html.fromHtml (mComments.get (position).user.headline));
		Picasso.with (mContext).load (mComments.get (position).user.largeImgUrl)
				.resize (Utils.dpToPx (56), Utils.dpToPx (56))
				.centerCrop ()
				.transform (new CircleTransform ())
				.into (holder.avatar);
	}

	public static class CommentsViewHolder extends RecyclerView.ViewHolder {

		@InjectView (R.id.comment_text)
		TextView comment;
		@InjectView (R.id.comment_avatar)
		ImageView avatar;
		@InjectView (R.id.comment_layout)
		LinearLayout commentLayout;
		@InjectView (R.id.comment_name)
		TextView name;
		@InjectView (R.id.comment_headline)
		TextView headline;

		public CommentsViewHolder (View itemView) {
			super (itemView);
			ButterKnife.inject (this, itemView);
		}
	}
}
