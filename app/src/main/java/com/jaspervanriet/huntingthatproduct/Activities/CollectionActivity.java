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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jaspervanriet.huntingthatproduct.Adapters.ProductListAdapter;
import com.jaspervanriet.huntingthatproduct.Entities.Collection;
import com.jaspervanriet.huntingthatproduct.Entities.Product;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Utils.Constants;
import com.jaspervanriet.huntingthatproduct.Utils.ViewUtils;
import com.jaspervanriet.huntingthatproduct.Views.FeedContextMenu;
import com.jaspervanriet.huntingthatproduct.Views.FeedContextMenuManager;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CollectionActivity extends ActionBarActivity
		implements ProductListAdapter.OnProductClickListener,
				   FeedContextMenu.OnFeedContextMenuItemClickListener {

	public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
	private static final int ANIM_LAYOUT_INTRO_DURATION = 250;

	@InjectView (R.id.toolbar)
	Toolbar mToolBar;
	@InjectView (R.id.collection_layout)
	FrameLayout mCollectionLayout;
	@InjectView (android.R.id.list)
	RecyclerView mRecyclerView;
	@InjectView (R.id.list_progress_wheel)
	ProgressWheel mProgressWheel;
	@InjectView (R.id.products_empty_view)
	LinearLayout mEmptyView;

	private int mDrawingStartLocation;
	private boolean mBackPressed = false;
	private Collection mCollection;
	private ArrayList<Product> mProducts = new ArrayList<> ();
	private ProductListAdapter mListAdapter;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_collection);
		ButterKnife.inject (this);

		mCollection = getIntent ().getParcelableExtra ("collection");
		setupToolBar ();
		expandAnimation (savedInstanceState);

		mProgressWheel.setBarColor (getResources ().getColor (R.color.primary_accent));
		mListAdapter = new ProductListAdapter (this, mProducts);
		mListAdapter.setOnProductClickListener (this);
		setupRecyclerView ();
		completeRefresh ();
	}

	private Intent getShareIntent () {
		Intent i = new Intent (Intent.ACTION_SEND);
		i.setType ("text/plain");
		i.putExtra (Intent.EXTRA_SUBJECT, mCollection.title);
		i.putExtra (Intent.EXTRA_TEXT, mCollection.collectionUrl);
		return i;
	}

	private void goBack () {
		mBackPressed = true;
		mCollectionLayout.animate ()
				.translationY (ViewUtils.getScreenHeight (this))
				.setDuration (200)
				.setListener (new AnimatorListenerAdapter () {
					@Override
					public void onAnimationEnd (Animator animation) {
						CollectionActivity.super.onBackPressed ();
						overridePendingTransition (0, 0);
						finish ();
					}
				})
				.start ();
	}

	private void expandAnimation (Bundle savedInstanceState) {
		mDrawingStartLocation = getIntent ().getIntExtra (ARG_DRAWING_START_LOCATION, 0);
		if (savedInstanceState == null) {
			mCollectionLayout.getViewTreeObserver ().addOnPreDrawListener (new ViewTreeObserver
					.OnPreDrawListener () {
				@Override
				public boolean onPreDraw () {
					mCollectionLayout.getViewTreeObserver ().removeOnPreDrawListener (this);
					startIntroAnimation ();
					return true;
				}
			});
		}
	}

	private void startIntroAnimation () {
		mCollectionLayout.setScaleY (0.1f);
		mCollectionLayout.setPivotY (mDrawingStartLocation);
		mCollectionLayout.animate ()
				.scaleY (1)
				.setDuration (ANIM_LAYOUT_INTRO_DURATION)
				.setInterpolator (new AccelerateInterpolator ())
				.start ();
	}

	private void completeRefresh () {
		if (mProducts.size () != 0) {
			mProducts.clear ();
			mListAdapter.notifyDataSetChanged ();
		}
		mProgressWheel.setVisibility (View.VISIBLE);
		mProgressWheel.spin ();
		getProducts ();
	}

	private void setupRecyclerView () {
		mRecyclerView.setHasFixedSize (true);
		mRecyclerView.setItemAnimator (new DefaultItemAnimator ());
		mRecyclerView.setLayoutManager (getLayoutManager ());
		mRecyclerView.setAdapter (mListAdapter);
		mRecyclerView.setOnScrollListener (new RecyclerView.OnScrollListener () {
			@Override
			public void onScrolled (RecyclerView recyclerView, int dx, int dy) {
				FeedContextMenuManager.getInstance ()
						.onScrolled (recyclerView, dx, dy);
			}
		});
	}

	// Retrieves content and adds it to mProducts
	private void getProducts () {
		Ion.with (this).load (Constants.API_URL + "collections/" +
				mCollection.id)
				.setHeader ("Authorization", "Bearer " + Constants.CLIENT_TOKEN)
				.asJsonObject ()
				.setCallback (new FutureCallback<JsonObject> () {
					@Override
					public void onCompleted (Exception e, JsonObject result) {
						if (e != null && e instanceof TimeoutException) {
							Toast.makeText (CollectionActivity.this,
									getResources ().getString
											(R.string.error_connection),
									Toast.LENGTH_SHORT).show ();
							return;
						}
						if (result != null && result.has ("collection")) {
							int i;
							result = result.getAsJsonObject ("collection");
							JsonArray products = result
									.getAsJsonArray ("posts");
							for (i = 0; i < products.size (); i++) {
								JsonObject obj = products.get (i).getAsJsonObject ();
								try {
									Product product = new Product (obj);
									mProducts.add (product);
								}
								catch (UnsupportedOperationException
										unsupportedException) {
									Crashlytics.logException (unsupportedException);
								}
							}
							mListAdapter.notifyDataSetChanged ();
							mProgressWheel.stopSpinning ();
							mProgressWheel.setVisibility (View.INVISIBLE);
							checkEmpty ();
						}
					}
				});
	}

	private void checkEmpty () {
		if (mListAdapter.getItemCount () == 0) {
			mEmptyView.setVisibility (View.VISIBLE);
		} else {
			mEmptyView.setVisibility (View.GONE);
		}
	}

	private void setupToolBar () {
		setSupportActionBar (mToolBar);
		ActionBar actionBar = getSupportActionBar ();
		actionBar.setTitle (mCollection.name);
		actionBar.setElevation (5);
		actionBar.setDisplayHomeAsUpEnabled (true);
	}

	private LinearLayoutManager getLayoutManager () {
		LinearLayoutManager layoutManager = new LinearLayoutManager (this);
		layoutManager.setOrientation (LinearLayoutManager.VERTICAL);
		return layoutManager;
	}

	private void activityExitAnimation (View v, Product product, Intent i) {
		int[] startingLocation = new int[2];
		v.getLocationOnScreen (startingLocation);
		i.putExtra (CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
		i.putExtra ("productId", product.getId ());
		i.putExtra ("collection", true);
		i.putExtra ("productTitle", product.getTitle ());
		i.putExtra ("productUrl", product.getProductUrl ());
		startActivity (i);
		overridePendingTransition (0, 0);
	}

	/*
	 * Adapter onClickListeners
	 */

	@Override
	public void onImageClick (View v, int position) {
		Product product = mProducts.get (position);
		Intent openUrl = new Intent (this, WebActivity.class);
		activityExitAnimation (v, product, openUrl);
	}

	@Override
	public void onCommentsClick (View v, Product product) {
		Intent i = new Intent (this, CommentsActivity.class);
		activityExitAnimation (v, product, i);
	}

	@Override
	public void onContextClick (View v, int position) {
		FeedContextMenuManager.getInstance ().toggleContextMenuFromView (v,
				position, this, false);
	}

	@Override
	public void onShareClick (int feedItem) {
		Product product = mProducts.get (feedItem);
		Intent share = new Intent (android.content.Intent.ACTION_SEND);
		share.setType ("text/plain");
		share.putExtra (Intent.EXTRA_SUBJECT, product.getTitle ());
		share.putExtra (Intent.EXTRA_TEXT, product.getProductUrl ());
		startActivity (Intent.createChooser (share, "Share product"));
		FeedContextMenuManager.getInstance ().hideContextMenu ();
	}

	@Override
	public void onCancelClick (int feedItem) {
		FeedContextMenuManager.getInstance ().hideContextMenu ();
	}

	/*
	 * UI boilerplate
	 */

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater ().inflate (R.menu.collection_menu, menu);
		MenuItem item = menu.findItem (R.id.menu_collection_share);
		ShareActionProvider shareActionProvider = new ShareActionProvider (this);
		shareActionProvider.setShareIntent (getShareIntent ());
		MenuItemCompat.setActionProvider (item, shareActionProvider);
		return true;
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
}
