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
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.jaspervanriet.huntingthatproduct.Classes.Collection;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CollectionListAdapter extends RecyclerView
		.Adapter<CollectionListAdapter.CollectionsViewHolder> implements
		View.OnClickListener {

	private final static int ANIM_LIST_ENTER_DURATION = 700;
	private static final int ANIMATED_ITEMS_COUNT = 5;

	private Context mContext;
	private ArrayList<Collection> mCollections;
	private OnCollectionClickListener mOnCollectionClickListener;
	private int lastAnimatedPosition = -1;

	public CollectionListAdapter (Context context,
	                              ArrayList<Collection> collections) {
		this.mContext = context;
		this.mCollections = collections;
	}

	@Override
	public CollectionsViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from (parent.getContext ()).inflate (R
				.layout.item_collection, parent, false);
		CollectionsViewHolder holder = new CollectionsViewHolder (itemView);
		holder.card.setOnClickListener (this);
		return holder;
	}

	@Override
	public void onBindViewHolder (CollectionsViewHolder holder, int position) {
		runEnterAnimation (holder.itemView, position);
		holder.card.setTag (position);
		holder.name.setText (mCollections.get (position).name);
		holder.title.setText (mCollections.get (position).title);
		String backgroundImageUrl = mCollections.get (position)
				.backgroundImageUrl;
		if (!backgroundImageUrl.equals ("")) {
			Picasso.with (mContext)
					.load (mCollections.get (position).backgroundImageUrl)
					.resize (380, 150)
					.centerCrop ()
					.into (holder.image);
		}
	}

	@Override
	public int getItemCount () {
		return mCollections.size ();
	}

	private void runEnterAnimation (View view, int position) {
		if (position >= ANIMATED_ITEMS_COUNT - 1) {
			return;
		}
		if (position > lastAnimatedPosition) {
			lastAnimatedPosition = position;
			view.setTranslationY (Utils.getScreenHeight (mContext));
			view.animate ()
					.translationY (0)
					.setInterpolator (new DecelerateInterpolator (3.f))
					.setDuration (ANIM_LIST_ENTER_DURATION)
					.start ();
		}
	}

	@Override
	public void onClick (View v) {
		mOnCollectionClickListener.onCollectionClick (v, (Integer) v.getTag ());
	}

	public void setOnCollectionClickListener (OnCollectionClickListener
			                                          onCollectionClickListener) {
		this.mOnCollectionClickListener = onCollectionClickListener;
	}

	public static class CollectionsViewHolder extends RecyclerView.ViewHolder {

		@InjectView (R.id.card_collection)
		CardView card;
		@InjectView (R.id.card_collection_image)
		ImageView image;
		@InjectView (R.id.card_collection_name)
		RobotoTextView name;
		@InjectView (R.id.card_collection_title)
		RobotoTextView title;

		public CollectionsViewHolder (View itemView) {
			super (itemView);
			ButterKnife.inject (this, itemView);
		}
	}

	public interface OnCollectionClickListener {
		public void onCollectionClick (View view, int position);
	}
}
