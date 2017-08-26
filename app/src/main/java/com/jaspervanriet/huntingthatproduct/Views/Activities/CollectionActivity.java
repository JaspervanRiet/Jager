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

package com.jaspervanriet.huntingthatproduct.Views.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import com.jaspervanriet.huntingthatproduct.Entities.Collection;
import com.jaspervanriet.huntingthatproduct.Entities.Product;
import com.jaspervanriet.huntingthatproduct.Presenters.ProductPresenterImpl;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.R2;
import com.jaspervanriet.huntingthatproduct.Utils.ViewUtils;
import com.jaspervanriet.huntingthatproduct.Views.Adapters.ProductListAdapter;
import com.jaspervanriet.huntingthatproduct.Views.FeedContextMenu;
import com.jaspervanriet.huntingthatproduct.Views.FeedContextMenuManager;
import com.jaspervanriet.huntingthatproduct.Views.ProductView;
import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CollectionActivity extends AppCompatActivity
		implements ProductListAdapter.OnProductClickListener,
		FeedContextMenu.OnFeedContextMenuItemClickListener,
		ProductView {

	public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
	private static final int ANIM_LAYOUT_INTRO_DURATION = 250;

	@BindView (R2.id.toolbar)
	Toolbar mToolBar;
	@BindView (R2.id.collection_layout)
	FrameLayout mCollectionLayout;
	@BindView (android.R.id.list)
	RecyclerView mRecyclerView;
	@BindView (R2.id.list_progress_wheel)
	ProgressWheel mProgressWheel;
	@BindView (R2.id.products_empty_view)
	LinearLayout mEmptyView;

	private int mDrawingStartLocation;
	private ProductPresenterImpl mPresenter;
	private boolean mBackPressed = false;
	private Collection mCollection;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_collection);
		ButterKnife.bind (this);
		mCollection = getIntent ().getParcelableExtra ("collection");
		setupToolBar ();
		expandAnimation (savedInstanceState);

		mProgressWheel.setBarColor (getResources ().getColor (R.color.primary_accent));
		mPresenter = new ProductPresenterImpl (this, mCollection.getId ());
		mPresenter.onActivityCreated (savedInstanceState);
	}

	@Override
	public void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState (outState);
		mPresenter.onSaveInstanceState (outState);
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

	private void setupToolBar () {
		setSupportActionBar (mToolBar);
		ActionBar actionBar = getSupportActionBar ();
		if (actionBar != null) {
			actionBar.setTitle (mCollection.getName ());
			actionBar.setElevation (5);
			actionBar.setDisplayHomeAsUpEnabled (true);
		}
	}

	private LinearLayoutManager getLayoutManager () {
		LinearLayoutManager layoutManager = new LinearLayoutManager (this);
		layoutManager.setOrientation (LinearLayoutManager.VERTICAL);
		return layoutManager;
	}

	@Override
	public void onImageClick (View v, int position) {
		mPresenter.onImageClick (v, position);
	}

	@Override
	public void onCommentsClick (View v, Product product) {
		mPresenter.onCommentsClick (v, product);
	}

	@Override
	public void onContextClick (View v, int position) {
		FeedContextMenuManager.getInstance ().toggleContextMenuFromView (v,
				position, this, false);
	}

	@Override
	public void onShareClick (int feedItem) {
		mPresenter.onShareClick (feedItem);
	}

	@Override
	public void onCancelClick (int feedItem) {
		hideContextMenu ();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater ().inflate (R.menu.collection_menu, menu);
		MenuItem item = menu.findItem (R.id.menu_collection_share);
		ShareActionProvider shareActionProvider = new ShareActionProvider (this);
		shareActionProvider.setShareIntent (getShareIntent ());
		MenuItemCompat.setActionProvider (item, shareActionProvider);
		return true;
	}

	private Intent getShareIntent () {
		Intent i = new Intent (Intent.ACTION_SEND);
		i.setType ("text/plain");
		i.putExtra (Intent.EXTRA_SUBJECT, mCollection.getTitle ());
		i.putExtra (Intent.EXTRA_TEXT, mCollection.getCollectionUrl ());
		return i;
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

	@Override
	public void initializeRecyclerView () {
		mRecyclerView.setHasFixedSize (true);
		mRecyclerView.setItemAnimator (new DefaultItemAnimator ());
		mRecyclerView.setLayoutManager (getLayoutManager ());
		mRecyclerView.setOnScrollListener (new RecyclerView.OnScrollListener () {
			@Override
			public void onScrolled (RecyclerView recyclerView, int dx, int dy) {
				FeedContextMenuManager.getInstance ()
						.onScrolled (recyclerView, dx, dy);
			}
		});
	}

	@Override
	public void setAdapterForRecyclerView (ProductListAdapter adapter) {
		mRecyclerView.setAdapter (adapter);
	}

	@Override
	public void showRefreshingIndicator () {
		mProgressWheel.setVisibility (View.VISIBLE);
		mProgressWheel.spin ();
	}

	@Override
	public void hideRefreshingIndicator () {
		mProgressWheel.stopSpinning ();
		mProgressWheel.setVisibility (View.INVISIBLE);

	}

	@Override
	public void showEmptyView () {
		mEmptyView.setVisibility (View.VISIBLE);
	}

	@Override
	public void hideEmptyView () {
		mEmptyView.setVisibility (View.GONE);
	}

	@Override
	public void showNoNetworkError () {
		Toast.makeText (this, getResources ().getString (R.string.error_connection),
				Toast.LENGTH_SHORT).show ();

	}

	@Override
	public void showContextMenu (View v, int position) {
		FeedContextMenuManager.getInstance ().toggleContextMenuFromView (v,
				position, this, true);
	}

	@Override
	public void hideContextMenu () {
		FeedContextMenuManager.getInstance ().hideContextMenu ();
	}

	@Override
	public void hideActivityTransition () {
		overridePendingTransition (0, 0);
	}

	@Override
	public void setToolbarTitle (String title) {
	}

	@Override
	public int getActivity () {
		return ProductPresenterImpl.ACTIVITY_COLLECTION;
	}

	@Override
	public Context getContext () {
		return this;
	}

	@Override
	public ProductListAdapter.OnProductClickListener getProductClickListener () {
		return this;
	}
}
