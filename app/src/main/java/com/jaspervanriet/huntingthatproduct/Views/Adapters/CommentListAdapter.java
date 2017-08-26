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

package com.jaspervanriet.huntingthatproduct.Views.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaspervanriet.huntingthatproduct.Entities.Comment;
import com.jaspervanriet.huntingthatproduct.Entities.User;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.R2;
import com.jaspervanriet.huntingthatproduct.Utils.CircleTransform;
import com.jaspervanriet.huntingthatproduct.Utils.ViewUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter
		.CommentsViewHolder> {

	private List<Comment> mComments;
	private Context mContext;

	public CommentListAdapter (Context context, List<Comment> comments) {
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
		params.setMargins (comment.getLevel () * 30, 0, 0, 0);
		holder.commentLayout.setLayoutParams (params);
		loadComment (holder, position);
	}

	private void loadComment (CommentsViewHolder holder, int position) {
		Comment comment = mComments.get (position);
		User user = comment.getUser ();
		holder.comment.setText (Html.fromHtml (comment.getBody ()));
		holder.name.setText (Html.fromHtml (user.getName ()) + " -  @"
				+ Html.fromHtml (user.getUsername ()));
		if (comment.isMaker ()) {
			holder.name.setTextColor (mContext.getResources ()
					.getColor (R.color.text_indicator_maker));
		} else {
			holder.name.setTextColor (mContext.getResources ().getColor
					(R.color.text_default));
		}
		if (user.getHeadline () != null) {
			holder.headline.setText (Html.fromHtml (user.getHeadline ()));
		}
		Picasso.with (mContext).load (user.getImageUrl ().getLargeImgUrl ())
				.resize (ViewUtils.dpToPx (56), ViewUtils.dpToPx (56))
				.centerCrop ()
				.transform (new CircleTransform ())
				.into (holder.avatar);
	}

	static class CommentsViewHolder extends RecyclerView.ViewHolder {

		@BindView (R2.id.comment_text)
		TextView comment;
		@BindView (R2.id.comment_avatar)
		ImageView avatar;
		@BindView (R2.id.comment_layout)
		LinearLayout commentLayout;
		@BindView (R2.id.comment_name)
		TextView name;
		@BindView (R2.id.comment_headline)
		TextView headline;

		public CommentsViewHolder (View itemView) {
			super (itemView);
			ButterKnife.bind (this, itemView);
		}
	}
}
