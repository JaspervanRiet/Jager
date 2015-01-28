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

package com.jaspervanriet.huntingthatproduct.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jaspervanriet.huntingthatproduct.Adapters.CommentListAdapter;
import com.jaspervanriet.huntingthatproduct.Classes.Comment;
import com.jaspervanriet.huntingthatproduct.Classes.Product;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Utils.Constants;
import com.jaspervanriet.huntingthatproduct.Utils.Utils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class CommentsActivity extends ActionBarActivity {

	public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
	private static final int ANIM_LAYOUT_INTRO_DURATION = 250;

	@InjectView (R.id.toolbar)
	Toolbar mToolBar;
	@InjectView (android.R.id.list)
	RecyclerView mRecyclerView;
	@InjectView (R.id.comments_layout)
	FrameLayout mCommentsLayout;
	@InjectView (R.id.comments_list_progress_wheel)
	ProgressWheel mListProgressWheel;
	@InjectView (R.id.comments_empty_view)
	LinearLayout mEmptyView;
	@InjectView (R.id.comments_empty_text)
	TextView mEmptyTextView;

	private CommentListAdapter mCommentListAdapter;
	private int mDrawingStartLocation;
	private ArrayList<Comment> mComments;
	private Product mProduct;
	private boolean mBackPressed = false;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_comments);
		ButterKnife.inject (this);

		mProduct = getIntent ().getParcelableExtra ("product");
		setupToolBar ();
		mComments = new ArrayList<> ();
		expandAnimation (savedInstanceState);
		mCommentListAdapter = new CommentListAdapter (this, mComments);
		setupRecyclerView ();
		completeRefresh ();
	}

	@Override
	public void onBackPressed () {
		if (!mBackPressed) {
			goBack ();
		}
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		int itemId = item.getItemId ();

		if (itemId == android.R.id.home) {
			if (!mBackPressed) {
				goBack ();
			}
			return true;
		}
		return false;
	}

	private void goBack () {
		mBackPressed = true;
		mCommentsLayout.animate ()
				.translationY (Utils.getScreenHeight (this))
				.setDuration (200)
				.setListener (new AnimatorListenerAdapter () {
					@Override
					public void onAnimationEnd (Animator animation) {
						CommentsActivity.super.onBackPressed ();
						overridePendingTransition (0, 0);
						finish ();
					}
				})
				.start ();
	}

	private void completeRefresh () {
		if (mComments.size () != 0) {
			mComments.clear ();
			mCommentListAdapter.notifyDataSetChanged ();
		}
		mListProgressWheel.setVisibility (View.VISIBLE);
		mListProgressWheel.spin ();
		getComments ();
	}

	private void getComments () {
		Ion.with (this).load (Constants.API_URL + "posts/" + mProduct.id)
				.setHeader ("Authorization", "Bearer " + Constants.CLIENT_TOKEN)
				.asJsonObject ()
				.setCallback (new FutureCallback<JsonObject> () {
					@Override
					public void onCompleted (Exception e, JsonObject result) {
						if (result == null || !result.has ("post") || e != null) {
							return;
						}
						result = result.get ("post").getAsJsonObject ();
						if (result.has ("comments")) {
							int i;
							JsonArray comments = result.getAsJsonArray ("comments");
							for (i = 0; i < comments.size (); i++) {
								JsonObject object = comments.get (i).getAsJsonObject ();
								processComment (object, 0);
							}
							mCommentListAdapter.notifyDataSetChanged ();
							mListProgressWheel.setVisibility (View.GONE);
							mListProgressWheel.stopSpinning ();
							checkEmpty ();
						}

					}
				});
	}

	private void processComment (JsonObject object, int level) {
		Comment comment = new Comment (object);
		comment.level = level;
		mComments.add (comment);
		if (!object.get ("child_comments").isJsonNull ()) {
			int i;
			level++;
			for (i = 0; i < comment.childCommentCount; i++) {
				processComment (object
						.getAsJsonArray ("child_comments")
						.get (i)
						.getAsJsonObject (), level);
			}
		}

	}

	private void expandAnimation (Bundle savedInstanceState) {
		mDrawingStartLocation = getIntent ().getIntExtra (ARG_DRAWING_START_LOCATION, 0);
		if (savedInstanceState == null) {
			mCommentsLayout.getViewTreeObserver ().addOnPreDrawListener (new ViewTreeObserver
					.OnPreDrawListener () {
				@Override
				public boolean onPreDraw () {
					mCommentsLayout.getViewTreeObserver ().removeOnPreDrawListener (this);
					startIntroAnimation ();
					return true;
				}
			});
		}
	}

	private void startIntroAnimation () {
		mCommentsLayout.setScaleY (0.1f);
		mCommentsLayout.setPivotY (mDrawingStartLocation);
		mCommentsLayout.animate ()
				.scaleY (1)
				.setDuration (ANIM_LAYOUT_INTRO_DURATION)
				.setInterpolator (new AccelerateInterpolator ())
				.start ();
	}

	private void setupToolBar () {
		setSupportActionBar (mToolBar);
		ActionBar actionBar = getSupportActionBar ();
		actionBar.setTitle (mProduct.title);
		actionBar.setElevation (5);
		actionBar.setDisplayHomeAsUpEnabled (true);
	}

	private void setupRecyclerView () {
		mRecyclerView.setHasFixedSize (true);
		mRecyclerView.setItemAnimator (new DefaultItemAnimator ());
		mRecyclerView.setLayoutManager (getLayoutManager ());
		mRecyclerView.setAdapter (mCommentListAdapter);
		mEmptyTextView.setTypeface (
				Typeface.createFromAsset (getAssets (), "fonts/Roboto-Light.ttf"));
	}

	private void checkEmpty () {
		if (mCommentListAdapter.getItemCount () == 0) {
			mEmptyView.setVisibility (View.VISIBLE);
		} else {
			mEmptyView.setVisibility (View.GONE);
		}
	}

	private LinearLayoutManager getLayoutManager () {
		LinearLayoutManager layoutManager = new LinearLayoutManager (this);
		layoutManager.setOrientation (LinearLayoutManager.VERTICAL);
		return layoutManager;
	}

}
